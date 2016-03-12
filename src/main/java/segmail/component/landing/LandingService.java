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
import eds.component.data.IncompleteDataException;
import eds.component.user.UserService;
import eds.entity.user.User;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.PersistenceException;
import org.hibernate.exception.GenericJDBCException;
import segmail.entity.landing.Assign_Server_User;
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
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
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
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
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
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ServerInstance addServerInstance(String name, String address, long userId) 
            throws EntityNotFoundException, IncompleteDataException {
        try {
            if(name == null || name.isEmpty())
                throw new IncompleteDataException("Name must not be empty.");
            
            if(address == null || address.isEmpty())
                throw new IncompleteDataException("You cannot add a server without an address!");
            
            User user = userService.getUserById(userId);
            if(user == null)
                throw new EntityNotFoundException(ServerInstance.class, userId);
            
            //Create new serverInstance
            ServerInstance newInstance = new ServerInstance();
            newInstance.setADDRESS(address);
            newInstance.setNAME(name);
            
            updateService.getEm().persist(newInstance);
            
            Assign_Server_User assignment = new Assign_Server_User(newInstance,user);
            
            updateService.getEm().persist(assignment);
            
            return newInstance;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }
    
    /**
     * This is a generator method that produces the next server instance based
     * on a LandingServerGenerationStrategy.
     * 
     * @param strategy
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ServerInstance getNextServerInstance(LandingServerGenerationStrategy strategy) throws IncompleteDataException {
        try {
            //Currently there's no strategy, just take the first one.
            //This is when the user can set their own landing servers
            //List<ServerInstance> servers = objectService.getAllSourceObjectsFromTarget(userId, Assign_Server_User.class, ServerInstance.class);
            //Only the system admin can set landing servers, so no point retrieving by assignment
            List<ServerInstance> servers = objectService.getAllEnterpriseObjects(ServerInstance.class);
            if(servers == null || servers.isEmpty())
                throw new IncompleteDataException("No Servers found, please contact your administrators to set a valid ServerInstance first.");
            
            return servers.get(0);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }
    
}
