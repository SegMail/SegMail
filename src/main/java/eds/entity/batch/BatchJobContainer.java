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
import eds.component.batch.BatchExecutionService;
import eds.component.batch.BatchProcessingException;
import eds.component.data.DataValidationException;
import eds.component.data.IncompleteDataException;
import static eds.entity.batch.BATCH_JOB_STATUS.ACTIVE;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.joda.time.DateTime;
import seca2.component.landing.LandingServerGenerationStrategy;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;
import seca2.entity.landing.ServerInstance;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class BatchJobContainer extends DBService {
    
    final CronType STANDARD_CRON_TYPE = CronType.UNIX;
    
    private BatchJob job;
    
    //Static data
    private List<BatchJobStep> steps;
    private List<BatchJobCondition> conditions;
    private List<BatchJobSchedule> schedules;
    
    //Instances
    //private List<BatchJobRun> runs; //I see no use for this at the moment
    
    //Exceptions
    /**
     * This is a potential use for CompletableFuture
     */
    private Exception ex;   
    
    @EJB LandingService landingService;
    @EJB BatchJobContainerHelper helper;
    
    public void clear() {
        job = null;
        steps = new ArrayList<>();
        conditions = new ArrayList<>();
        schedules = new ArrayList<>();
        //runs = new ArrayList<>();
    }
    
    public BatchJobContainer read(long batchJobId) {
        clear();
        job = em.find(BatchJob.class, batchJobId);
        steps = loadBatchJobSteps(batchJobId);
        conditions = loadBatchJobConditions(batchJobId);
        schedules = loadBatchJobSchedules(batchJobId);
        
        //Don't load all runs, as it might get overloaded
        
        return this;
    }
    
    public BatchJobContainer read(String runKey) {
        clear();
        //Don't load all runs, as it might get overloaded
        List<BatchJobRun> runs = getJobRunsByKey(runKey);
        if(runs == null || runs.isEmpty()) {
            return this;
        }
        job = runs.get(0).getBATCH_JOB();
        steps = loadBatchJobSteps(job.getBATCH_JOB_ID());
        conditions = loadBatchJobConditions(job.getBATCH_JOB_ID());
        schedules = loadBatchJobSchedules(job.getBATCH_JOB_ID());
        
        return this;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobContainer create(String name) throws IncompleteDataException {
        clear();
        ServerInstance server = landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN, ServerNodeType.ERP);
        
        job = new BatchJob();
        job.setSTATUS(ACTIVE.label);
        job.setBATCH_JOB_NAME(name);
        job.setSERVER_NAME(server.getNAME());
        
        em.persist(job);
        
        return this;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobContainer addStep(String serviceName, String serviceMethod, Object[] params) 
            throws IOException {
        
        BatchJobStep step = new BatchJobStep();
        step.setBATCH_JOB(job);
        step.setSERVICE_NAME(serviceName);
        step.setSERVICE_METHOD(serviceMethod);
        em.persist(step);
        this.steps.add(step);
        
        //Create params
        for (int i = 0; params != null && i < params.length; i++) {

            BatchJobStepParam newParam = new BatchJobStepParam();
            newParam.setPARAM_ORDER(i);
            newParam.setBATCH_JOB_STEP(step);
            
            em.persist(newParam);
            step.addPARAMS(newParam);

            Object obj = params[i];
            Class clazz = obj.getClass();
            if (Serializable.class.isAssignableFrom(clazz)) {
                Serializable s = (Serializable) obj;
                newParam.setSERIALIZED_OBJECT(s);
                continue;
            }
            newParam.setSTRING_VALUE(obj.toString());
        }
        
        return this;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobContainer addCondition(String serviceName, String serviceMethod, Object[] params) throws IOException {
        
        BatchJobCondition condition = new BatchJobCondition();
        condition.setBATCH_JOB(job);
        condition.setSERVICE_NAME(serviceName);
        condition.setSERVICE_METHOD(serviceMethod);
        em.persist(condition);
        conditions.add(condition);
        
        //Create params
        for (int i = 0; params != null && i < params.length; i++) {

            BatchJobConditionParam newParam = new BatchJobConditionParam();
            newParam.setPARAM_ORDER(i);
            newParam.setBATCH_JOB_CONDITION(condition);
            em.persist(newParam);

            condition.addPARAMS(newParam);

            Object obj = params[i];
            Class clazz = obj.getClass();
            if (Serializable.class.isAssignableFrom(clazz)) {
                Serializable s = (Serializable) obj;
                newParam.setSERIALIZED_OBJECT(s);
                continue;
            }
            newParam.setSTRING_VALUE(obj.toString());
        }
        
        return this;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobContainer setSchedule(String cronExpression) throws DataValidationException {
        
        BatchJobSchedule schedule = new BatchJobSchedule();
        schedule.setBATCH_JOB(job);
        schedule.setCRON_EXPRESSION(cronExpression);
        schedule.setTRIGGER_STATUS(BATCH_JOB_TRIGGER_STATUS.ACTIVE.label);
        
        //Each BatchJob should only have 1 schedule
        if(schedules != null) {
            for(BatchJobSchedule existingSchedule : schedules) {
                em.remove(existingSchedule);
            }
        }
        em.persist(schedule);
        schedules.add(schedule);
        
        return this;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
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
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
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
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
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

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<BatchJobRun> getJobRunsByKey(String key) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<BatchJobRun> query = builder.createQuery(BatchJobRun.class);
        Root<BatchJobRun> fromRun = query.from(BatchJobRun.class);

        query.select(fromRun);
        query.where(builder.equal(fromRun.get(BatchJobRun_.RUN_KEY), key));

        List<BatchJobRun> results = em.createQuery(query)
                .getResultList();
        return results;
    }
    
    
    /**
     * Given a time, trigger the next run by considering the BatchJob's schedule
     * and conditions.
     * <br>
     * <strong>Condition</strong>
     * If there are no conditions available, run it.
     * <br>
     * <strong>Schedule</strong>
     * If there are no schedules available, run it immediately.
     * <br>
     * Note: we have to run this in a transaction context, although there is a 
     * chance that it will timeout, because whoever calls it will call it 
     * in a transaction context together with other methods from this container.
     * Eg. create().setSchedule().schedule(). If this method is non-transactional
     * then it won't be able to "see" the objects created in the preceeding calls.
     * 
     * @param triggerTime
     * @return this instance
     * @throws BatchProcessingException if there is already an existing active run
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobContainer schedule(DateTime triggerTime) throws BatchProcessingException {
        //Get the schedules and conditions
        conditions = loadBatchJobConditions(job.getBATCH_JOB_ID());
        schedules = loadBatchJobSchedules(job.getBATCH_JOB_ID());
        
        if(conditions != null && !conditions.isEmpty()) {
            //Need to call this in a new transaction
            if(!helper.checkConditions(conditions))
                return this;
        }
        DateTime nextExecution = triggerTime;
        
        if(schedules != null && !schedules.isEmpty()) {
            //Assume that there will only be 1 schedule
            BatchJobSchedule schedule = schedules.get(0);
            String cronExpression = schedule.getCRON_EXPRESSION();
            nextExecution = this.getNextExecutionTimeCron(cronExpression, triggerTime.withSecondOfMinute(0), STANDARD_CRON_TYPE);
        }
        //Create new run
        //Check if there are any existing jobs in the active status
        BatchJobRun oneAndOnlyRun = getOneAndOnlyRun();
        oneAndOnlyRun = helper.pushToScheduled(oneAndOnlyRun,nextExecution);
        
        return this;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobContainer cancel(DateTime cancelTime) {
        return this;
    }
    
    /**
     * Executes the underlying batch job and the status updates.
     * <br>
     * Once called, the underlying BatchJobRun must only end in 3 states:
     * <ul>
     * <li><b>COMPLETED</b>: if the batch job successfully completes</li>
     * <li><b>ERROR</b>: if an expected exception occurs within the execution
     * - ie. thrown by the underlying service defined in BatchJobStep</li>
     * <li><b>SCHEDULED</b>: if an unexpected error occurs outside of the execution
     * and the entire transaction has to be rolled back - ie. thrown by this 
     * BatchJobContainer in the update operation of the BatchJobRun itself</li>
     * </ul>
     * Once the operation has started and before it ends, the BatchJobRun status
     * must be in the <b>IN_PROCESS</b>. However, it should not stay in this state
     * once the execution has completed or rolled back.
     * <br>
     * This method must also check for batch job integrity - there should only be
     * one batch job run in a ready and active state.
     * <br>
     * This method should be called in no transaction context, because it is 
     * long running.
     * 
     * @param startTime
     * @return
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void execute(DateTime startTime) {
        BatchJobRun oneAndOnlyRun = null;
        
        try {
            oneAndOnlyRun = getOneAndOnlyRun();
            //Send to an external service to update the status of the job
            //This is the only operation that requires a separate transaction because
            //we don't know when the job will finish executing and while the job is 
            //executing, we don't want other jobs to accidentally pick it up and process
            //it again.
            //Potential use of CompletableFuture here
            helper.pushToStartStatus(oneAndOnlyRun,startTime);
            for (BatchJobStep step : steps) {
            
                Object ret = step.execute();
                //Create log entry
                BatchJobRunLog log = new BatchJobRunLog();
                log.setBATCH_JOB_RUN(oneAndOnlyRun);
                log.setSTEP_ORDER(step.getSTEP_ORDER());
                log.setTIME(new java.sql.Timestamp(DateTime.now().getMillis()));
                helper.insertLog(log);
            }
            //Update the job with COMPLETED status
            helper.pushToCompleted(oneAndOnlyRun,DateTime.now());
            //start scheduling the next job
            schedule(DateTime.now());
            
        } catch (Exception ex) { //Expected exceptions
            Logger.getLogger(BatchExecutionService.class.getName()).log(Level.SEVERE, null, ex);
            //Update the job with ERROR status
            helper.logErrors(oneAndOnlyRun,ex); 
        }
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
        DateTime nextExecution = executionTime.nextExecution(now.minusSeconds(1));//because ExecutionTime will plus 1 sec
        //DateTime lastExecution = executionTime.lastExecution(now);
        
        return nextExecution;
    }
    
    public Cron getValidCronExp(String cronExp,CronType cronType) {
        CronDefinition cronDef = CronDefinitionBuilder.instanceDefinitionFor(cronType);
        CronParser parser = new CronParser(cronDef);
        return parser.parse(cronExp).validate();
        
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public BatchJobRun getOneAndOnlyRun() throws BatchProcessingException {
        //Check if there are any existing jobs in the active status
        //Need to check in this.runs too!!!
        List<BatchJobRun> activeRuns = helper.getLastNBatchRuns(
                job.getBATCH_JOB_ID(),
                BATCH_JOB_RUN_STATUS.getActiveStatuses(),
                1);
        if(activeRuns != null && !activeRuns.isEmpty()) {
            throw new BatchProcessingException("There is an active run of this batch job currently!");
        }
        //Check if there are any existing jobs in the ready status, modify them instead of creating new
        //ones
        List<BatchJobRun> standbyRuns = helper.getLastNBatchRuns(
                job.getBATCH_JOB_ID(),
                BATCH_JOB_RUN_STATUS.getReadyStatuses(),
                1); //We are assuming that there will only be 1 run in a standby status
        
        BatchJobRun oneAndOnlyRun = new BatchJobRun();
        if(standbyRuns != null && !standbyRuns.isEmpty()) {
            oneAndOnlyRun = standbyRuns.get(0);
            oneAndOnlyRun = helper.updateRun(oneAndOnlyRun);
        } else { 
            //Most of the time it won't come to this point because this container
            //is called when there is already a batch job run scheduled
            oneAndOnlyRun.setBATCH_JOB(job);
            oneAndOnlyRun.setSERVER_NAME(job.getSERVER_NAME());
            
            helper.insertRun(oneAndOnlyRun);
            //runs.add(oneAndOnlyRun);
        }
        return oneAndOnlyRun;
    }
}
