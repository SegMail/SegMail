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
import eds.component.data.MissingOwnerException;
import eds.component.user.UserService;
import eds.entity.user.User;
import eds.entity.user.User_;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.apache.commons.validator.routines.UrlValidator;
import org.hibernate.exception.GenericJDBCException;
import seca2.entity.landing.Assign_Server_User;
import seca2.entity.landing.Assign_Server_User_;
import seca2.entity.landing.ServerInstance;
import seca2.entity.landing.ServerInstance_;
import seca2.entity.landing.ServerResource;
import seca2.entity.landing.ServerResourceType;
import seca2.entity.landing.ServerResource_;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class LandingService {

    public final String SERVER_NAME = "LandingService.SERVER_NAME";

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

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<ServerInstance> getServerInstances(ServerNodeType type) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<ServerInstance> query = builder.createQuery(ServerInstance.class);
        Root<ServerInstance> fromServer = query.from(ServerInstance.class);

        query.select(fromServer);
        query.where(builder.equal(fromServer.get(ServerInstance_.SERVER_NODE_TYPE), type.value));

        List<ServerInstance> results = objectService.getEm().createQuery(query)
                .getResultList();

        return results;
    }

    /**
     *
     * @param name
     * @param uri
     * @param userId
     * @return
     * @throws EntityNotFoundException if userId is not found.
     * @throws IncompleteDataException if name or uri are not provided.
     * @throws EntityExistsException if there is already a ServerInstance with
     * the same name.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ServerInstance addServerInstance(String name, String uri, long userId, ServerNodeType type)
            throws EntityNotFoundException, IncompleteDataException, EntityExistsException, DataValidationException, URISyntaxException {
        try {
            if (name == null || name.isEmpty()) {
                throw new IncompleteDataException("Name must not be empty.");
            }

            //List<ServerInstance> existingServers = objectService.getEnterpriseObjectsByName(name,ServerInstance.class);
            //if (existingServers != null && !existingServers.isEmpty())
            //    throw new EntityExistsException(existingServers.get(0));
            if (uri == null || uri.isEmpty()) {
                throw new IncompleteDataException("You cannot add a server without an address!");
            }

            User user = userService.getUserById(userId);
            if (user == null) {
                throw new EntityNotFoundException(ServerInstance.class, userId);
            }

            //Create new serverInstance
            ServerInstance newInstance = new ServerInstance();
            newInstance.setURI(uri);
            newInstance.setNAME(name);
            newInstance.setSERVER_NODE_TYPE(type.value);

            this.validateServer(newInstance);

            updateService.getEm().persist(newInstance);

            Assign_Server_User assignment = new Assign_Server_User(newInstance, user);

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
        if (user == null) {
            throw new EntityNotFoundException(User.class, userId);
        }

        ServerInstance server = objectService.getEnterpriseObjectById(serverId, ServerInstance.class);
        if (server == null) {
            throw new EntityNotFoundException(ServerInstance.class, serverId);
        }

        Assign_Server_User newAssignment = new Assign_Server_User(server, user);
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
            throws DataValidationException, URISyntaxException, EntityExistsException {
        this.validateServer(server);
        updateService.getEm().merge(server);
    }

    /**
     * This is a generator method that produces the next server instance based
     * on a LandingServerGenerationStrategy.
     *
     * @param strategy
     * @param type
     * @return
     * @throws eds.component.data.IncompleteDataException if no servers are
     * found
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ServerInstance getNextServerInstance(LandingServerGenerationStrategy strategy, ServerNodeType type)
            throws IncompleteDataException {
            //Currently there's no strategy, just take the first one.
        //This is when the user can set their own landing servers
        //List<ServerInstance> servers = objectService.getAllSourceObjectsFromTarget(userId, Assign_Server_User.class, ServerInstance.class);
        //Only the system admin can set landing servers, so no point retrieving by assignment
        List<ServerInstance> servers = objectService.getAllEnterpriseObjects(ServerInstance.class);
            //if(servers == null || servers.isEmpty())

        for (ServerInstance server : servers) {
            if (type.value.equals(server.getSERVER_NODE_TYPE())) {
                return server;
            }
        }

        throw new IncompleteDataException("No Servers found, please contact your administrators to set a valid ServerInstance first.");

    }

    /**
     * Deletes the server and all its assignments: - Assign_Server_User
     *
     * @param serverId
     * @throws eds.component.data.EntityNotFoundException if the serverId is not
     * found
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteServer(long serverId)
            throws EntityNotFoundException {
        updateService.deleteObjectDataAndRelationships(serverId);
    }

    //Wrong return param - should return a list or use "Contains"
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public User getUserFromServerName(String serverName) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);

        Root<ServerInstance> fromServer = query.from(ServerInstance.class);
        Root<Assign_Server_User> fromAssign = query.from(Assign_Server_User.class);
        Root<User> fromUser = query.from(User.class);

        query.select(fromUser).where(builder.and(builder.equal(fromServer.get(ServerInstance_.NAME), serverName),
                builder.equal(
                        fromServer.get(ServerInstance_.OBJECTID),
                        fromAssign.get(Assign_Server_User_.SOURCE)),
                builder.equal(
                        fromAssign.get(Assign_Server_User_.TARGET),
                        fromUser.get(User_.OBJECTID))
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
    public void resolveAndUpdateIPHostnamePath(ServerInstance server)
            throws DataValidationException, URISyntaxException {
        try {
            //Everything comes from URI
            String uriString = server.getURI();
            if (uriString == null || uriString.isEmpty()) {
                throw new DataValidationException("URI cannot be empty.");
            }

            URI uri = new URI(uriString);

            String path = uri.getPath();
            server.setPATH(path);

            String hostname = uri.getHost();
            server.setHOSTNAME(hostname);

            int port = uri.getPort();
            server.setPORT(port);

            InetAddress address = InetAddress.getByName(hostname);
            server.setIP_ADDRESS(address.getHostAddress());

        } catch (UnknownHostException ex) {
            throw new DataValidationException("IP address cannot be resolved: " + ex.getMessage());
        }
    }

    public void validateURL(ServerInstance server) throws DataValidationException {
        String[] schemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.ALLOW_LOCAL_URLS);
        String url = server.getURI();

        if (url == null || url.isEmpty()) {
            throw new DataValidationException("URL cannot be empty.");
        }

        //remove trailing slash
        if (url.endsWith("/") && url.length() >= 3) {
            url = url.substring(0, url.length() - 1);
        }

        //If the URL doesn't start with any of the schemes, prepend default http://
        boolean match = false;
        for (String scheme : schemes) {
            if (url.startsWith(scheme)) {
                match = true;
            }
        }
        if (!match) {
            url = schemes[0] + "://" + url;
        }

        server.setURI(url);

        if (!urlValidator.isValid(url)) {
            throw new DataValidationException("Invalid URL: " + server.getURI());
        }
    }

    /**
     * Chain of validations for server. - validateURL -
     * resolveAndUpdateIPHostnamePath - checkDuplicate
     *
     * @param server
     * @throws DataValidationException
     * @throws java.net.URISyntaxException
     * @throws eds.component.data.EntityExistsException
     */
    public void validateServer(ServerInstance server)
            throws DataValidationException, URISyntaxException, EntityExistsException {
        validateURL(server);
        resolveAndUpdateIPHostnamePath(server);
        checkDuplicatedServerName(server);
    }

    /**
     *
     * @param server
     * @throws eds.component.data.EntityExistsException
     */
    public void checkDuplicatedServerName(ServerInstance server) throws EntityExistsException {
        List<ServerInstance> servers = objectService.getEnterpriseObjectsByName(server.getNAME(), ServerInstance.class);
        if (servers != null) {
            for (ServerInstance s : servers) {
                if (s.getOBJECTID() != server.getOBJECTID()) {
                    throw new EntityExistsException(s);
                }
            }
        }
    }

    /**
     * Assuming that every server can only have 1 resource.
     *
     * @param resource
     * @return
     * @throws MissingOwnerException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ServerResource updateOrAddResourceForServer(ServerResource resource) throws MissingOwnerException {
        if (resource.getOWNER() == null) {
            throw new MissingOwnerException(resource);
        }

        //List<ServerResource> existingResources = this.getServerResource(
        //        resource.getOWNER().getOBJECTID(), ServerResourceType.valueOf(resource.getRESOURCE_TYPE()));
        ServerResource existing = objectService.getEnterpriseDataForObject(resource.getOWNER().getOBJECTID(), resource.getSTART_DATE(), resource.getEND_DATE(), resource.getSNO(), ServerResource.class);
        if (existing != null) {
            return updateService.getEm().merge(resource);
        }

        updateService.getEm().persist(resource);
        return resource;
    }

    public List<ServerResource> getServerResource(long serverId, ServerResourceType type) {
        CriteriaBuilder builder = updateService.getEm().getCriteriaBuilder();
        CriteriaQuery<ServerResource> query = builder.createQuery(ServerResource.class);
        Root<ServerResource> fromServer = query.from(ServerResource.class);

        query.select(fromServer);
        query.where(builder.and(
                builder.equal(fromServer.get(ServerResource_.OWNER), serverId),
                builder.equal(fromServer.get(ServerResource_.RESOURCE_TYPE), type.label)
        ));

        List<ServerResource> results = updateService.getEm().createQuery(query)
                .getResultList();

        return results;
    }

    /**
     * If the server does not have an existing JMS connection, return a fresh
     * new one.
     *
     * @param serverId
     * @return
     */
    public ServerResource getServerJMSConnection(long serverId) {
        List<ServerResource> results = getServerResource(serverId, ServerResourceType.JMS_CONNECTION);
        if (results == null || results.isEmpty()) {
            ServerResource newJMSConn = new ServerResource();
            newJMSConn.setRESOURCE_TYPE(ServerResourceType.JMS_CONNECTION);

            return newJMSConn;
        }

        return results.get(0); //Assume there's only 1
    }

    public String getOwnServerName() {
        return System.getProperty(SERVER_NAME);
    }

    public ServerInstance getOwnServerInstance() {
        List<ServerInstance> servers = objectService.getEnterpriseObjectsByName(this.getOwnServerName(), ServerInstance.class);
        if (servers == null || servers.isEmpty()) {
            return null;
        }

        return servers.get(0);
    }
}
