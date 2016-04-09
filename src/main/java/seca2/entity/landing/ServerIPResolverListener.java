/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.entity.landing;

import eds.component.data.DataValidationException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import seca2.component.landing.LandingService;

/**
 *
 * @author LeeKiatHaw
 */
public class ServerIPResolverListener {
    
    @EJB
    private LandingService landingService; //Injection still doesn't work!
    
    @PrePersist
    public void prePersist(ServerInstance server) throws DataValidationException, URISyntaxException {
        //landingService.resolveAndUpdateIPHostnamePath(server);
    }
    
    @PreUpdate
    public void preUpdate(ServerInstance server) throws DataValidationException, URISyntaxException {
        //landingService.resolveAndUpdateIPHostnamePath(server);
    }
    
}
