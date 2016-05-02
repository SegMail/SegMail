/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.batch;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.data.EntityNotFoundException;
import eds.entity.batch.BATCH_JOB_STATUS;
import static eds.entity.batch.BATCH_JOB_STATUS.SCHEDULED;
import static eds.entity.batch.BATCH_JOB_STATUS.WAITING;
import eds.entity.batch.BatchJob;
import eds.entity.batch.BatchJobStep;
import eds.entity.batch.BatchJobStepParam;
import eds.entity.batch.BatchJob_;
import eds.entity.transaction.EnterpriseTransaction_;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
    
    /**
     * Schedule a single step job and assign it to a new or existing batch job.
     *
     * @param serviceName Full class name, with package, of the EJB class eg.
     * eds.component.data.ObjectService
     * @param serviceMethod
     * @param params
     * @param batchJobId if found, step will be added to the existing batch job,
     * else a new batch job will be created.
     * @param serverId
     * @param scheduledTime
     * @return 
     * @throws eds.component.batch.BatchProcesingException 
     * @throws eds.component.data.EntityNotFoundException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobStep createJobStep(
            String serviceName, 
            String serviceMethod, 
            Object[] params, 
            long batchJobId, 
            long serverId,
            DateTime scheduledTime) 
            throws BatchProcesingException, EntityNotFoundException {
        try {
            ServerInstance server = landingService.getServerInstance(serverId);
            if(server == null)
                throw new EntityNotFoundException(ServerInstance.class,serverId);
            
            BatchJob newBatchJob = updateService.getEm().find(BatchJob.class, batchJobId);
            if (newBatchJob == null) {
                newBatchJob = new BatchJob();
            }
            newBatchJob.setSTATUS(WAITING.label);
            if(scheduledTime != null) {
                newBatchJob.setSTATUS(SCHEDULED.label);
                newBatchJob.setSCHEDULED_TIME(new Timestamp(scheduledTime.getMillis()));
            }
                
            newBatchJob.setSERVER(server);
            updateService.getEm().persist(newBatchJob);
            
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
            
            updateService.getEm().flush();
            return newBatchJobStep;
            
        } catch (SecurityException ex) {
            throw new BatchProcesingException("Batch processing failed:", ex);
        } catch (IOException ex) {
            throw new BatchProcesingException("Batch processing failed:", ex);
        }
    }
    
    /**
     * This is written for the admin program /batch to query all existing batch 
     * jobs scheduled/executed within the given time period. For querying and 
     * executing batch jobs from a particular server, 
     * 
     * @see eds.component.batch.BatchProcessingService#getNextNJobs()
     * 
     * 
     * @param start
     * @param end
     * @param status If null, all statuses will be retrieved.
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<BatchJob> getBatchJobs(Timestamp start, Timestamp end, BATCH_JOB_STATUS status){
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<BatchJob> query = builder.createQuery(BatchJob.class);
        Root<BatchJob> fromBatchJob = query.from(BatchJob.class);
        
        List<Predicate> conditions = new ArrayList<>();
        
        conditions.add(builder.or(
                                builder.between(fromBatchJob.get(BatchJob_.DATETIME_CREATED), start, end),
                                builder.between(fromBatchJob.get(BatchJob_.DATETIME_CHANGED), start, end),
                                builder.between(fromBatchJob.get(BatchJob_.SCHEDULED_TIME), start, end),
                                builder.and(
                                        builder.lessThanOrEqualTo(fromBatchJob.get(BatchJob_.START_TIME), end),
                                        builder.greaterThanOrEqualTo(fromBatchJob.get(BatchJob_.END_TIME), start)
                                )
                        ));
        
        if (status != null){
            conditions.add(builder.equal(fromBatchJob.get(BatchJob_.STATUS), status.label));
        }
        
        query.where(builder.and(conditions.toArray(new Predicate[]{})));
        
        /*query.select(fromBatchJob);
        query.where(
                builder.and(
                        builder.or(
                                builder.between(fromBatchJob.get(BatchJob_.DATETIME_CREATED), start, end),
                                builder.between(fromBatchJob.get(BatchJob_.DATETIME_CHANGED), start, end),
                                builder.and(
                                        builder.lessThanOrEqualTo(fromBatchJob.get(BatchJob_.START_TIME), end),
                                        builder.greaterThanOrEqualTo(fromBatchJob.get(BatchJob_.END_TIME), start)
                                )
                        ),
                        builder.equal(fromBatchJob.get(BatchJob_.PROCESSING_STATUS), status.label)
                )
        );*/
        
        List<BatchJob> results = objectService.getEm().createQuery(query)
                .getResultList();
        
        return results;
    }
    
    
    public BatchJob getBatchJobById(long batchJobId) {
        return objectService.getEm().find(BatchJob.class, batchJobId);
    }
    
}
