/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.batch;

import eds.component.DBService;
import eds.entity.batch.BATCH_JOB_RUN_STATUS;
import eds.entity.batch.BatchJobRun;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    public static final int MAX_DELETE_RETRIES = 3;
    
    @EJB BatchJobTransitionServiceHelper helper;
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public BatchJobRun transit(BatchJobRun run, BATCH_JOB_RUN_STATUS status, DateTime dt) 
            throws BatchProcessingException {
        
        if(status.equals(run.STATUS()))
            throw new BatchProcessingException("Batch job run "+run.getRUN_KEY()+" has the same status as the proposed new status "+status.label);
        
        // Try for MAX_DELETE_RETRIES times
        int remove = 0;
        int tries = 0;
        boolean done = false;
        while(tries < MAX_DELETE_RETRIES && !done) {
            try {
                remove = helper.removeRun2(run.getRUN_KEY(), run.STATUS());
                done = true;
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                        ("Lock acquisition exception? Retrying..." + (MAX_DELETE_RETRIES - tries) 
                                + " tries remaining...")
                        );
                tries++;
            }
        }
        if(!done) {
            throw new BatchProcessingException("Lock acquisition exception for " + run.getRUN_KEY());
        }
        
        if(remove <= 0) 
            throw new BatchProcessingException("Batch job run "+run.getRUN_KEY()+" is not in status "+run.STATUS()+" anymore.");

        /**
         * Because we are handling the 2 operations - delete and reinsert 
         * separately, we need to handle the case when the reinsert operation has 
         * failed and we have to "rollback" the delete operation. 
         * We could have put this entire method into 1 transaction, but from the 
         * way hibernate behaves it would result in the scenario where the delete
         * operation is not flushed to the db quick enough before another EJB
         * picks the batch job up again and creating duplicated runs.
         */
        BatchJobRun newRun = run.transit(status,dt);
        try {
            newRun = helper.reInsertRun(newRun);
        } catch (Exception ex) {
            newRun = helper.reInsertRun(run); // Return the old run instead
        }
        return newRun;
    }
}
