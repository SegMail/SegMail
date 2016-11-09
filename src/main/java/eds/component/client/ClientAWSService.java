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
import com.amazonaws.services.identitymanagement.model.User;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.DeleteIdentityRequest;
import com.amazonaws.services.simpleemail.model.DeleteIdentityResult;
import com.amazonaws.services.simpleemail.model.NotificationType;
import com.amazonaws.services.simpleemail.model.SetIdentityNotificationTopicRequest;
import com.amazonaws.services.simpleemail.model.SetIdentityNotificationTopicResult;
import com.amazonaws.services.simpleemail.model.VerifyEmailIdentityRequest;
import com.amazonaws.services.simpleemail.model.VerifyEmailIdentityResult;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import eds.component.GenericObjectService;
import eds.component.data.DataValidationException;
import eds.component.mail.Password;
import eds.component.user.UserService;
import eds.entity.client.Client;
import eds.entity.client.ClientAWSAccount;
import eds.entity.client.VerifiedSendingAddress;
import eds.entity.client.VerifiedSendingAddress_;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.commons.validator.routines.EmailValidator;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class ClientAWSService {
    
    @EJB
    private GenericObjectService objService;
    @EJB
    private UserService userService;

    @Inject
    private ClientFacade clientFacade;

    @Inject
    @Password
    BasicAWSCredentials awsCredentials;
    
    public static final String DEFAULT_SMTP_ENDPOINT = "email-smtp.us-east-1.amazonaws.com";
    public static final String DEFAULT_HTTPS_ENDPOINT_PROD = "email.us-east-1.amazonaws.com"; 
    public static final String DEFAULT_AWS_ENDPOINT = "us-east-1.amazonaws.com"; //Current production

    public static final String AWS_IAM_ENDPOINT = "ClientAWSService.AWS_IAM_ENDPOINT";
    public static final String AWS_SES_ENDPOINT = "ClientAWSService.AWS_SES_ENDPOINT";
    public static final String AWS_SQS_ENDPOINT = "ClientAWSService.AWS_SQS_ENDPOINT";
    public static final String AWS_SNS_ENDPOINT = "ClientAWSService.AWS_SNS_ENDPOINT";
    
    /**
     * If there's no container-managed endpoint, fall back to the default sandbox.
     * @return 
     */
    public String getIAMEndpoint() {
        String endpoint = System.getProperty(AWS_IAM_ENDPOINT,"iam."+DEFAULT_AWS_ENDPOINT);
        
        return endpoint;
    }
    
    public String getSQSEndpoint() {
        String endpoint = System.getProperty(AWS_SQS_ENDPOINT,"sqs."+DEFAULT_AWS_ENDPOINT);
        
        return endpoint;
    }
    
    public String getSNSEndpoint() {
        String endpoint = System.getProperty(AWS_SNS_ENDPOINT,"sns."+DEFAULT_AWS_ENDPOINT);
        
        return endpoint;
    }
    
    public String getSESEndpoint() {
        String endpoint = System.getProperty(AWS_SES_ENDPOINT,"email."+DEFAULT_AWS_ENDPOINT);
        
        return endpoint;
    }

    public void attachSegmailSESPolicyToClient(Client client) {
        AmazonIdentityManagementClient awsClient = new AmazonIdentityManagementClient(awsCredentials);
        AddUserToGroupRequest groupReq = new AddUserToGroupRequest();
        groupReq.setGroupName("SegmailCustomers");
        groupReq.setUserName(client.getCLIENT_NAME());
        awsClient.addUserToGroup(groupReq);
    }

    /**
     *
     * @param sender
     * @param sqsClient
     * @return VerifiedSendingAddress with the updated queue ARN
     */
    public String registerSQSForSender(String name, String sender, AmazonSQSClient sqsClient) {
        CreateQueueRequest sqsReq1 = new CreateQueueRequest();
        sqsReq1.setQueueName(name);
        CreateQueueResult sqsResult1 = sqsClient.createQueue(sqsReq1);
        String queueURL = sqsResult1.getQueueUrl();
        
        GetQueueAttributesRequest sqsReq2 = new GetQueueAttributesRequest();
        List<String> queueAttr = new ArrayList<>();
        queueAttr.add("QueueArn");
        sqsReq2.setAttributeNames(queueAttr);
        sqsReq2.setQueueUrl(queueURL);
        GetQueueAttributesResult sqsResult2 = sqsClient.getQueueAttributes(sqsReq2);
        Map<String, String> sqsResult2Map = sqsResult2.getAttributes();
        String queueARN = sqsResult2Map.get("QueueArn");
        
        return queueARN;
    }

    public AccessKey registerNewAWSAccessKey(Client client) {
        AmazonIdentityManagementClient awsClient = new AmazonIdentityManagementClient(awsCredentials);
        ListAccessKeysRequest listReq = new ListAccessKeysRequest();
        listReq.setUserName(client.getCLIENT_NAME());
        ListAccessKeysResult listResult = awsClient.listAccessKeys(listReq);
        for (AccessKeyMetadata key : listResult.getAccessKeyMetadata()) {
            DeleteAccessKeyRequest delReq = new DeleteAccessKeyRequest();
            delReq.setUserName(key.getUserName());
            delReq.setAccessKeyId(key.getAccessKeyId());
            awsClient.deleteAccessKey(delReq);
        }
        CreateAccessKeyRequest accessKeyReq = new CreateAccessKeyRequest();
        accessKeyReq.setRequestCredentials(awsCredentials);
        accessKeyReq.setUserName(client.getCLIENT_NAME());
        CreateAccessKeyResult accessKeyResult = awsClient.createAccessKey(accessKeyReq);
        return accessKeyResult.getAccessKey();
    }

    /**
     * Registers a bounce queue and message processing hook at AWS and updates
     * sender with the various ARN.
     *
     * @param sender
     */
    public void registerNotifProcessingForSender(VerifiedSendingAddress sender,NotificationType type) {
        List<ClientAWSAccount> accounts = objService.getEnterpriseData(sender.getOWNER().getOBJECTID(), ClientAWSAccount.class);
        if (accounts == null || accounts.isEmpty()) {
            throw new RuntimeException("AWS account has not been setup for this account yet.");
        }
        ClientAWSAccount account = accounts.get(0);
        BasicAWSCredentials clientCredentials = new BasicAWSCredentials(account.getAWS_ACCESS_KEY_ID(), account.getAWS_SECRET_ACCESS_KEY());
        sender = objService.getEm().merge(sender);
        
        AmazonSQSClient sqsClient = new AmazonSQSClient(clientCredentials);
        sqsClient.setEndpoint(getSQSEndpoint());
        AmazonSNSClient snsClient = new AmazonSNSClient(clientCredentials);
        snsClient.setEndpoint(getSNSEndpoint());
        //1. Create SQS message Queue
        String queueARN = sender.getQueueARN(type);
        if (queueARN == null || queueARN.isEmpty()) {
            queueARN = registerSQSForSender(type.name()+"-sqs-"+sender.getOWNER().getOBJECTID(),sender.getVERIFIED_ADDRESS(), sqsClient);
            sender.setQueueARN(queueARN,type);
        }
        
        //2. Create SNS Topic
        String topicARN = sender.getTopicARN(type);
        if (topicARN == null || topicARN.isEmpty()) {
            topicARN = registerSNSTopicForSender(type.name()+"-sns-"+sender.getOWNER().getOBJECTID(),sender.getVERIFIED_ADDRESS(), snsClient);
            sender.setTopicARN(topicARN,type);
        }
        
        //3. Subscribe the SQS Queue to the SNS Topic
        String subscriptionARN = sender.getSubsriptionARN(type);
        if (subscriptionARN == null || subscriptionARN.isEmpty()) {
            subscriptionARN = subsribeToSNSTopic(topicARN, queueARN, snsClient);
            sender.setSubsriptionARN(subscriptionARN,type);
        }
        
        //4. SES publish notifications to SNS Topic
        AmazonSimpleEmailServiceClient sesClient = new AmazonSimpleEmailServiceClient(clientCredentials);
        sesClient.setEndpoint(getSESEndpoint());
        SetIdentityNotificationTopicRequest req = new SetIdentityNotificationTopicRequest();
        req.setIdentity(sender.getVERIFIED_ADDRESS());
        req.setNotificationType(type);
        req.setSnsTopic(topicARN);
        
        SetIdentityNotificationTopicResult sesResult = sesClient.setIdentityNotificationTopic(req);
    }

    /**
     * Retrieves the given email address by clientId
     * 
     * @param clientId
     * @param sendingAddress
     * @return 
     */
    public List<VerifiedSendingAddress> getVerifiedAddress(long clientId, String sendingAddress) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<VerifiedSendingAddress> query = builder.createQuery(VerifiedSendingAddress.class);
        Root<VerifiedSendingAddress> fromAddress = query.from(VerifiedSendingAddress.class);
        
        query.select(fromAddress);
        query.where(
                builder.and(
                        builder.equal(fromAddress.get(VerifiedSendingAddress_.OWNER), clientId), 
                        builder.equal(fromAddress.get(VerifiedSendingAddress_.VERIFIED_ADDRESS), sendingAddress)));
        
        List<VerifiedSendingAddress> results = objService.getEm().createQuery(query).getResultList();
        
        return results;
    }
    
    /**
     * Retrieves the given email address globally.
     * 
     * @param sendingAddress
     * @return 
     */
    public List<VerifiedSendingAddress> getVerifiedAddress(String sendingAddress) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<VerifiedSendingAddress> query = builder.createQuery(VerifiedSendingAddress.class);
        Root<VerifiedSendingAddress> fromAddress = query.from(VerifiedSendingAddress.class);
        
        query.select(fromAddress);
        query.where(builder.and( 
                builder.equal(fromAddress.get(VerifiedSendingAddress_.VERIFIED_ADDRESS), sendingAddress)));
        
        List<VerifiedSendingAddress> results = objService.getEm().createQuery(query).getResultList();
        
        return results;
    }

    public User retrieveOrRegisterAWSUser(Client client) {
        User result = null;
        AmazonIdentityManagementClient awsClient = new AmazonIdentityManagementClient(awsCredentials);
        awsClient.setEndpoint(getIAMEndpoint());
        try {
            GetUserRequest getReq = new GetUserRequest();
            getReq.setUserName(client.getCLIENT_NAME());
            GetUserResult getResult = awsClient.getUser(getReq);
            result = getResult.getUser();
        } catch (NoSuchEntityException ex) {
            CreateUserRequest userReq = new CreateUserRequest(client.getCLIENT_NAME()).withPath("/segmail/");
            CreateUserResult userResult = awsClient.createUser(userReq);
            result = userResult.getUser();
        } finally {
            return result;
        }
    }

    public void deleteVerifiedAddressAndSESIdentity(Client client, String verifiedEmail) {
        deleteVerifiedAddress(client, verifiedEmail);
        objService.getEm().flush();
        objService.getEm().clear();
        deleteSESIdentity(client, verifiedEmail);
    }

    /**
     * Only 1 credential record is required and allowed.
     *
     * @param client
     */
    public ClientAWSAccount registerAWSForClient(Client client) {
        User newOrExistingUser = retrieveOrRegisterAWSUser(client);
        AccessKey newAccessKey = registerNewAWSAccessKey(client);
        attachSegmailSESPolicyToClient(client);
        
        ClientAWSAccount newAccount = new ClientAWSAccount();
        newAccount.setUSERID(newOrExistingUser.getUserId());
        newAccount.setUSERNAME(newOrExistingUser.getUserName());
        newAccount.setARN(newOrExistingUser.getArn());
        newAccount.setAWS_ACCESS_KEY_ID(newAccessKey.getAccessKeyId());
        newAccount.setAWS_SECRET_ACCESS_KEY(newAccessKey.getSecretAccessKey());
        objService.getEm().persist(newAccount);
        newAccount.setOWNER(client);
        return newAccount;
    }

    /**
     * 
     * @param name
     * @param sender
     * @param snsClient
     * @return 
     */
    public String registerSNSTopicForSender(String name, String sender, AmazonSNSClient snsClient) {
        CreateTopicRequest snsReq1 = new CreateTopicRequest();
        snsReq1.setName(name);
        
        CreateTopicResult snsResult1 = snsClient.createTopic(snsReq1);
        String topicARN = snsResult1.getTopicArn();
        
        return topicARN;
    }
    
    public String subsribeToSNSTopic(String topicARN, String queueARN, AmazonSNSClient snsClient) {
        SubscribeRequest snsReq2 = new SubscribeRequest();
        snsReq2.setTopicArn(topicARN);
        snsReq2.setProtocol("sqs");
        snsReq2.setEndpoint(queueARN);
        
        SubscribeResult snsResult2 = snsClient.subscribe(snsReq2);
        String subscriptionARN = snsResult2.getSubscriptionArn();
        
        return subscriptionARN;
    }

    /**
     * Heavy operation with lots of Internet round trips.
     * 
     * @param client
     * @param sendingAddress
     * @return
     * @throws DataValidationException 
     */
    public VerifiedSendingAddress verifyNewSendingAddress(Client client, String sendingAddress) 
            throws DataValidationException {
        
        if (!EmailValidator.getInstance().isValid(sendingAddress)) {
            throw new DataValidationException("Email address is invalid.");
        }
        
        //Must check globally, not just for the individual Client
        List<VerifiedSendingAddress> existingAddresses = getVerifiedAddress(sendingAddress);
        if (existingAddresses != null && !existingAddresses.isEmpty()) {
            for(VerifiedSendingAddress address : existingAddresses) {
                if(address.getOWNER().equals(client))
                    throw new DataValidationException("This email address was requested by you or already verified.");
            }
            throw new DataValidationException("This email address is already taken by another account.");
        }
        
        List<ClientAWSAccount> accounts = objService.getEnterpriseData(client.getOBJECTID(), ClientAWSAccount.class);
        if (accounts == null || accounts.isEmpty()) {
            throw new RuntimeException("AWS account has not been setup for this account yet.");
        }
        
        ClientAWSAccount account = accounts.get(0);
        int sno = objService.getHighestSNO(VerifiedSendingAddress.class, client.getOBJECTID());
        
        VerifiedSendingAddress newVerifiedAddress = new VerifiedSendingAddress();
        newVerifiedAddress.setVERIFIED_ADDRESS(sendingAddress);
        newVerifiedAddress.setSNO(++sno);
        objService.getEm().persist(newVerifiedAddress);
        newVerifiedAddress.setOWNER(client);
        
        BasicAWSCredentials clientCredentials = new BasicAWSCredentials(account.getAWS_ACCESS_KEY_ID(), account.getAWS_SECRET_ACCESS_KEY());
        AmazonSimpleEmailServiceClient awsClient = new AmazonSimpleEmailServiceClient(clientCredentials);
        awsClient.setEndpoint(getSESEndpoint());
        VerifyEmailIdentityRequest verifyReq = new VerifyEmailIdentityRequest().withEmailAddress(sendingAddress);
        VerifyEmailIdentityResult verifyResult = awsClient.verifyEmailIdentity(verifyReq);
        
        registerNotifProcessingForSender(newVerifiedAddress,NotificationType.Bounce);
        registerNotifProcessingForSender(newVerifiedAddress,NotificationType.Complaint);
        
        return newVerifiedAddress;
    }

    public void deleteSESIdentity(Client client, String verifiedEmail) {
        List<ClientAWSAccount> accounts = objService.getEnterpriseData(client.getOBJECTID(), ClientAWSAccount.class);
        if (accounts == null || accounts.isEmpty()) {
            throw new RuntimeException("AWS account has not been setup for this account yet.");
        }
        ClientAWSAccount account = accounts.get(0);
        BasicAWSCredentials clientCredentials = new BasicAWSCredentials(account.getAWS_ACCESS_KEY_ID(), account.getAWS_SECRET_ACCESS_KEY());
        AmazonSimpleEmailServiceClient awsClient = new AmazonSimpleEmailServiceClient(clientCredentials);
        awsClient.setEndpoint(getSESEndpoint());
        
        DeleteIdentityRequest delRequest = new DeleteIdentityRequest();
        delRequest.setIdentity(verifiedEmail);
        
        DeleteIdentityResult result = awsClient.deleteIdentity(delRequest);
    }

    public void deleteVerifiedAddress(Client client, String verifiedEmail) {
        List<VerifiedSendingAddress> deleteAddresses = getVerifiedAddress(client.getOBJECTID(), verifiedEmail);
        for (VerifiedSendingAddress add : deleteAddresses) {
            objService.getEm().remove(add);
        }
    }
    
}
