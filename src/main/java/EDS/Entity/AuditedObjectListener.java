/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package EDS.Entity;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.joda.time.DateTime;

/**
 *
 * @author KH
 */
public class AuditedObjectListener {
    
    @PrePersist
    @PreUpdate
    public void PrePersistUpdate(EnterpriseObject entity){
        recordDateChanged(entity);
        recordCreated(entity);
    }
    
    
    public void recordDateChanged(EnterpriseObject entity){
        DateTime today = new DateTime();
        java.sql.Date todaySQL = new java.sql.Date(today.getMillis());
        
        entity.setDATE_CHANGED(todaySQL);
    }
    
    public void recordCreated(EnterpriseObject entity){
        if(entity.DATE_CREATED != null) return;
        
        DateTime today = new DateTime();
        java.sql.Date todaySQL = new java.sql.Date(today.getMillis());
        
        entity.setDATE_CREATED(todaySQL);
    }
}
