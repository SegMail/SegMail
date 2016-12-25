/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.batch;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
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
import eds.entity.batch.BatchJobStep_;
import eds.entity.batch.BatchJobTrigger;
import eds.entity.batch.BatchJobTrigger_;
import eds.entity.batch.BatchJob_;
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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.joda.time.DateTime;
import seca2.component.landing.LandingService;
import seca2.entity.landing.ServerInstance;
import seca2.entity.landing.ServerInstance_;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class BatchSchedulingService {
    
    public final CronType STANDARD_CRON_TYPE = CronType.UNIX;

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

        if (name == null || name.isEmpty()) {
            throw new IncompleteDataException("All batch jobs must have a name.");
        }

        BatchJob newBatchJob = new BatchJob();
        newBatchJob.setSTATUS(ACTIVE.label);
        //newBatchJob.setSERVER(server);
        newBatchJob.setBATCH_JOB_NAME(name);
        newBatchJob.setSERVER_NAME(server.getNAME());

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
            newParam.setPARAM_ORDER(i);
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
        return newBatchJobStep;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobTrigger createJobTrigger(long batchJobId, String cronExpression)
            throws BatchProcessingException {
        //Get the BatchJob object first
        BatchJob newBatchJob = (batchJobId <= 0) ? null : this.getBatchJobById(batchJobId);
        if (newBatchJob == null) {
            throw new BatchProcessingException("Batch job with ID " + batchJobId + " not found.");
        }
        
        /*CronDefinition cronDef = CronDefinitionBuilder.instanceDefinitionFor(STANDARD_CRON_TYPE);
        CronValidator cronValid = new CronValidator(cronDef);

        //If cronExpression is provided but invalid, throw an exception
        if (cronExpression != null && !cronExpression.isEmpty() && !cronValid.isValid(cronExpression)) {
            throw new BatchProcessingException("Invalid cronExpression \"" + cronExpression + "\"");
        }*/
        //Validation method, throw runtime exception if invalids
        Cron validCron = this.getValidCronExp(cronExpression, STANDARD_CRON_TYPE);

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
     * @param currentTime that tells BatchSchedulingService when to start computing
     * the next run based on cronTriggerExpression.
     * 
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
            String cronTriggerExpression,
            DateTime currentTime)
            throws BatchProcessingException, EntityNotFoundException, IncompleteDataException {
        try {
            //Create batch job and the single step
            BatchJob newBatchJob = this.createBatchJob(serverId, batchJobName);
            BatchJobStep newStep = this.createJobStep(serviceName, serviceMethod, params, newBatchJob.getBATCH_JOB_ID());

            //Create the trigger using cronTriggerExpression
            BatchJobTrigger newTrigger = this.createJobTrigger(newBatchJob.getBATCH_JOB_ID(), cronTriggerExpression);

            //Trigger next run
            //Because Cron expression doesn't have seconds, so if you don't set this,
            //your batch job won't run immediately even if your Cron expression 
            //means so.
            BatchJobRun newRun = triggerNextBatchJobRun(currentTime.withSecondOfMinute(0), newTrigger); 

            return newRun;

        } catch (SecurityException ex) {
            throw new BatchProcessingException("Batch processing failed:", ex);
        } catch (IOException ex) {
            throw new BatchProcessingException("Batch processing failed:", ex);
        }
    }
    
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
            BatchJobRun newRun = triggerNextBatchJobRun(DateTime.now(), newTrigger);

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
     * @param start
     * @param end
     * @param statuses
     * @param indexStart -1 to omit
     * @param recordLimit -1 to omit
     * @see eds.component.batch.BatchProcessingService#getNextNJobs()
     *
     * Scheduled & Executed batch jobs: to read from BatchJobRun using date
     * range Waiting batch jobs: to read from BatchJob using date range
     *
     * @return
     */
    public List<BatchJobRun> getBatchRuns(Timestamp start, Timestamp end, List<String> statuses, int indexStart, int recordLimit) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<BatchJobRun> query = builder.createQuery(BatchJobRun.class);
        Root<BatchJobRun> fromRun = query.from(BatchJobRun.class);
        
        List<Predicate> andCriteria = new ArrayList<>();
        
        andCriteria.add(builder.lessThanOrEqualTo(fromRun.get(BatchJobRun_.DATETIME_CREATED), end));
        andCriteria.add(builder.greaterThanOrEqualTo(fromRun.get(BatchJobRun_.END_TIME), start));
        
        if(statuses != null && !statuses.isEmpty()) {
            andCriteria.add(fromRun.get(BatchJobRun_.STATUS).in(statuses));
        }
        
        query.select(fromRun);
        query.where(andCriteria.toArray(new Predicate[]{}));
        
        
        TypedQuery<BatchJobRun> finalQuery = objectService.getEm().createQuery(query);
        if(indexStart >= 0) 
            finalQuery.setFirstResult(indexStart);
        if(recordLimit > 0)
            finalQuery.setMaxResults(recordLimit);
        List<BatchJobRun> results = finalQuery.getResultList();

        return results;
    }

    public BatchJob getBatchJobById(long batchJobId) {
        return objectService.getEm().find(BatchJob.class, batchJobId);
    }

    /**
     * Should check if batch job is in process and throw an exception.
     *
     * @param jobRun
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobRun updateBatchJobRun(BatchJobRun jobRun) {
        updateBatchJobRunStatus(jobRun);
        return updateService.getEm().merge(jobRun);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJob updateBatchJob(BatchJob job) {
        
        return updateService.getEm().merge(job);
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

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BatchJobRun triggerNextBatchJobRun(DateTime now, BatchJobTrigger trigger) 
            throws BatchProcessingException {
        if(trigger == null)
            return null;
        BatchJobRun newRun = new BatchJobRun();
        newRun.setBATCH_JOB(trigger.getBATCH_JOB());
        //newRun.setSERVER(trigger.getBATCH_JOB().getSERVER());
        newRun.setSERVER_NAME(trigger.getBATCH_JOB().getSERVER_NAME());
        //newRun.setSTATUS(BATCH_JOB_RUN_STATUS.WAITING.label);
        newRun.wait(now);

        updateService.getEm().persist(newRun);

        String cronExpression = trigger.getCRON_EXPRESSION();
        //If cronExpression is empty, just return a WAITING BatchJobRun
        if (cronExpression == null || cronExpression.isEmpty()) {
            return newRun;
        }

        //Validate the cron expression
        //No need as 4.1.0 onwards will use Cron.validate()
        /*CronDefinition cronDef = CronDefinitionBuilder.instanceDefinitionFor(STANDARD_CRON_TYPE);
        CronValidator cronValid = new CronValidator(cronDef);
        if (!cronValid.isValid(cronExpression)) {
            throw new BatchProcessingException("BatchJob " + trigger.getBATCH_JOB().getBATCH_JOB_NAME() + " has invalid cronExpression \"" + cronExpression + "\"");
        }*/

        //Get the next execution time
        //DateTime now = DateTime.now();
        DateTime nextExecution = this.getNextExecutionTimeCron(cronExpression, now, STANDARD_CRON_TYPE);
        //newRun.schedule(nextExecution);
        //newRun.setSTATUS(BATCH_JOB_RUN_STATUS.SCHEDULED.label);
        newRun.schedule(nextExecution);
        
        return newRun;
    }

    /**
     *
     * @param key
     * @return
     */
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
     * @param key
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int deleteBatchJobRun(String key) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaDelete<BatchJobRun> query = builder.createCriteriaDelete(BatchJobRun.class);
        Root<BatchJobRun> fromRun = query.from(BatchJobRun.class);

        query.where(builder.equal(fromRun.get(BatchJobRun_.RUN_KEY), key));
        
        int result = objectService.getEm().createQuery(query)
                .executeUpdate();
        
        return result;
    }
    
    /**
     * Just updates the status and cancellation datetime. If the job run is already
     * processing on some other server, the server should poll the status of this 
     * batch job run to decide if the execution should halt.
     * 
     * @param key 
     * @return  
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobRun cancelBatchJobRun(String key) throws BatchProcessingException {
        
        List<BatchJobRun> runs = this.getJobRunsByKey(key);
        if(runs == null || runs.isEmpty())
            throw new BatchProcessingException("Batch job not found for key "+key);
        
        BatchJobRun run = runs.get(0);
        run.cancel(DateTime.now());
        
        return updateService.getEm().merge(run);
        
    }

    /**
     * 
     * CANCELLED - CANCELLED_TIME
     * 
     * WAITING: (no time)
     *
     * SCHEDULED: - SCHEDULED_TIME
     *
     * IN_PROCESS: - SCHEDULED_TIME - START_TIME
     *
     * COMPLETED: - SCHEDULED_TIME - START_TIME - END_TIME
     *
     * FAILED: - SCHEDULED_TIME - START_TIME - END_TIME
     *
     *
     *
     * @param run
     */
    private void updateBatchJobRunStatus(BatchJobRun run) {
        
        if (run.getCANCEL_TIME() != null) {
            run.setSTATUS(BATCH_JOB_RUN_STATUS.CANCELLED.label);
            return;
        }
        
        if (run.getSCHEDULED_TIME() == null) {
            run.setSTATUS(BATCH_JOB_RUN_STATUS.WAITING.label);
            return;
        }

        if (run.getSTART_TIME() == null) {
            run.setSTATUS(BATCH_JOB_RUN_STATUS.SCHEDULED.label);
            return;
        }

        if (run.getEND_TIME() == null) {
            run.setSTATUS(BATCH_JOB_RUN_STATUS.IN_PROCESS.label);
            return;
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
    
    public List<BatchJobTrigger> loadBatchJobTriggers(long batchJobId) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<BatchJobTrigger> query = builder.createQuery(BatchJobTrigger.class);
        Root<BatchJobTrigger> fromTrigger = query.from(BatchJobTrigger.class);
        
        query.select(fromTrigger);
        query.where(builder.equal(fromTrigger.get(BatchJobTrigger_.BATCH_JOB), batchJobId));
        
        List<BatchJobTrigger> results = objectService.getEm().createQuery(query)
                .getResultList();
        
        return results;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<BatchJobStep> loadBatchJobSteps(long batchJobId) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<BatchJobStep> query = builder.createQuery(BatchJobStep.class);
        Root<BatchJobStep> fromSteps = query.from(BatchJobStep.class);
        
        query.select(fromSteps);
        query.where(builder.equal(fromSteps.get(BatchJobStep_.BATCH_JOB), batchJobId));
        
        List<BatchJobStep> results = objectService.getEm().createQuery(query)
                .getResultList();
        
        return results;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobTrigger updateBatchJobTrigger(BatchJobTrigger trigger){
        //Do checks
        //Get latest TRIGGER_ORDER
        BatchJobTrigger mergedTrigger = updateService.getEm().merge(trigger);
        
        if(trigger.getBATCH_JOB() == null) //Shouldn't happen, if the above statement was successfully executed
            throw new RuntimeException("No batch job trigger should be created without a BatchJob object.");
        
        List<BatchJobTrigger> allTriggers = loadBatchJobTriggers(trigger.getBATCH_JOB().getBATCH_JOB_ID());
        //Assume that it is retrieved with the intended order
        //"Refresh" the indexes
        //mergedTrigger should be part of this list
        for(int i=0; i<allTriggers.size(); i++) {
            BatchJobTrigger t = allTriggers.get(i);
            t.setTRIGGER_ORDER(i);
            t = updateService.getEm().merge(t);
        }
        
        return mergedTrigger;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobStep updateBatchJobStep(BatchJobStep step){
        //Do checks
        return updateService.getEm().merge(step);       
    }
    
    /**
     * 
     * @param batchJobId
     * @param serverId
     * @return
     * @throws EntityNotFoundException
     * @throws BatchProcessingException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void assignServerToBatchJob(long batchJobId, long serverId) throws EntityNotFoundException, BatchProcessingException {
        //ServerInstance server = landingService.getServerInstance(serverId);
        //if(server == null)
        //    throw new EntityNotFoundException(ServerInstance.class,serverId);
        
        /*BatchJob job = this.getBatchJobById(batchJobId);
        if(job == null)
            throw new BatchProcessingException("Batch job ID "+batchJobId+" not found.");
        
        job.setSERVER(server);
        updateService.getEm().merge(job);*/
        CriteriaBuilder builder = updateService.getEm().getCriteriaBuilder();
        CriteriaUpdate<BatchJob> update = builder.createCriteriaUpdate(BatchJob.class);
        Root<BatchJob> fromBatchJob = update.from(BatchJob.class);
        
        Subquery<String> serverQuery = update.subquery(String.class);
        Root<ServerInstance> fromServer = serverQuery.from(ServerInstance.class);
        serverQuery.select(fromServer.get(ServerInstance_.NAME));
        serverQuery.where(builder.equal(fromServer.get(ServerInstance_.OBJECTID), serverId));
        
        update.set(fromBatchJob.get(BatchJob_.SERVER_NAME), serverQuery);
        update.where(builder.equal(fromBatchJob.get(BatchJob_.BATCH_JOB_ID), batchJobId));
        
        int results = updateService.getEm().createQuery(update)
                .executeUpdate();
        
        if(results <= 0)
            throw new BatchProcessingException("No batch jobs updated.");
        
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void assignServerToBatchJobRun(String runKey, long serverId) throws EntityNotFoundException, BatchProcessingException {
        /*ServerInstance server = landingService.getServerInstance(serverId);
        if(server == null)
            throw new EntityNotFoundException(ServerInstance.class,serverId);
        
        List<BatchJobRun> runs = this.getJobRunsByKey(runKey);
        if(runs == null || runs.isEmpty())
            throw new BatchProcessingException("Batch job run key "+runKey+" not found.");
        
        BatchJobRun run = runs.get(0);
        //run.setSERVER(server);
        run.setSERVER_NAME(server.getNAME());
        updateService.getEm().merge(run);*/
        CriteriaBuilder builder = updateService.getEm().getCriteriaBuilder();
        CriteriaUpdate<BatchJobRun> update = builder.createCriteriaUpdate(BatchJobRun.class);
        Root<BatchJobRun> fromBatchJob = update.from(BatchJobRun.class);
        
        Subquery<String> serverQuery = update.subquery(String.class);
        Root<ServerInstance> fromServer = serverQuery.from(ServerInstance.class);
        serverQuery.select(fromServer.get(ServerInstance_.NAME));
        serverQuery.where(builder.equal(fromServer.get(ServerInstance_.OBJECTID), serverId));
        
        update.set(fromBatchJob.get(BatchJobRun_.SERVER_NAME), serverQuery);
        update.where(builder.equal(fromBatchJob.get(BatchJobRun_.RUN_KEY), runKey));
        
        int results = updateService.getEm().createQuery(update)
                .executeUpdate();
        
        if(results <= 0)
            throw new BatchProcessingException("No batch jobs updated.");
    }
    
    public Cron getValidCronExp(String cronExp,CronType cronType) {
        CronDefinition cronDef = CronDefinitionBuilder.instanceDefinitionFor(cronType);
        CronParser parser = new CronParser(cronDef);
        return parser.parse(cronExp).validate();
        
    }
    
    public long countBatchJobRuns(Timestamp start, Timestamp end, List<String> statuses) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<BatchJobRun> fromRuns = query.from(BatchJobRun.class);
        
        List<Predicate> andCriteria = new ArrayList<>();
        
        andCriteria.add(builder.lessThanOrEqualTo(fromRuns.get(BatchJobRun_.DATETIME_CREATED), end));
        andCriteria.add(builder.greaterThanOrEqualTo(fromRuns.get(BatchJobRun_.END_TIME), start));
        
        if(statuses != null && !statuses.isEmpty()) {
            andCriteria.add(fromRuns.get(BatchJobRun_.STATUS).in(statuses));
        }
        
        query.select(builder.count(fromRuns));
        query.where(andCriteria.toArray(new Predicate[]{}));
        
        long result = objectService.getEm().createQuery(query).getSingleResult();
        
        return result;
    }
}
