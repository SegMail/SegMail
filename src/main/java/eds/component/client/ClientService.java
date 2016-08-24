/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.client;

import eds.component.GenericObjectService;
import eds.component.data.DBConnectionException;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import eds.component.user.UserService;
import eds.entity.client.Client;
import eds.entity.client.ClientUserAssignment;
import eds.entity.client.ClientResource;
import eds.entity.client.ClientResourceAssignment;
import eds.entity.client.ClientType;
import eds.entity.client.ContactInfo;
import eds.entity.data.EnterpriseObject;
import eds.entity.user.User;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import org.hibernate.exception.GenericJDBCException;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class ClientService {
    
    @EJB private GenericObjectService genericEnterpriseObjectService;
    @EJB private UserService userService;
    
    @PersistenceContext(name = "HIBERNATE")
    private EntityManager em;
    
    @Inject private ClientFacade clientFacade;
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Client getClientById(long clientid) throws DBConnectionException{
        try{
            //Try the shorter JPA way
            //Client result = em.find(Client.class, clientid);
            
            Client result = this.genericEnterpriseObjectService.getEnterpriseObjectById(clientid, Client.class);
            return result;
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ClientType getClientTypeById(long clienttypeid) throws DBConnectionException{
        try{
            
            ClientType result = this.genericEnterpriseObjectService.getEnterpriseObjectById(clienttypeid, ClientType.class);
            
            return result;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Client getClientByClientname(String clientname) throws DBConnectionException{
        try{
            //Try the shorter JPA way
            //Client result = em.find(Client.class, clientid);
            
            List<Client> results = this.genericEnterpriseObjectService.getEnterpriseObjectsByName(clientname, Client.class);
            
            return (results == null || results.size() <= 0) ? null : results.get(0);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ClientType getClientTypeByName(String clienttypename) throws DBConnectionException{
        try{
            
            List<ClientType> results = this.genericEnterpriseObjectService.getEnterpriseObjectsByName(clienttypename, ClientType.class);
            
            if(results == null || results.isEmpty())
                return null;
            
            //Return only the first matching result            
            return results.get(0);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void registerClientType(String clienttypename, String clienttypedesc)
        throws DBConnectionException, EntityExistsException, IncompleteDataException{
        
        try{
            //Validate the inputted clienttypename
            if(clienttypename == null || clienttypename.isEmpty())
                throw new IncompleteDataException("Client type name cannot be empty!");
            
            //Validate if client type already exist
            ClientType existingClientType = this.getClientTypeByName(clienttypename);
            if(existingClientType != null)
                throw new EntityExistsException(existingClientType);
            
            ClientType newClientType = new ClientType();
            newClientType.setCLIENT_TYPE_NAME(clienttypename);
            newClientType.setDESCRIPTION(clienttypedesc);
            
            this.em.persist(newClientType);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } 
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void registerClient(long clienttypeid, String clientname)
        throws IncompleteDataException, EntityExistsException, EntityNotFoundException{
        try{
            if(clientname == null || clientname.isEmpty())
                throw new IncompleteDataException("Client name cannot be empty.");
            
            Client existingClient = this.getClientByClientname(clientname);
            
            if(existingClient != null)
                throw new EntityExistsException(existingClient);
            
            ClientType clientType = this.getClientTypeById(clienttypeid);
            
            if(clientType == null)
                throw new EntityNotFoundException(ClientType.class,clienttypeid);
            
            Client newClient = new Client();
            newClient.setCLIENT_NAME(clientname);
            newClient.setCLIENTTYPE(clientType);
            
            em.persist(newClient);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } 
    }
    
    /**
     * Creates a Client object based on the user.alias() name. 
     * 
     * @param user
     * @param clienttypeid
     * @return
     * @throws IncompleteDataException
     * @throws EntityNotFoundException
     * @throws EntityExistsException
     * @throws RelationshipExistsException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ClientUserAssignment registerClientForUser(User user, long clienttypeid)
        throws IncompleteDataException, EntityNotFoundException, EntityExistsException, RelationshipExistsException{
            if(user == null)
                throw new IncompleteDataException("User not provided.");
            
            ClientType clientType = this.getClientTypeById(clienttypeid);
            
            if(clientType == null)
                throw new EntityNotFoundException(ClientType.class,clienttypeid);
            
            Client existingClient = this.getClientByClientname(user.alias());
            
            if(existingClient != null)
                throw new EntityExistsException(existingClient);
            
            List<ClientUserAssignment> existingAssignments = 
                    this.genericEnterpriseObjectService.getRelationshipsForTargetObject(user.getOBJECTID(), ClientUserAssignment.class);
            
            if(existingAssignments != null && existingAssignments.size() > 0)
                throw new RelationshipExistsException(existingAssignments.get(0));
            
            //Create the client object
            Client newClient = new Client();
            newClient.setCLIENT_NAME(user.alias());
            newClient.setCLIENTTYPE(clientType);
            
            this.em.persist(newClient);
            
            //Assign the client object to the enterpriseobject
            ClientUserAssignment newAssignment = new ClientUserAssignment(newClient,user);
            
            this.em.persist(newAssignment);
            
            return newAssignment;
          
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ContactInfo getContactInfoForUser(long userid) throws DBConnectionException{
        try{
            List<ClientUserAssignment> clientAssignment =
                    this.genericEnterpriseObjectService.getRelationshipsForTargetObject(userid, ClientUserAssignment.class);
            
            //Cannot find Client object
            if(clientAssignment == null || clientAssignment.isEmpty())
                return null;
            
            //Only get the first result
            Client client = clientAssignment.get(0).getSOURCE();
            
            DateTime today = new DateTime();
            java.sql.Date todaySQL = new java.sql.Date(today.getMillis());
            
            List<ContactInfo> contactInfos = 
                    this.genericEnterpriseObjectService
                            .getEnterpriseDataForObject(client.getOBJECTID(), todaySQL, todaySQL, ContactInfo.class);
            
            if(contactInfos == null || contactInfos.isEmpty())
                return null;
            
            //At this point, we tend to just return the 1st object in the list,
            //disregarding the sequence number, time constraints, etc.
            return contactInfos.get(0);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateClientContact(ContactInfo contactInfo) 
            throws DBConnectionException{
        try{
            //if(contactInfo.getOWNER() == null)
            //    throw new MissingOwnerException(contactInfo);
            //em.merge() will insert new if doesn't exist, but need to set
            //the owner with the managed instance
            ContactInfo ci = this.em.merge(contactInfo); //ci is the one getting persisted and managed actually
            
            //For newly created ContactInfo
            if(ci.getOWNER() == null)
                ci.setOWNER(this.clientFacade.getClient());
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } //You don't have to catch everything, that's counter-productive@
        //Just catch whatever you know, and let the other errors propagate up
        //so that they can be "glaring", not hidden.
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Client getClientByAssignedUser(long userid) throws DBConnectionException{
        try{
            List<ClientUserAssignment> results = this.genericEnterpriseObjectService.getRelationshipsForTargetObject(userid, ClientUserAssignment.class);
            
            if(results == null || results.isEmpty())
                return null;
            
            return results.get(0).getSOURCE();
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ClientResourceAssignment assignClientResource(Client client, EnterpriseObject clientResource){
        try {
            if(client == null )
                throw new RuntimeException("Client is null.");
            if(clientResource == null )
                throw new RuntimeException("ClientResource is null.");
            if(!clientResource.getClass().isAnnotationPresent(ClientResource.class))
                throw new RuntimeException("EntepriseObject type "+clientResource.getClass().getSimpleName()+ " is not a Client Resource.");
            
            ClientResourceAssignment newAssign = new ClientResourceAssignment();
            newAssign.setSOURCE(client);
            newAssign.setTARGET(clientResource);
            
            em.persist(newAssign);
            return newAssign;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
