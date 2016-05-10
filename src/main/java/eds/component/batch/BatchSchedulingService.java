/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.batch;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.cronutils.validator.CronValidator;
import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.entity.batch.BATCH_JOB_RUN_STATUS;
import static eds.entity.batch.BATCH_JOB_RUN_STATUS.SCHEDULED;
import static eds.entity.batch.BATCH_JOB_STATUS.ACTIVE;
import eds.entity.batch.BATCH_JOB_TRIGGER_STATUS;
import eds.entity.batch.BatchJob;
import eds.entity.batch.BatchJobRun;
import eds.entity.batch.BatchJobRun_;
import eds.entity.batch.BatchJobStep;
import eds.entity.batch.BatchJobStepParam;
import eds.entity.batch.BatchJobTrigger;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.joda.time.DateTime;
import seca2.component.landing.LandingService;
import seca2.entity.landing.ServerInstance;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class BatchSchedulingService {

    @EJB
    UpdateObjectService updateService;

    @EJB
    LandingService landingService;

    @EJB
    GenericObjectService objectService;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJob createBatchJob(long serverId, String name) throws EntityNotFoundException, IncompleteDataException {
        ServerInstance server = landingService.getServerInstance(serverId);
        if (server == null) {
            throw new EntityNotFoundException(ServerInstance.class, serverId);
        }
        
        if(name == null || name.isEmpty())
            throw new IncompleteDataException("All batch jobs must have a name.");

        BatchJob newBatchJob = new BatchJob();
        newBatchJob.setSTATUS(ACTIVE.label);
        newBatchJob.setSERVER(server);
        newBatchJob.setBATCH_JOB_NAME(name);

        updateService.getEm().persist(newBatchJob);
        return newBatchJob;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobStep createJobStep(
            String serviceName,
            String serviceMethod,
            Object[] params,
            long batchJobId) throws BatchProcessingException, IOException {
        
        BatchJob newBatchJob = (batchJobId <= 0) ? null : this.getBatchJobById(batchJobId);
        if (newBatchJob == null) {
            throw new BatchProcessingException("Batch job with ID " + batchJobId + " not found.");
        }

        BatchJobStep newBatchJobStep = new BatchJobStep();
        newBatchJobStep.setBATCH_JOB(newBatchJob);
        newBatchJobStep.setSERVICE_NAME(serviceName);
        newBatchJobStep.setSERVICE_METHOD(serviceMethod);
        updateService.getEm().persist(newBatchJobStep);

        for (int i = 0; params != null && i < params.length; i++) {

            BatchJobStepParam newParam = new BatchJobStepParam();
            newParam.setSNO(i);
            newParam.setBATCH_JOB_STEP(newBatchJobStep);
            updateService.getEm().persist(newParam);

            newBatchJobStep.addPARAMS(newParam);

            Object obj = params[i];
            Class clazz = obj.getClass();
            if (Serializable.class.isAssignableFrom(clazz)) {
                Serializable s = (Serializable) obj;
                newParam.setSERIALIZED_OBJECT(s);
                continue;
            }
            newParam.setSTRING_VALUE(obj.toString());
        }

        newBatchJob.addSTEP(newBatchJobStep);

        return newBatchJobStep;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobTrigger createJobTrigger(long batchJobId, String cronExpression) 
            throws BatchProcessingException{
        //Get the BatchJob object first
        BatchJob newBatchJob = (batchJobId <= 0) ? null : this.getBatchJobById(batchJobId);
        if (newBatchJob == null) {
            throw new BatchProcessingException("Batch job with ID " + batchJobId + " not found.");
        }
        CronDefinition cronDef = CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX);
        CronValidator cronValid = new CronValidator(cronDef);
        
        //If cronExpression is provided but invalid, throw an exception
        if (cronExpression != null && !cronExpression.isEmpty() && !cronValid.isValid(cronExpression))
            throw new BatchProcessingException("Invalid cronExpression \""+cronExpression+"\"");
        
        BatchJobTrigger newTrigger = new BatchJobTrigger();
        newTrigger.setBATCH_JOB(newBatchJob);
        newTrigger.setCRON_EXPRESSION(cronExpression);
        newTrigger.setTRIGGER_STATUS(BATCH_JOB_TRIGGER_STATUS.ACTIVE.label);
        
        updateService.getEm().persist(newTrigger);
        
        return newTrigger;
    }

    /**
     * Schedule a single step job and assign it to a new or existing batch job.
     *
     * @param batchJobName
     * @param serviceName Full class name, with package, of the EJB class eg.
     * eds.component.data.ObjectService
     * @param serviceMethod
     * @param params
     * @param cronTriggerExpression
     * @param serverId
     * @return
     * @throws eds.component.batch.BatchProcessingException
     * @throws eds.component.data.EntityNotFoundException
     * @throws eds.component.data.IncompleteDataException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobRun createSingleStepJob(
            String batchJobName,
            String serviceName,
            String serviceMethod,
            Object[] params,
            long serverId,
            String cronTriggerExpression)
            throws BatchProcessingException, EntityNotFoundException, IncompleteDataException {
        try {
            //Create batch job and the single step
            BatchJob newBatchJob = this.createBatchJob(serverId, batchJobName);
            BatchJobStep newStep = this.createJobStep(serviceName, serviceMethod, params, newBatchJob.getBATCH_JOB_ID());
            
            //Create the trigger using cronTriggerExpression
            BatchJobTrigger newTrigger = this.createJobTrigger(newBatchJob.getBATCH_JOB_ID(), cronTriggerExpression);
            
            //Trigger next run
            BatchJobRun newRun = triggerNextBatchJobRun(newTrigger);
            
            return newRun;

        } catch (SecurityException ex) {
            throw new BatchProcessingException("Batch processing failed:", ex);
        } catch (IOException ex) {
            throw new BatchProcessingException("Batch processing failed:", ex);
        }
    }

    /**
     * This is written for the admin program /batch to query all existing batch
     * jobs scheduled/executed within the given time period. For querying and
     * executing batch jobs from a particular server,
     *
     * @see eds.component.batch.BatchProcessingService#getNextNJobs()
     * 
     * Scheduled & Executed batch jobs: to read from BatchJobRun using date range
     * Waiting batch jobs: to read from BatchJob using date range
     *
     *
     * @param start
     * @param end
     * @param status If null, all statuses will be retrieved.
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<BatchJobRun> getBatchRuns(Timestamp start, Timestamp end, BATCH_JOB_RUN_STATUS status) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<BatchJobRun> query = builder.createQuery(BatchJobRun.class);
        Root<BatchJobRun> fromRun = query.from(BatchJobRun.class);
        
        List<Predicate> orCriteria = new ArrayList<>();
        //if status = null or WAITING
        //retrieve all WAITING
        
        //OR
            //if status not WAITING
            //retrieve start and end
            //AND
            //if status != null
            //AND all with status
        
        if(status == null || BATCH_JOB_RUN_STATUS.WAITING.label.equals(status)){
            orCriteria.add(builder.equal(fromRun.get(BatchJobRun_.STATUS), BATCH_JOB_RUN_STATUS.WAITING.label));
        }
        
        List<Predicate> andCriteria = new ArrayList<>();
        if(status != null){
            andCriteria.add(builder.equal(fromRun.get(BatchJobRun_.STATUS), status.label));
        }
        if(!BATCH_JOB_RUN_STATUS.WAITING.label.equals(status)){
            andCriteria.add(builder.lessThanOrEqualTo(fromRun.get(BatchJobRun_.START_TIME), end));
            andCriteria.add(builder.greaterThanOrEqualTo(fromRun.get(BatchJobRun_.END_TIME), start));
        }
        
        orCriteria.add(
                builder.and(
                        andCriteria.toArray(new Predicate[]{})));
        
        query.select(fromRun);
        query.where(builder.or(orCriteria.toArray(new Predicate[]{})));
        
        List<BatchJobRun> results = objectService.getEm().createQuery(query)
                .getResultList();
        
        return results;
            
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public BatchJob getBatchJobById(long batchJobId) {
        return objectService.getEm().find(BatchJob.class, batchJobId);
    }

    /**
     * Should check if batch job is in process and throw an exception.
     *
     * @param job
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobRun updateBatchJobRun(BatchJobRun jobRun) {

        return updateService.getEm().merge(jobRun);
    }

    /**
     *
     * @param batchJobId
     * @throws BatchProcessingException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteBatchJob(long batchJobId) throws BatchProcessingException {
        BatchJob job = updateService.getEm().find(BatchJob.class, batchJobId, LockModeType.WRITE);
        switch (BATCH_JOB_RUN_STATUS.valueOf(job.getSTATUS())) {
            case SCHEDULED:
                break;
            case IN_PROCESS:
            case COMPLETED:
            case FAILED:
                throw new BatchProcessingException("Batch job cannot be modified as it is already in " + job.getSTATUS() + " status.");
        }
        updateService.getEm().remove(job);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobRun triggerNextBatchJobRun(BatchJobTrigger trigger) throws BatchProcessingException {
        BatchJobRun newRun = new BatchJobRun();
        newRun.setBATCH_JOB(trigger.getBATCH_JOB());
        newRun.setSERVER(trigger.getBATCH_JOB().getSERVER());
        newRun.setSTATUS(BATCH_JOB_RUN_STATUS.WAITING.label);
        
        updateService.getEm().persist(newRun);
        
        String cronExpression = trigger.getCRON_EXPRESSION();
        //If cronExpression is empty, just return a WAITING BatchJobRun
        if (cronExpression == null || cronExpression.isEmpty())
            return newRun;
        
        //Validate the cron expression
        CronDefinition cronDef = CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX);
        CronValidator cronValid = new CronValidator(cronDef);
        if (!cronValid.isValid(cronExpression))
            throw new BatchProcessingException("BatchJob "+trigger.getBATCH_JOB().getBATCH_JOB_NAME()+" has invalid cronExpression \""+cronExpression+"\"");
        
        //Get the next execution time
        CronParser parser = new CronParser(cronDef);
        ExecutionTime executionTime = ExecutionTime.forCron(parser.parse(cronExpression));
        DateTime now = DateTime.now();
        DateTime nextExecution = executionTime.nextExecution(now);
        newRun.setSCHEDULED_TIME(nextExecution);
        newRun.setSTATUS(BATCH_JOB_RUN_STATUS.SCHEDULED.label);
        
        return newRun;
    }
    
    /**
     * 
     * @param key
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<BatchJobRun> getJobRunsByKey(String key) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<BatchJobRun> query = builder.createQuery(BatchJobRun.class);
        Root<BatchJobRun> fromRun = query.from(BatchJobRun.class);
        
        query.select(fromRun);
        query.where(builder.equal(fromRun.get(BatchJobRun_.RUN_KEY), key));
        
        List<BatchJobRun> results = objectService.getEm().createQuery(query)
                .getResultList();
        return results;
    }
    
    /**
     * 
     * @param run
     * @throws BatchJobRunValidationException 
     */
    public void validateBatchJobRun(BatchJobRun run) throws BatchJobRunValidationException {
        /**
         * This is a serious programming error.
         */
        if(run.getBATCH_JOB() == null)
            throw new RuntimeException("Batch Job Run is not linked to a Batch Job");
        
        /**
         * 
         */
        switch(BATCH_JOB_RUN_STATUS.valueOf(run.getSTATUS())){
            case WAITING    :   if(run.getSCHEDULED_TIME() != null) 
                                    throw new BatchJobRunValidationException("If a batch job has scheuled time, it has be in status "+BATCH_JOB_RUN_STATUS.WAITING.label);
                                break;
            case SCHEDULED  :   if(run.getSCHEDULED_TIME() == null) 
                                    throw new BatchJobRunValidationException("If a batch job has no scheuled time, it cannot be in status "+BATCH_JOB_RUN_STATUS.SCHEDULED.label);
                                break;
            case IN_PROCESS :   if(run.getSTART_TIME() == null)
                                    throw new BatchJobRunValidationException("If a batch job is in "+BATCH_JOB_RUN_STATUS.IN_PROCESS.label+" status, it has to have a start time.");
                                break;
            case COMPLETED  :   if(run.getEND_TIME() == null)
                                    throw new BatchJobRunValidationException("If a batch job is in "+BATCH_JOB_RUN_STATUS.COMPLETED.label+" status, it has to have an end time.");
                                break;
            case FAILED     :   //if(run.getEND_TIME() == null)
                                //    throw new BatchJobRunValidationException("If a batch job is in "+BATCH_JOB_RUN_STATUS.COMPLETED.label+" status, it has to have an end time.");
                                break;
            case CANCELLED  :   if(run.getEND_TIME() == null)
                                    throw new BatchJobRunValidationException("If a batch job is in "+BATCH_JOB_RUN_STATUS.CANCELLED.label+" status, it has to have an end time.");
                                break;
            default         :   break;
                                
        }
            
    }
}
