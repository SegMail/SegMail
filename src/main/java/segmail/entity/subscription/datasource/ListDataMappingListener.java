/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.datasource;

import javax.persistence.PrePersist;

/**
 *
 * @author LeeKiatHaw
 */
public class ListDataMappingListener {
    
    @PrePersist
    public void prePersist(ListDataMapping mapping) {
        populateEmptyStrings(mapping);
    }
    
    /**
     * For defect https://github.com/SegMail/SegMail/issues/158
     * 
     * @param mapping 
     */
    public void populateEmptyStrings(ListDataMapping mapping) {
        if (mapping.getFOREIGN_NAME() == null)
            mapping.setFOREIGN_NAME("");
        
        if (mapping.getKEY_NAME() == null)
            mapping.setKEY_NAME("");
        
        if (mapping.getLOCAL_NAME() == null)
            mapping.setLOCAL_NAME("");
    }
}
