/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.entity.landing;

import eds.component.data.DataValidationException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 *
 * @author LeeKiatHaw
 */
public class ServerIPResolverListener {
    
    @PrePersist
    public void prePersist(ServerInstance server) throws DataValidationException {
        resolveAndUpdateIP(server);
    }
    
    @PreUpdate
    public void preUpdate(ServerInstance server) throws DataValidationException {
        resolveAndUpdateIP(server);
    }
    
    public void resolveAndUpdateIP(ServerInstance server) throws DataValidationException {
        try {
            String hostname = server.getHOSTNAME();
            InetAddress address = InetAddress.getByName(hostname);
            
            server.setIP_ADDRESS(address.getHostAddress());
            
        } catch (UnknownHostException ex) {
            /*Logger.getLogger(ServerIPResolverListener.class.getName()).log(Level.SEVERE, null, ex);
            server.setADDRESS("");*/
            throw new DataValidationException("IP address is not resolved: "+ex.getMessage());
        } 
    }
}
