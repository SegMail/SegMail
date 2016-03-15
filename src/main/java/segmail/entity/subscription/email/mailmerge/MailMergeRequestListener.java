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
    
    private final int EXPIRATION_TIME_MS = 86400000; //24 hours
    
    @PrePersist
    public void PrePersist(EnterpriseTransaction trans) {
        this.recordExpiry(trans);
    }
    
    @PostPersist
    public void PostPersist(EnterpriseTransaction trans) {
        
    }
    
    @PreUpdate
    public void PreUpdate(EnterpriseTransaction trans) {
        
    }
    
    public void recordExpiry(EnterpriseTransaction trans) {
        
        java.sql.Timestamp lastChanged = (trans.getDATETIME_CHANGED() == null) ? 
                trans.getDATETIME_CREATED() : trans.getDATETIME_CHANGED();
        
        if(lastChanged == null)
            return;
        
        DateTime newExpiryDate = new DateTime(lastChanged.getTime());
        newExpiryDate.plusMillis(EXPIRATION_TIME_MS);
        trans.setEXPIRY_DATETIME(new java.sql.Timestamp(newExpiryDate.getMillis()));
        
    }
}
