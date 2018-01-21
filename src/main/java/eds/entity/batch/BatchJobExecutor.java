/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import eds.component.DBService;
import eds.component.batch.BatchJobTransitionService;
import eds.component.batch.BatchProcessingException;
import static eds.entity.batch.BATCH_JOB_RUN_STATUS.CANCELLED;
import static eds.entity.batch.BATCH_JOB_RUN_STATUS.COMPLETED;
import static eds.entity.batch.BATCH_JOB_RUN_STATUS.IN_PROCESS;
import eds.entity.batch.run.BatchJobRunScheduled;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.joda.time.DateTime;
import seca2.component.landing.LandingService;

/**
 *
 * @author LeeKiatHaw
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/BatchJobContainer"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class BatchJobExecutor extends DBService implements MessageListener {
    
    public static final String BATCH_RUN_KEY_PARAM = "BATCH_RUN_KEY";
    
    final CronType STANDARD_CRON_TYPE = CronType.UNIX;
    
    @EJB LandingService landingService;
    @EJB BatchJobExecutorHelper helper;
    @EJB BatchJobTransitionService bjTransService;
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<BatchJobStep> loadBatchJobSteps(long batchJobId) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<BatchJobStep> query = builder.createQuery(BatchJobStep.class);
        Root<BatchJobStep> fromSteps = query.from(BatchJobStep.class);
        
        query.select(fromSteps);
        query.where(builder.equal(fromSteps.get(BatchJobStep_.BATCH_JOB), batchJobId));
        
        List<BatchJobStep> results = em.createQuery(query)
                .getResultList();
        
        return results;
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<BatchJobCondition> loadBatchJobConditions(long batchJobId) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<BatchJobCondition> query = builder.createQuery(BatchJobCondition.class);
        Root<BatchJobCondition> fromTrigger = query.from(BatchJobCondition.class);
        
        query.select(fromTrigger);
        query.where(builder.equal(fromTrigger.get(BatchJobCondition_.BATCH_JOB), batchJobId));
        
        List<BatchJobCondition> results = em.createQuery(query)
                .getResultList();
        
        return results;
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<BatchJobSchedule> loadBatchJobSchedules(long batchJobId) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<BatchJobSchedule> query = builder.createQuery(BatchJobSchedule.class);
        Root<BatchJobSchedule> fromTrigger = query.from(BatchJobSchedule.class);
        
        query.select(fromTrigger);
        query.where(builder.equal(fromTrigger.get(BatchJobSchedule_.BATCH_JOB), batchJobId));
        
        List<BatchJobSchedule> results = em.createQuery(query)
                .getResultList();
        
        return results;
    }
    
    /**
     * Helper method.
     * 
     * @param cronExpression
     * @param now
     * @param cronType
     * @return 
     */
    public DateTime getNextExecutionTimeCron(String cronExpression, DateTime now, CronType cronType){
        //Get the next execution time
        ExecutionTime executionTime = ExecutionTime.forCron(getValidCronExp(cronExpression,cronType));
        DateTime nextExecution = executionTime.nextExecution(now);
        
        return nextExecution;
    }
    
    public Cron getValidCronExp(String cronExp,CronType cronType) {
        CronDefinition cronDef = CronDefinitionBuilder.instanceDefinitionFor(cronType);
        CronParser parser = new CronParser(cronDef);
        return parser.parse(cronExp).validate();
        
    }

    @Override
    public void onMessage(Message message) {
        try {
            String runKey = message.getStringProperty(BATCH_RUN_KEY_PARAM);
            
            /**
             * 3 Steps to process a batch job: 
             * 1) Lock - updates the batch job run status to IN_PROCESS so that 
             * BatchProcessingService will not pick it up again.
             * 2) Execute - instantiates the POJO java class (usually an EJB) and 
             * invokes the selected method. 
             * 3) Schedule next run - execute all BatchJobConditions and decide
             * if the next run should be scheduled.
             * 
             * All steps should not have any transaction associated - beca
             * 
            */
            BatchJobRun run = lockForExecution(runKey); // so that no other cron instance would pick up this run
            run = executeRun(run);
            BatchJobRun nextRun = scheduleNextRun(runKey, DateTime.now());
            
        } catch (JMSException ex) {
            Logger.getLogger(BatchJobExecutor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BatchProcessingException ex) {
            Logger.getLogger(BatchJobExecutor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Changes the run status to IN_PROCESS.
     * BatchProcessingService will not be able to pick up this job but users 
     * can still change the status to CANCELLED.
     * 
     * @param runKey
     * @return
     * @throws BatchProcessingException 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public BatchJobRun lockForExecution(String runKey) throws BatchProcessingException {
        BatchJobRun run = em.find(BatchJobRun.class, runKey);
        
        // This means that batch job was picked up twice by BatchJobProcessingService
        // We should lockForExecution the batch job the moment it is sent to the queue, but this is a temporary
        // arrangement to validate the root cause.
        if(!BATCH_JOB_RUN_STATUS.QUEUED.equals(run.STATUS())) {
            throw new BatchProcessingException("Issue #177 Batch job failure. Run key "
                    +run.getRUN_KEY()+" has already been queued by BatchProcessingService before sent to "
                    + "the JMS queue twice.");
        }
        
        run = bjTransService.transit(run, IN_PROCESS, DateTime.now());
        
        return run;
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public BatchJobRun executeRun(BatchJobRun run) {
        try {
            List<BatchJobStep> runSteps = loadBatchJobSteps(run.getBATCH_JOB().getBATCH_JOB_ID());
            
            for (BatchJobStep step : runSteps) {
            
                Object ret = step.execute();
                //Create log entry
                BatchJobRunLog log = new BatchJobRunLog();
                log.setBATCH_JOB_RUN(run);
                log.setSTEP_ORDER(step.getSTEP_ORDER());
                log.setTIME(new java.sql.Timestamp(DateTime.now().getMillis()));
                log = helper.insertLog(log);
            }
            //Update the job with COMPLETED status
            run = bjTransService.transit(run, COMPLETED, DateTime.now());
        } catch (Exception ex) { //Expected exceptions
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            //Update the job with ERROR status
            run = helper.logErrors(run,ex); 
        }
        
        return run;
    }
    
    /**
     * If run.getSTATUS() is CANCELLED, return the current run and do not 
     * schedule the next run.
     * 
     * @param run
     * @param triggerTime
     * @return
     * @throws BatchProcessingException 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public BatchJobRun scheduleNextRun(String runKey, DateTime triggerTime) throws BatchProcessingException {
        // Check if the status has been updated to CANCELLED
        // by reading it again
        BatchJobRun run = em.find(BatchJobRun.class, runKey);
        if(run == null || CANCELLED.equals(BATCH_JOB_RUN_STATUS.valueOf(run.getSTATUS()))) {
            return run;
        }

        //Get the schedules and conditions
        List<BatchJobCondition> conditionList = loadBatchJobConditions(run.getBATCH_JOB().getBATCH_JOB_ID());
        List<BatchJobSchedule> scheduleList = loadBatchJobSchedules(run.getBATCH_JOB().getBATCH_JOB_ID());
        
        if(conditionList != null && !conditionList.isEmpty()) {
            //Need to call this in a new transaction
            if(!helper.checkConditions(conditionList))
                return run;
        }
        DateTime nextExecution = triggerTime;
        
        if(scheduleList != null && !scheduleList.isEmpty()) {
            //Assume that there will only be 1 schedule
            BatchJobSchedule schedule = scheduleList.get(0);
            String cronExpression = schedule.getCRON_EXPRESSION();
            nextExecution = getNextExecutionTimeCron(
                    cronExpression, 
                    triggerTime, 
                    STANDARD_CRON_TYPE);
        }
        //Create new run
        BatchJobRun nextRun = new BatchJobRunScheduled();
        nextRun.setBATCH_JOB(run.getBATCH_JOB());
        nextRun.setSERVER_NAME(run.getBATCH_JOB().getSERVER_NAME());
        nextRun.schedule(nextExecution);
        nextRun = helper.insertRun(nextRun);
        
        return nextRun;
    }
}
