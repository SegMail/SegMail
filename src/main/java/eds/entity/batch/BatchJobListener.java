/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import eds.component.encryption.EncryptionService;
import eds.component.encryption.EncryptionType;
import eds.entity.audit.ActiveUser;
import javax.inject.Inject;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
public class BatchJobListener {
    
    @Inject ActiveUser user;
    
    @PrePersist
    public void PrePersist(BatchJob job) {
        this.recordCreated(job);
        this.recordDatetimeChanged(job);
    }
    
    @PostPersist
    public void PostPersist(BatchJob job){
        //this.generateTransactionKey(trans);
    }
    
    @PreUpdate
    public void PreUpdate(BatchJob job) {
        this.recordCreated(job);
        this.recordDatetimeChanged(job);
    }
    
    @PostUpdate
    public void PostUpdate(BatchJob job) {
        //this.generateTransactionKey(trans);
    }
    
    public void recordDatetimeChanged(BatchJob job){
        DateTime today = new DateTime();
        java.sql.Timestamp todaySQL = new java.sql.Timestamp(today.getMillis());
        
        job.setDATETIME_CHANGED(todaySQL);
        if(user != null )
            job.setCHANGED_BY(user.getUsername());
    }
    
    public void recordCreated(BatchJob job){
        if(job.getDATETIME_CREATED()!= null) return;
        
        DateTime today = new DateTime();
        java.sql.Timestamp todaySQL = new java.sql.Timestamp(today.getMillis());
        
        job.setDATETIME_CREATED(todaySQL);
        if(user != null )
            job.setCREATED_BY(user.getUsername());
    }
    
    
}
