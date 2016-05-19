/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.batch;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.entity.batch.BATCH_JOB_RUN_STATUS;
import eds.entity.batch.BatchJobRun;
import eds.entity.batch.BatchJobStep;
import eds.entity.batch.BatchJobStep_;
import eds.entity.batch.BatchJobTrigger;
import java.util.List;
import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class BatchExecutionService {

    @EJB
    GenericObjectService objService;
    @EJB
    UpdateObjectService updService;
    @EJB
    BatchSchedulingService scheduleService; 

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<BATCH_JOB_RUN_STATUS> executeJob(BatchJobRun job) {
        try {
            job.setSTATUS(BATCH_JOB_RUN_STATUS.IN_PROCESS.label);
            updService.getEm().merge(job);
            updService.getEm().flush();
            List<BatchJobStep> steps = this.getBatchJobSteps(job.getBATCH_JOB().getBATCH_JOB_ID());
            for (BatchJobStep step : steps) {
                step.execute();
            }
            job.setSTATUS(BATCH_JOB_RUN_STATUS.COMPLETED.label);
            
            //Trigger the next run
            DateTime now = DateTime.now();
            List<BatchJobTrigger> triggers = scheduleService.loadBatchJobTriggers(job.getBATCH_JOB().getBATCH_JOB_ID());
            //Should be loosely coupled procedure, no exceptions thrown
            if(triggers != null || !triggers.isEmpty()) {
                scheduleService.triggerNextBatchJobRun(now, triggers.get(0));
            }
            
        } catch (Throwable ex) {
            job.setSTATUS(BATCH_JOB_RUN_STATUS.FAILED.label);
            
        } finally {
            updService.getEm().merge(job);
            updService.getEm().flush();
            return new AsyncResult<>(BATCH_JOB_RUN_STATUS.valueOf(job.getSTATUS()));
        }
        
    }

    public List<BatchJobStep> getBatchJobSteps(long batchJobId) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<BatchJobStep> query = builder.createQuery(BatchJobStep.class);
        Root<BatchJobStep> fromBatchJobRun = query.from(BatchJobStep.class);

        query.select(fromBatchJobRun);
        query.where(builder.and(
                builder.equal(fromBatchJobRun.get(BatchJobStep_.BATCH_JOB), batchJobId)
        ));

        List<BatchJobStep> results = objService.getEm().createQuery(query)
                .getResultList();

        return results;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setInProcess(BatchJobRun job) {
        job.setSTATUS(BATCH_JOB_RUN_STATUS.IN_PROCESS.label);
        updService.getEm().merge(job);
        updService.getEm().flush();
    }
}
