/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.batch;

import com.google.common.base.Objects;
import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.entity.batch.BATCH_JOB_RUN_STATUS;
import eds.entity.batch.BatchJobRun;
import eds.entity.batch.BatchJobStep;
import eds.entity.batch.BatchJobStep_;
import eds.entity.batch.BatchJobTrigger;
import java.sql.Timestamp;
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
import org.jboss.logging.Logger;
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
            DateTime start = DateTime.now();
            job.setSTATUS(BATCH_JOB_RUN_STATUS.IN_PROCESS.label);
            job.setSTART_TIME(new Timestamp(start.getMillis()));
            job.getBATCH_JOB().setLAST_RUN(new Timestamp(start.getMillis()));
            updService.getEm().merge(job);
            updService.getEm().flush();
            
            Object ret = null;
            List<BatchJobStep> steps = this.getBatchJobSteps(job.getBATCH_JOB().getBATCH_JOB_ID());
            for (BatchJobStep step : steps) {
                ret = step.execute();
                Logger.getLogger(this.getClass().getSimpleName()).log(Logger.Level.ERROR, 
                        (ret == null) ? "" : ret.toString());
            }
            //Record completion info
            job.setSTATUS(BATCH_JOB_RUN_STATUS.COMPLETED.label);
            DateTime end = DateTime.now();
            job.setEND_TIME(new Timestamp(end.getMillis()));
            
            //Trigger the next run
            DateTime now = DateTime.now();
            List<BatchJobTrigger> triggers = scheduleService.loadBatchJobTriggers(job.getBATCH_JOB().getBATCH_JOB_ID());
            //Should be loosely coupled procedure, no exceptions thrown
            if(triggers != null && !triggers.isEmpty()
                    && !Objects.equal((ret == null) ? null : ret.getClass(),StopNextRunQuickAndDirty.class)) { //THIS IS A HACK!!!
                //Logger.getLogger(this.getClass().getSimpleName()).log(Logger.Level.ERROR, "No trigger found for batch job "+job.getBATCH_JOB().getBATCH_JOB_ID());
                scheduleService.triggerNextBatchJobRun(now, triggers.get(0));
            }
        } catch (Throwable ex) {
            Logger.getLogger(this.getClass().getSimpleName()).log(Logger.Level.ERROR, ex.getMessage());
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
