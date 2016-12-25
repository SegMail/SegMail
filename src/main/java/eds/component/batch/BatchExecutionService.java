/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.batch;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.entity.batch.BatchJobRun;
import eds.entity.batch.BatchJobRunError;
import eds.entity.batch.BatchJobStep;
import eds.entity.batch.BatchJobStep_;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.NamingException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.jboss.logging.Logger;

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
    public Future<?> executeJobNew(BatchJobRun job) {

        try {
            Object ret = null; //Only return the last return object from the last step
            List<BatchJobStep> steps = this.getBatchJobSteps(job.getBATCH_JOB().getBATCH_JOB_ID());
            for (BatchJobStep step : steps) {
                ret = step.execute();
                
            }
            return new AsyncResult<>(ret);
            
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getSimpleName()).log(Logger.Level.ERROR, ex.getMessage());
            return new AsyncResult<>(ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(this.getClass().getSimpleName()).log(Logger.Level.ERROR, ex.getMessage());
            return new AsyncResult<>(ex);
        } catch (NamingException ex) {
            Logger.getLogger(this.getClass().getSimpleName()).log(Logger.Level.ERROR, ex.getMessage());
            return new AsyncResult<>(ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(this.getClass().getSimpleName()).log(Logger.Level.ERROR, ex.getMessage());
            return new AsyncResult<>(ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(this.getClass().getSimpleName()).log(Logger.Level.ERROR, ex.getMessage());
            return new AsyncResult<>(ex.getTargetException());
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

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void logErrors(BatchJobRun job, Throwable ex) {
        BatchJobRunError newError = new BatchJobRunError(job, ex);

        updService.getEm().persist(ex);
    }
}
