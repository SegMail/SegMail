/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.landing;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.data.DBConnectionException;
import eds.component.data.EntityNotFoundException;
import eds.component.user.UserService;
import eds.entity.user.User;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.PersistenceException;
import org.hibernate.exception.GenericJDBCException;
import segmail.entity.landing.AssignServerUser;
import segmail.entity.landing.ServerInstance;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class LandingService {
    
    @EJB private GenericObjectService objectService;
    @EJB private UserService userService;
    @EJB private UpdateObjectService updateService;
    
    public List<ServerInstance> getServerInstances() {
        try {
            return objectService.getAllEnterpriseObjects(ServerInstance.class);
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }
    
    public ServerInstance getServerInstance(long serverId) {
        try {
            return objectService.getEnterpriseObjectById(serverId, ServerInstance.class);
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }
    
    public ServerInstance addServerInstance(String address, long userId) 
            throws EntityNotFoundException {
        try {
            User user = userService.getUserById(userId);
            if(user == null)
                throw new EntityNotFoundException(ServerInstance.class, userId);
            
            //Create new serverInstance
            ServerInstance newInstance = new ServerInstance();
            newInstance.setADDRESS(address);
            
            updateService.getEm().persist(newInstance);
            
            AssignServerUser assignment = new AssignServerUser(newInstance,user);
            
            updateService.getEm().persist(assignment);
            
            return newInstance;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }
}
