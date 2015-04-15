/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.client;

import eds.component.GenericEnterpriseObjectService;
import eds.component.data.DBConnectionException;
import eds.component.data.MissingOwnerException;
import eds.component.user.UserService;
import eds.entity.EnterpriseObject;
import eds.entity.client.Client;
import eds.entity.client.ClientAssignment;
import eds.entity.client.ClientType;
import eds.entity.client.ContactInfo;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
    
    @EJB private GenericEnterpriseObjectService genericEnterpriseObjectService;
    @EJB private UserService userService;
    
    @PersistenceContext(name = "HIBERNATE")
    private EntityManager em;
    
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
        throws DBConnectionException, ClientTypeRegistrationException{
        
        try{
            //Validate the inputted clienttypename
            if(clienttypename == null || clienttypename.isEmpty())
                throw new ClientTypeRegistrationException("Client type name cannot be empty!");
            
            //Validate if client type already exist
            ClientType existingClientType = this.getClientTypeByName(clienttypename);
            if(existingClientType != null)
                throw new ClientTypeRegistrationException("Client type "+clienttypename+" already exists!");
            
            ClientType newClientType = new ClientType();
            newClientType.setCLIENT_TYPE_NAME(clienttypename);
            newClientType.setDESCRIPTION(clienttypedesc);
            
            this.em.persist(newClientType);
            
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
    public void registerClient(long clienttypeid, String clientname)
        throws DBConnectionException, ClientRegistrationException{
        try{
            if(clientname == null || clientname.isEmpty())
                throw new ClientRegistrationException("Client name cannot be empty.");
            
            Client existingClient = this.getClientByClientname(clientname);
            
            if(existingClient != null)
                throw new ClientRegistrationException("Client "+clientname+" already exist.");
            
            ClientType clientType = this.getClientTypeById(clienttypeid);
            
            if(clientType == null)
                throw new ClientRegistrationException("ClientType "+clienttypeid+" doesn't exist.");
            
            Client newClient = new Client();
            newClient.setCLIENT_NAME(clientname);
            newClient.setCLIENTTYPE(clientType);
            
            em.persist(newClient);
            
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
    public void registerClientForObject(EnterpriseObject enterpriseobject, long clienttypeid)
        throws DBConnectionException, ClientRegistrationException{
        try{
            
            if(enterpriseobject == null)
                throw new ClientRegistrationException("Object "+enterpriseobject.getOBJECT_NAME()+" does not exist.");
            
            ClientType clientType = this.getClientTypeById(clienttypeid);
            
            if(clientType == null)
                throw new ClientRegistrationException("Client type "+clienttypeid+" doesn not exist.");
            
            Client existingClient = this.getClientByClientname(enterpriseobject.alias());
            
            if(existingClient != null)
                throw new ClientRegistrationException("Client "+existingClient.alias()+" already exist.");
            
            List<ClientAssignment> existingAssignments = 
                    this.genericEnterpriseObjectService.getRelationshipsForTargetObject(enterpriseobject.getOBJECTID(), ClientAssignment.class);
            
            if(existingAssignments != null && existingAssignments.size() > 0)
                throw new ClientRegistrationException("Object is already assigned to a client.");
            
            //Create the client object
            Client newClient = new Client();
            newClient.setCLIENT_NAME(enterpriseobject.alias());
            newClient.setCLIENTTYPE(clientType);
            
            this.em.persist(newClient);
            
            //Assign the client object to the enterpriseobject
            ClientAssignment newAssignment = new ClientAssignment(newClient,enterpriseobject);
            
            this.em.persist(newAssignment);
            
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
    public ContactInfo getContactInfoForObject(long objectid) throws DBConnectionException{
        try{
            List<ClientAssignment> clientAssignment =
                    this.genericEnterpriseObjectService.getRelationshipsForTargetObject(objectid, ClientAssignment.class);
            
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
            throws DBConnectionException, MissingOwnerException{
        try{
            if(contactInfo.getOWNER() == null)
                throw new MissingOwnerException(contactInfo);
            //em.merge() will insert new if doesn't exist, but need to set
            //the owner with the managed instance
            ContactInfo ci = this.em.merge(contactInfo); //ci is the one getting persisted and managed actually
            
            if(ci.getOWNER() == null)
                ci.setOWNER(contactInfo.getOWNER());
            
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
    public Client getClientByAssignedObjectId(long objectid) throws DBConnectionException{
        try{
            List<ClientAssignment> results = this.genericEnterpriseObjectService.getRelationshipsForTargetObject(objectid, ClientAssignment.class);
            
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
}
