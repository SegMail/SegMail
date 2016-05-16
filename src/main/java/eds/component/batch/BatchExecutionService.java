/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.batch;

import eds.component.UpdateObjectService;
import eds.entity.batch.BATCH_JOB_RUN_STATUS;
import eds.entity.batch.BatchJob;
import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class BatchExecutionService {
    
    @EJB UpdateObjectService updService;
    
    /*@Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<BATCH_JOB_RUN_STATUS> executeJob(BatchJob job){
        updService.getEm().refresh(job);
        job.setSTATUS(BATCH_JOB_RUN_STATUS.IN_PROCESS.label);
        updService.getEm().flush();
        
        try {
            job.execute();
            job.setSTATUS(BATCH_JOB_RUN_STATUS.COMPLETED.label);
        } catch (BatchProcessingException ex) {
            //Write job log
            job.setSTATUS(BATCH_JOB_RUN_STATUS.FAILED.label);
            
        } finally {
            updService.getEm().flush();
        }
        
        return new AsyncResult<BATCH_JOB_RUN_STATUS>(BATCH_JOB_RUN_STATUS.valueOf(job.getSTATUS()));
    }*/
}
