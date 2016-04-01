/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.component.landing;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.data.DBConnectionException;
import eds.component.data.DataValidationException;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.user.UserService;
import eds.entity.user.User;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.exception.GenericJDBCException;
import seca2.entity.landing.Assign_Server_User;
import seca2.entity.landing.Assign_Server_User_;
import seca2.entity.landing.ServerInstance;
import seca2.entity.landing.ServerInstance_;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class LandingService {

    @EJB
    private GenericObjectService objectService;
    @EJB
    private UserService userService;
    @EJB
    private UpdateObjectService updateService;

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

    /**
     * 
     * @param name
     * @param hostname
     * @param userId
     * @return
     * @throws EntityNotFoundException if userId is not found.
     * @throws IncompleteDataException if name or hostname are not provided.
     * @throws EntityExistsException if there is already a ServerInstance with the same name.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ServerInstance addServerInstance(String name, String hostname, long userId)
            throws EntityNotFoundException, IncompleteDataException, EntityExistsException, DataValidationException {
        try {
            if (name == null || name.isEmpty()) {
                throw new IncompleteDataException("Name must not be empty.");
            }
            
            List<ServerInstance> existingServers = objectService.getEnterpriseObjectsByName(name,ServerInstance.class);
            if (existingServers != null && !existingServers.isEmpty())
                throw new EntityExistsException(existingServers.get(0));

            if (hostname == null || hostname.isEmpty()) {
                throw new IncompleteDataException("You cannot add a server without an address!");
            }

            User user = userService.getUserById(userId);
            if (user == null) {
                throw new EntityNotFoundException(ServerInstance.class, userId);
            }

            //Create new serverInstance
            ServerInstance newInstance = new ServerInstance();
            newInstance.setHOSTNAME(hostname);
            newInstance.setNAME(name);

            this.validateServer(newInstance);
            
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
     * Assume only 1 assignment at the moment.
     *
     * @param serverId
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Assign_Server_User getServerUserAssignment(long serverId) {
        List<Assign_Server_User> assignments = objectService.getRelationshipsForSourceObject(serverId, Assign_Server_User.class);

        return (assignments == null || assignments.isEmpty()) ? null : assignments.get(0);
    }

    /**
     * Deletes all existing assignment and re-assign to the only one provided.
     * Only 1 user can be assigned to a server.
     *
     * @param userId
     * @param serverId
     * @return
     * @throws eds.component.data.EntityNotFoundException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Assign_Server_User assignUserToServer(long userId, long serverId) 
            throws EntityNotFoundException {
        List<Assign_Server_User> assignments = objectService.getRelationshipsForSourceObject(serverId, Assign_Server_User.class);
        
        for (Assign_Server_User assignment : assignments) {
            updateService.getEm().remove(
                    updateService.getEm().contains(assignment)
                            ? assignment : updateService.getEm().merge(assignment));
        }
        
        User user = objectService.getEnterpriseObjectById(userId, User.class);
        if(user == null)
            throw new EntityNotFoundException(User.class,userId);
        
        ServerInstance server = objectService.getEnterpriseObjectById(serverId, ServerInstance.class);
        if(server == null)
            throw new EntityNotFoundException(ServerInstance.class,serverId);
        
        Assign_Server_User newAssignment = new Assign_Server_User(server,user);
        updateService.getEm().persist(newAssignment);
        
        return newAssignment;
    }
    
    /**
     * Update method for ServerInstance. Validates first with validateServer().
     * 
     * @param server
     * @throws DataValidationException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveServer(ServerInstance server) 
            throws DataValidationException{
        this.validateServer(server);
        updateService.getEm().merge(server);
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
    
    /**
     * Deletes the server and all its assignments:
     * - Assign_Server_User
     * 
     * @param serverId 
     * @throws eds.component.data.EntityNotFoundException if the serverId is not found
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteServer(long serverId) 
            throws EntityNotFoundException{
        updateService.deleteObjectDataAndRelationships(serverId);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public User getUserFromServerAddress(String ipAddress) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        
        Root<ServerInstance> fromServer = query.from(ServerInstance.class);
        Root<Assign_Server_User> fromAssign = query.from(Assign_Server_User.class);
        Root<User> fromUser = query.from(User.class);
        
        query.select(fromUser).where(builder.and(builder.equal(fromServer.get(ServerInstance_.IP_ADDRESS), ipAddress),
                        builder.equal(
                                fromServer.get(ServerInstance_.OBJECTID), 
                                fromAssign.get(Assign_Server_User_.SOURCE))
                )
        );
        
        List<User> results = objectService.getEm().createQuery(query)
                .getResultList();
        
        return (results != null && !results.isEmpty()) ? results.get(0) : null;
    }
    
    /**
     * 
     * @param server
     * @throws DataValidationException 
     */
    public void resolveAndUpdateIP(ServerInstance server) throws DataValidationException {
        try {
            String hostname = server.getHOSTNAME();
            InetAddress address = InetAddress.getByName(hostname);
            
            server.setIP_ADDRESS(address.getHostAddress());
            
        } catch (UnknownHostException ex) {
            /*Logger.getLogger(ServerIPResolverListener.class.getName()).log(Level.SEVERE, null, ex);
            server.setADDRESS("");*/
            throw new DataValidationException("IP address cannot be resolved: "+ex.getMessage());
        } 
    }
    
    /**
     * Chain of validations for server. 
     * 
     * @param server
     * @throws DataValidationException 
     */
    public void validateServer(ServerInstance server) throws DataValidationException {
        resolveAndUpdateIP(server);
    }
}
