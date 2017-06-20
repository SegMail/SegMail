/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.datasource;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 *
 * @author LeeKiatHaw
 */
public class ListDatasourceListener {
    
    
    @PrePersist
    public void prePersist(ListDatasource ld) {
        //updateURL(ld);
        updatePort(ld);
    }
    
    @PreUpdate
    public void preUpdate(ListDatasource ld) {
        //updateURL(ld);
        updatePort(ld);
    }
    
    /**
     * Removes any prefixes that ld.ENDPOINT_URL might have and replace them
     * by those described from its ld.ENDPOINT_TYPE.
     * 
     * @param ld 
     */
    public void updateURL(ListDatasource ld) {
        if(ld.getENDPOINT_TYPE() != null && !ld.getENDPOINT_TYPE().isEmpty()) {
            DATASOURCE_ENDPOINT_TYPE type = DATASOURCE_ENDPOINT_TYPE.valueOf(ld.getENDPOINT_TYPE());
            
            //Remove any prefixes first
            if(ld.getSERVER_NAME().contains("//")) {
                int start = ld.getSERVER_NAME().lastIndexOf("//") + 2;
                start = Math.min(start, ld.getSERVER_NAME().length());
                String removedURL = ld.getSERVER_NAME().substring(start);
                
                ld.setSERVER_NAME(removedURL);
            }
            
            ld.setSERVER_NAME(type.prefix + ld.getSERVER_NAME());
        }
    }
    
    /**
     * If port is an invalid value ie. not between 1 - 9999, then use the available
     * ENDPOINT_TYPE to set the port value.
     * 
     * @param ld 
     */
    public void updatePort(ListDatasource ld) {
        //if(ld.getPORT() <= 0 || ld.getPORT() >= 10000) { //invalid values
            if(ld.getENDPOINT_TYPE() != null && !ld.getENDPOINT_TYPE().isEmpty()) {
                DATASOURCE_ENDPOINT_TYPE type = DATASOURCE_ENDPOINT_TYPE.valueOf(ld.getENDPOINT_TYPE());
                
                ld.setPORT(type.port);
            }
        //}
        //Remove the port from URL
        
    }
    
}
