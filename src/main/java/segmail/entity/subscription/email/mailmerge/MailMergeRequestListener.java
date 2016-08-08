/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.email.mailmerge;

import eds.entity.transaction.EnterpriseTransaction;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
public class MailMergeRequestListener {
    
    //private final int EXPIRATION_TIME_MS = 86400000; //24 hours
    
    @PrePersist
    public void PrePersist(MailMergeRequest req) {
        this.recordExpiry(req);
    }
    
    @PostPersist
    public void PostPersist(MailMergeRequest req) {
        
    }
    
    @PreUpdate
    public void PreUpdate(MailMergeRequest req) {
        
    }
    
    public void recordExpiry(MailMergeRequest req) {
        
        java.sql.Timestamp lastChanged = (req.getDATETIME_CHANGED() == null) ? 
                req.getDATETIME_CREATED() : req.getDATETIME_CHANGED();
        
        if(lastChanged == null)
            return;
        
        int expiry = MAILMERGE_REQUEST.valueOf(req.getMAILMERGE_LABEL()).expiry;
        
        if(expiry <= 0)
            return;
        
        DateTime newExpiryDate = new DateTime(lastChanged.getTime());
        newExpiryDate.plusMillis(expiry);
        req.setEXPIRY_DATETIME(new java.sql.Timestamp(newExpiryDate.getMillis()));
        
    }
}
