/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.batch;

import eds.component.DBService;
import eds.entity.batch.BATCH_JOB_RUN_STATUS;
import eds.entity.batch.BatchJobRun;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class BatchJobTransitionService extends DBService {
    
    @EJB BatchJobTransitionServiceHelper helper;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobRun transit(BatchJobRun run, BATCH_JOB_RUN_STATUS status, DateTime dt) 
            throws BatchProcessingException {
        
        if(status.equals(run.STATUS()))
            throw new BatchProcessingException("Batch job run "+run.getRUN_KEY()+" has the same status as the proposed new status "+status.label);
        
        //int remove = helper.removeRun(run.getRUN_KEY(), run.STATUS());
        //if(remove <= 0) 
        //    throw new BatchProcessingException("Batch job run "+run.getRUN_KEY()+" is not in status "+run.STATUS()+" anymore.");
        em.remove(run);

        BatchJobRun newRun = run.transit(status,dt);
        newRun = helper.reInsertRun(newRun); 
        // we are getting a JPA exception because the run was already deleted
        // but not yet flushed hence there will be 2 instances of BatchJobRun with the same ID
        
        return newRun;
    }
}
