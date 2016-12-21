/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.client;

import com.amazonaws.auth.BasicAWSCredentials;
import eds.component.GenericObjectService;
import eds.component.data.DataValidationException;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import eds.component.mail.Password;
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
import org.apache.commons.validator.routines.EmailValidator;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class ClientService {

    @EJB
    private GenericObjectService objService;
    @EJB
    private UserService userService;

    @Inject
    private ClientFacade clientFacade;

    @Inject
    @Password
    BasicAWSCredentials awsCredentials;

    public Client getClientById(long clientid) {
            //Try the shorter JPA way
        //Client userResult = em.find(Client.class, clientid);

        Client result = this.objService.getEnterpriseObjectById(clientid, Client.class);
        return result;
    }

    public ClientType getClientTypeById(long clienttypeid) {
        ClientType result = this.objService.getEnterpriseObjectById(clienttypeid, ClientType.class);

        return result;
    }

    public Client getClientByClientname(String clientname) {
            //Try the shorter JPA way
        //Client userResult = em.find(Client.class, clientid);

        List<Client> results = this.objService.getEnterpriseObjectsByName(clientname, Client.class);

        return (results == null || results.size() <= 0) ? null : results.get(0);

    }

    public ClientType getClientTypeByName(String clienttypename) {

        List<ClientType> results = this.objService.getEnterpriseObjectsByName(clienttypename, ClientType.class);

        if (results == null || results.isEmpty()) {
            return null;
        }

        //Return only the first matching userResult            
        return results.get(0);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void registerClientType(String clienttypename, String clienttypedesc)
            throws EntityExistsException, IncompleteDataException {
        //Validate the inputted clienttypename
        if (clienttypename == null || clienttypename.isEmpty()) {
            throw new IncompleteDataException("Client type name cannot be empty!");
        }

        //Validate if client type already exist
        ClientType existingClientType = this.getClientTypeByName(clienttypename);
        if (existingClientType != null) {
            throw new EntityExistsException(existingClientType);
        }

        ClientType newClientType = new ClientType();
        newClientType.setCLIENT_TYPE_NAME(clienttypename);
        newClientType.setDESCRIPTION(clienttypedesc);

        objService.getEm().persist(newClientType);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void registerClient(long clienttypeid, String clientname)
            throws IncompleteDataException, EntityExistsException, EntityNotFoundException {
        if (clientname == null || clientname.isEmpty()) {
            throw new IncompleteDataException("Client name cannot be empty.");
        }

        Client existingClient = this.getClientByClientname(clientname);

        if (existingClient != null) {
            throw new EntityExistsException(existingClient);
        }

        ClientType clientType = this.getClientTypeById(clienttypeid);

        if (clientType == null) {
            throw new EntityNotFoundException(ClientType.class, clienttypeid);
        }

        Client newClient = new Client();
        newClient.setCLIENT_NAME(clientname);
        newClient.setCLIENTTYPE(clientType);

        objService.getEm().persist(newClient);
    }

    /**
     * Creates a Client object based on the newOrExistingUser.alias() name.
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
            throws IncompleteDataException, EntityNotFoundException, EntityExistsException, RelationshipExistsException {
        if (user == null) {
            throw new IncompleteDataException("User not provided.");
        }

        ClientType clientType = this.getClientTypeById(clienttypeid);

        if (clientType == null) {
            throw new EntityNotFoundException(ClientType.class, clienttypeid);
        }

        Client existingClient = this.getClientByClientname(user.alias());

        if (existingClient != null) {
            throw new EntityExistsException(existingClient);
        }

        List<ClientUserAssignment> existingAssignments
                = this.objService.getRelationshipsForTargetObject(user.getOBJECTID(), ClientUserAssignment.class);

        if (existingAssignments != null && existingAssignments.size() > 0) {
            throw new RelationshipExistsException(existingAssignments.get(0));
        }

        //Create the client object
        Client newClient = new Client();
        newClient.setCLIENT_NAME(user.alias());
        newClient.setCLIENTTYPE(clientType);

        objService.getEm().persist(newClient);

        //Assign the client object to the enterpriseobject
        ClientUserAssignment newAssignment = new ClientUserAssignment(newClient, user);

        objService.getEm().persist(newAssignment);

        return newAssignment;

    }

    public ContactInfo getContactInfoForUser(long userid) {
        List<ClientUserAssignment> clientAssignment
                = this.objService.getRelationshipsForTargetObject(userid, ClientUserAssignment.class);

        //Cannot find Client object
        if (clientAssignment == null || clientAssignment.isEmpty()) {
            return null;
        }

        //Only get the first userResult
        Client client = clientAssignment.get(0).getSOURCE();

        DateTime today = new DateTime();
        java.sql.Date todaySQL = new java.sql.Date(today.getMillis());

        List<ContactInfo> contactInfos
                = this.objService
                .getEnterpriseDataForObject(client.getOBJECTID(), null, null, ContactInfo.class);

        if (contactInfos == null || contactInfos.isEmpty()) {
            return null;
        }

            //At this point, we tend to just return the 1st object in the list,
        //disregarding the sequence number, time constraints, etc.
        return contactInfos.get(0);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ContactInfo createClientContact(ContactInfo contactInfo) {
        objService.getEm().persist(contactInfo);
        return contactInfo;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateClientContact(ContactInfo contactInfo)
            throws DataValidationException {

        //Validate 
        String email = contactInfo.getEMAIL();
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new DataValidationException("Email address is invalid.");
        }
        //em.merge() will insert new if doesn't exist, but need to set
        //the owner with the managed instance

        ContactInfo ci = objService.getEm().merge(contactInfo); //ci is the one getting persisted and managed actually

        //For newly created ContactInfo
        if (ci.getOWNER() == null) {
            ci.setOWNER(this.clientFacade.getClient());
        }

    }

    public Client getClientByAssignedUser(long userid) {
        List<ClientUserAssignment> results = this.objService.getRelationshipsForTargetObject(userid, ClientUserAssignment.class);

        if (results == null || results.isEmpty()) {
            return null;
        }

        return results.get(0).getSOURCE();

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ClientResourceAssignment assignClientResource(Client client, EnterpriseObject clientResource) {
        if (client == null) {
            throw new RuntimeException("Client is null.");
        }
        if (clientResource == null) {
            throw new RuntimeException("ClientResource is null.");
        }
        if (!clientResource.getClass().isAnnotationPresent(ClientResource.class)) {
            throw new RuntimeException("EntepriseObject type " + clientResource.getClass().getSimpleName() + " is not a Client Resource.");
        }

        ClientResourceAssignment newAssign = new ClientResourceAssignment();
        newAssign.setSOURCE(client);
        newAssign.setTARGET(clientResource);

        objService.getEm().persist(newAssign);
        return newAssign;

    }

}
