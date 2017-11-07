/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import eds.component.encryption.EncryptionUtility;
import eds.component.encryption.EncryptionType;
import eds.entity.audit.ActiveUser;
import javax.inject.Inject;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
public class BatchJobRunListener {
    
    @Inject ActiveUser user;
    
    @PrePersist
    public void PrePersist(BatchJobRun run) {
        this.recordCreated(run);
        this.generateTransactionKey(run);
    }
    
    @PreUpdate
    public void PreUpdate(BatchJobRun run) {
        this.recordCreated(run);
        this.generateTransactionKey(run);
    }
    
    public void recordCreated(BatchJobRun run){
        if(run.getDATETIME_CREATED()!= null) return;
        
        DateTime today = new DateTime();
        java.sql.Timestamp todaySQL = new java.sql.Timestamp(today.getMillis());
        
        run.setDATETIME_CREATED(todaySQL);
        if(user != null )
            run.setCREATED_BY(user.getUsername());
    }
    

    private void generateTransactionKey(BatchJobRun run) {
        if(run.getRUN_KEY() != null && !run.getRUN_KEY().isEmpty())
            return;
        long batchJobId = run.getBATCH_JOB().getBATCH_JOB_ID();
        long ts = run.getDATETIME_CREATED().getTime();
        double random = Math.random()*ts;
        String hash = EncryptionUtility.getHash(Long.toHexString(batchJobId^ts)+Double.toString(random), EncryptionType.MD5);
        
        run.setRUN_KEY(hash);
    }
    
    public void updateDates(BatchJobRun run) {
        
    }
    
}
