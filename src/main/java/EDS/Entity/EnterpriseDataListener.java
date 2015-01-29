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
 * @author LeeKiatHaw
 */
public class EnterpriseDataListener {
    
    @PrePersist
    @PreUpdate
    public void PrePersistUpdate(EnterpriseData dataRecord){
        recordStartDate(dataRecord);
        recordEndDate(dataRecord);
    }
    
    public void recordStartDate(EnterpriseData dataRecord){
        if(dataRecord.getSTART_DATE() != null)
            return;
        DateTime today = new DateTime();
        java.sql.Date todaySQL = new java.sql.Date(today.getMillis());
        
        dataRecord.setSTART_DATE(todaySQL);
    }
    
    public void recordEndDate(EnterpriseData dataRecord){
        if(dataRecord.getEND_DATE() != null)
            return;
        DateTime today = new DateTime();
        java.sql.Date todaySQL = new java.sql.Date(today.getMillis());
        
        dataRecord.setEND_DATE(todaySQL);
    }
}
