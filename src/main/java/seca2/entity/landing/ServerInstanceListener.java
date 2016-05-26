/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.entity.landing;

import eds.component.data.DataValidationException;
import java.net.URISyntaxException;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 *
 * @author LeeKiatHaw
 */
public class ServerInstanceListener {
    
    @PrePersist
    public void prePersist(ServerInstance server) {
        
    }
    
    @PreUpdate
    public void preUpdate(ServerInstance server) throws DataValidationException, URISyntaxException {
        
    }
}
