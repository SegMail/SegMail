/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.client;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.AccessKey;
import com.amazonaws.services.identitymanagement.model.AccessKeyMetadata;
import com.amazonaws.services.identitymanagement.model.AddUserToGroupRequest;
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyRequest;
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyResult;
import com.amazonaws.services.identitymanagement.model.CreateUserRequest;
import com.amazonaws.services.identitymanagement.model.CreateUserResult;
import com.amazonaws.services.identitymanagement.model.DeleteAccessKeyRequest;
import com.amazonaws.services.identitymanagement.model.GetUserRequest;
import com.amazonaws.services.identitymanagement.model.GetUserResult;
import com.amazonaws.services.identitymanagement.model.ListAccessKeysRequest;
import com.amazonaws.services.identitymanagement.model.ListAccessKeysResult;
import com.amazonaws.services.identitymanagement.model.NoSuchEntityException;
import com.amazonaws.services.identitymanagement.model.PutUserPolicyRequest;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.DeleteIdentityRequest;
import com.amazonaws.services.simpleemail.model.DeleteIdentityResult;
import com.amazonaws.services.simpleemail.model.VerifyEmailIdentityRequest;
import com.amazonaws.services.simpleemail.model.VerifyEmailIdentityResult;
import eds.component.GenericObjectService;
import eds.component.data.DBConnectionException;
import eds.component.data.DataValidationException;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import eds.component.mail.Password;
import eds.component.user.UserService;
import eds.entity.client.Client;
import eds.entity.client.ClientAWSAccount;
import eds.entity.client.ClientUserAssignment;
import eds.entity.client.ClientResource;
import eds.entity.client.ClientResourceAssignment;
import eds.entity.client.ClientType;
import eds.entity.client.ContactInfo;
import eds.entity.client.VerifiedSendingAddress;
import eds.entity.client.VerifiedSendingAddress_;
import eds.entity.data.EnterpriseData_;
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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.exception.GenericJDBCException;
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

    /**
     * Only 1 credential record is required and allowed.
     *
     * @param client
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ClientAWSAccount registerAWSForClient(Client client) {
        
        //1) Call CreateUser API http://docs.aws.amazon.com/IAM/latest/APIReference/API_CreateUser.html
        //Assume that this would only be called once for each client at this moment
        /*AmazonIdentityManagementClient awsClient = new AmazonIdentityManagementClient(awsCredentials);
        CreateUserRequest userReq = new CreateUserRequest(client.getCLIENT_NAME()).withPath("/segmail/");
        CreateUserResult userResult = awsClient.createUser(userReq);*/
        com.amazonaws.services.identitymanagement.model.User newOrExistingUser = this.retrieveOrRegisterAWSUser(client);
        
        //2) Call CreateAccessKey API http://docs.aws.amazon.com/IAM/latest/APIReference/API_CreateAccessKey.html
        /*CreateAccessKeyRequest accessKeyReq = new CreateAccessKeyRequest();
        accessKeyReq.setRequestCredentials(awsCredentials);
        accessKeyReq.setUserName(client.getCLIENT_NAME());
        CreateAccessKeyResult accessKeyResult = awsClient.createAccessKey(accessKeyReq);*/
        AccessKey newAccessKey = registerNewAWSAccessKey(client);
        
        //3) Call AttachUserPolicy API http://docs.aws.amazon.com/IAM/latest/APIReference/API_AttachUserPolicy.html
        attachSegmailSESPolicyToClient(client);
        
        ClientAWSAccount newAccount = new ClientAWSAccount();
        
        //newAccount.setOWNER(client);
        newAccount.setUSERID(newOrExistingUser.getUserId());
        newAccount.setUSERNAME(newOrExistingUser.getUserName());
        newAccount.setARN(newOrExistingUser.getArn());
        newAccount.setAWS_ACCESS_KEY_ID(newAccessKey.getAccessKeyId());
        newAccount.setAWS_SECRET_ACCESS_KEY(newAccessKey.getSecretAccessKey());
        
        objService.getEm().persist(newAccount);
        newAccount.setOWNER(client);
        
        return newAccount;
    }

    public com.amazonaws.services.identitymanagement.model.User retrieveOrRegisterAWSUser(Client client) {
        com.amazonaws.services.identitymanagement.model.User result = null;
        AmazonIdentityManagementClient awsClient = new AmazonIdentityManagementClient(awsCredentials);
        
        try {
            //1) Call CreateUser API http://docs.aws.amazon.com/IAM/latest/APIReference/API_CreateUser.html
            //Assume that this would only be called once for each client at this moment
            //Get the newOrExistingUser first
            GetUserRequest getReq = new GetUserRequest();
            //getReq.setRequestCredentials(awsCredentials);
            getReq.setUserName(client.getCLIENT_NAME());
            GetUserResult getResult = awsClient.getUser(getReq);
            result = getResult.getUser();
            
        } catch(NoSuchEntityException ex) {
            CreateUserRequest userReq = new CreateUserRequest(client.getCLIENT_NAME()).withPath("/segmail/");
            CreateUserResult userResult = awsClient.createUser(userReq);
            result = userResult.getUser();
        } finally {
            return result;
        }
    }
    
    public AccessKey registerNewAWSAccessKey(Client client) {
        AmazonIdentityManagementClient awsClient = new AmazonIdentityManagementClient(awsCredentials);
        
        //Delete all existing access keys first
        ListAccessKeysRequest listReq = new ListAccessKeysRequest();
        listReq.setUserName(client.getCLIENT_NAME());
        ListAccessKeysResult listResult = awsClient.listAccessKeys(listReq);
        for(AccessKeyMetadata key : listResult.getAccessKeyMetadata()){
            DeleteAccessKeyRequest delReq = new DeleteAccessKeyRequest();
            delReq.setUserName(key.getUserName());
            delReq.setAccessKeyId(key.getAccessKeyId());
            awsClient.deleteAccessKey(delReq);
        }
        //Then create a new access key
        CreateAccessKeyRequest accessKeyReq = new CreateAccessKeyRequest();
        accessKeyReq.setRequestCredentials(awsCredentials);
        accessKeyReq.setUserName(client.getCLIENT_NAME());
        CreateAccessKeyResult accessKeyResult = awsClient.createAccessKey(accessKeyReq);
        
        return accessKeyResult.getAccessKey();
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public VerifiedSendingAddress verifyNewSendingAddress(Client client, String sendingAddress) 
            throws DataValidationException {
        if(!EmailValidator.getInstance().isValid(sendingAddress))
            throw new DataValidationException("Email address is invalid.");
        
        List<VerifiedSendingAddress> existingAddresses = getVerifiedAddress(client.getOBJECTID(), sendingAddress);
        if(existingAddresses != null && !existingAddresses.isEmpty())
            return existingAddresses.get(0);
        
        //Get client's AWS credential
        ///Assume that there's only 1
        
        List<ClientAWSAccount> accounts = objService.getEnterpriseData(client.getOBJECTID(), ClientAWSAccount.class);
        if(accounts == null || accounts.isEmpty()) {
            throw new RuntimeException("AWS account has not been setup for this account yet.");
        }
        ClientAWSAccount account = accounts.get(0);
        
        //Persist record first before sending request, just like our MailService
        //This is to prevent internal errors causing external systems to keep
        //receiving our erroneous requests
        int sno = objService.getHighestSNO(VerifiedSendingAddress.class, client.getOBJECTID());
        
        VerifiedSendingAddress newVerifiedAddress = new VerifiedSendingAddress();
        newVerifiedAddress.setVERIFIED_ADDRESS(sendingAddress);
        newVerifiedAddress.setSNO(++sno);
        
        objService.getEm().persist(newVerifiedAddress);
        newVerifiedAddress.setOWNER(client);
        
        BasicAWSCredentials clientCredentials = new BasicAWSCredentials(account.getAWS_ACCESS_KEY_ID(),account.getAWS_SECRET_ACCESS_KEY());
        AmazonSimpleEmailServiceClient awsClient = new AmazonSimpleEmailServiceClient(clientCredentials);
        VerifyEmailIdentityRequest verifyReq = new VerifyEmailIdentityRequest().withEmailAddress(sendingAddress);
        VerifyEmailIdentityResult verifyResult = awsClient.verifyEmailIdentity(verifyReq);
        
        return newVerifiedAddress;
    }
    
    public List<VerifiedSendingAddress> getVerifiedAddress(long clientId, String sendingAddress) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<VerifiedSendingAddress> query = builder.createQuery(VerifiedSendingAddress.class);
        Root<VerifiedSendingAddress> fromAddress = query.from(VerifiedSendingAddress.class);
        
        query.select(fromAddress);
        query.where(builder.and(
                builder.equal(fromAddress.get(VerifiedSendingAddress_.OWNER), clientId),
                builder.equal(fromAddress.get(VerifiedSendingAddress_.VERIFIED_ADDRESS), sendingAddress)));
        
        List<VerifiedSendingAddress> results = objService.getEm().createQuery(query)
                .getResultList();
        
        return results;
        
    }
    
    public void attachSegmailSESPolicyToClient(Client client) {
        AmazonIdentityManagementClient awsClient = new AmazonIdentityManagementClient(awsCredentials);
        AddUserToGroupRequest groupReq = new AddUserToGroupRequest();
        groupReq.setGroupName("SegmailCustomers");
        groupReq.setUserName(client.getCLIENT_NAME());
        awsClient.addUserToGroup(groupReq);
        
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteSESIdentity(Client client, String verifiedEmail) {
        
        List<ClientAWSAccount> accounts = objService.getEnterpriseData(client.getOBJECTID(), ClientAWSAccount.class);
        if(accounts == null || accounts.isEmpty()) {
            throw new RuntimeException("AWS account has not been setup for this account yet.");
        }
        ClientAWSAccount account = accounts.get(0);
        
        BasicAWSCredentials clientCredentials = new BasicAWSCredentials(account.getAWS_ACCESS_KEY_ID(),account.getAWS_SECRET_ACCESS_KEY());
        AmazonSimpleEmailServiceClient awsClient = new AmazonSimpleEmailServiceClient(clientCredentials);
        DeleteIdentityRequest delRequest = new DeleteIdentityRequest();
        delRequest.setIdentity(verifiedEmail);
        DeleteIdentityResult result = awsClient.deleteIdentity(delRequest);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteVerifiedAddress(Client client, String verifiedEmail) {
        List<VerifiedSendingAddress> deleteAddresses = getVerifiedAddress(client.getOBJECTID(), verifiedEmail);
        for(VerifiedSendingAddress add : deleteAddresses) {
            objService.getEm().remove(add);
        }
        
        
    }
    
    public void deleteVerifiedAddressAndSESIdentity(Client client, String verifiedEmail) {
        
        deleteVerifiedAddress(client,verifiedEmail);
        objService.getEm().flush();
        objService.getEm().clear();
        //New transaction, because no 2 different classes of EnterpriseData can be retrieved in the 
        //same transaction
        deleteSESIdentity(client, verifiedEmail);
    }
}
