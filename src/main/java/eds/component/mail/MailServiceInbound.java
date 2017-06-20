/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.mail;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.model.NotificationType;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import eds.component.GenericObjectService;
import eds.component.client.ClientAWSService;
import eds.entity.client.VerifiedSendingAddress;
import eds.entity.mail.EMAIL_PROCESSING_STATUS;
import eds.entity.mail.Email;
import eds.entity.mail.Email_;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import segmail.component.subscription.SubscriptionService;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class MailServiceInbound {
    
    final int MAX_EMAIL_PROCESSED = 100;
    
    @Inject
    @Password
    BasicAWSCredentials awsCredentials;
    
    @EJB GenericObjectService objService;
    @EJB ClientAWSService clientAWSService;
    @EJB SubscriptionService subService;
    
    /**
     * Try to return a List of messages in some format
     * 
     * @param sender 
     * @param type 
     */
    public List<Email> retrieveEmailFromSQSMessage(VerifiedSendingAddress sender, NotificationType type) {
        String endpoint = clientAWSService.getSQSEndpoint();
        String queueName = clientAWSService.getSQSNameForAddress(sender, type);
        
        //Get the queueURL first
        AmazonSQSClient sqsClient = new AmazonSQSClient(awsCredentials); //or use client credentials?
        sqsClient.setEndpoint(endpoint);
        GetQueueUrlRequest urlReq = new GetQueueUrlRequest();
        urlReq.setQueueName(queueName);
        GetQueueUrlResult urlRes = sqsClient.getQueueUrl(urlReq);
        
        String queueURL = urlRes.getQueueUrl();
        
        //Get the messages using queueURL
        ReceiveMessageRequest msgReq = new ReceiveMessageRequest();
        msgReq.setQueueUrl(queueURL);
        msgReq.setMaxNumberOfMessages(10);
        
        ReceiveMessageResult msgRes = sqsClient.receiveMessage(msgReq);
        List<Message> messages = msgRes.getMessages();
        
        List<Email> emails = new ArrayList<>();
        List<String> messageIds = new ArrayList<>();
        List<String> receiptHandles = new ArrayList<>();
        
        for(Message message : messages) {
            JsonReader reader = Json.createReader(new StringReader(message.getBody()));
            JsonObject body = reader.readObject();
            String receiptHandle = message.getReceiptHandle();
            receiptHandles.add(receiptHandle);
            
            JsonObject msgBody = Json.createReader(new StringReader(body.getJsonString("Message").getString())).readObject();
            JsonString notifType = msgBody.getJsonString("notificationType");
            
            if(!"Bounce".equals(notifType.getString()))
                continue;
            JsonObject bounce = msgBody.getJsonObject("bounce");
            JsonString bounceType = bounce.getJsonString("bounceType");
            JsonObject mail = msgBody.getJsonObject("mail");
            JsonString messageId = mail.getJsonString("messageId");
            
            if("Permanent".equalsIgnoreCase(bounceType.getString())) {
                messageIds.add(messageId.getString());
            } else {//Haven't figure out what to do yet
                
            }
        }
        emails.addAll(getEmailsBySESMessageId(messageIds));
        
        //Delete messages that have been read
        for(String receiptHandle : receiptHandles) {
            sqsClient.deleteMessage(queueURL, receiptHandle);
        }
        
        return emails;
    }
    
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateBounceStatusForEmails(List<Email> emails, long clientId) {
        if(emails == null || emails.isEmpty())
            return;
        List<String> subscriberAddress = new ArrayList<>();
        for(Email email : emails) {
            email.setPROCESSING_STATUS(EMAIL_PROCESSING_STATUS.BOUNCED.label);
            Set<String> recipients = email.getRECIPIENTS();
            subscriberAddress.addAll(recipients);
            
        }
        //Update Subscription
        subService.updateSubscriptionBounceStatus(subscriberAddress, clientId);

        //Update SubscriberAccount
        subService.updateSubscriberBounceStatus(subscriberAddress, clientId);

        //Update subscriber count for SubscriptionLists
        subService.updateAllSubscriberCountForClient(clientId);
    }
    
    public void processAllBounce() {
        List<VerifiedSendingAddress> senders = new ArrayList<>();
        List<VerifiedSendingAddress> singleFetch = getAllVerifiedSenders(0, MAX_EMAIL_PROCESSED);
        senders.addAll(singleFetch);
        while (singleFetch.size() >= MAX_EMAIL_PROCESSED) {
            singleFetch = getAllVerifiedSenders(senders.size(), MAX_EMAIL_PROCESSED);
            senders.addAll(singleFetch);
        }
        
        for(VerifiedSendingAddress sender : senders) {
            
            List<Email> emails = retrieveEmailFromSQSMessage(sender, NotificationType.Bounce);
            updateBounceStatusForEmails(emails, sender.getOWNER().getOBJECTID());
        }
        
    }
    
    public List<VerifiedSendingAddress> getAllVerifiedSenders(int start, int max) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<VerifiedSendingAddress> countQuery = builder.createQuery(VerifiedSendingAddress.class);
        Root<VerifiedSendingAddress> fromSender = countQuery.from(VerifiedSendingAddress.class);
        
        countQuery.select(fromSender);
        
        List<VerifiedSendingAddress> senders = objService.getEm().createQuery(countQuery)
                .setFirstResult(start)
                .setMaxResults(max)
                .getResultList();
        
        return senders;
    }
    
    public List<Email> getEmailsBySESMessageId(List<String> messageIds) {
        if(messageIds == null || messageIds.isEmpty())
            return new ArrayList<>();
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<Email> query = builder.createQuery(Email.class);
        Root<Email> fromEmail = query.from(Email.class);
        
        query.select(fromEmail);
        query.where(fromEmail.get(Email_.AWS_SES_MESSAGE_ID).in(messageIds));
        
        List<Email> results = objService.getEm().createQuery(query)
                .getResultList();
        
        return results;
    }
    
}
/**
 * Sample message structure
 * {  
   "notificationType":"Bounce",
   "bounce":{  
      "bounceType":"Permanent",
      "bounceSubType":"General",
      "bouncedRecipients":[  
         {  
            "emailAddress":"kiathaw@segmail.io",
            "action":"failed",
            "status":"5.1.1",
            "diagnosticCode":"smtp"            ; "550-5.1.1 The email account that you tried to reach does not exist. Please try\\n550-5.1.1 double-checking the recipient's email address for typos or\\n550-5.1.1 unnecessary spaces. Learn more at\\n550 5.1.1  https://support.google.com/mail/?p=NoSuchUser 23si9644397qkd.84 - gsmtp"
         }
      ],
      "timestamp":"2016-11-12T08:33:56.696Z",
      "feedbackId":"0100015857abf374-03579aad-27db-4ebc-88de-46d30fc31e64-000000",
      "reportingMTA":"dsn"      ; "a8-43.smtp-out.amazonses.com"
   },
   "mail":{  
      "timestamp:2016-11-12T08:33:56.000Z",
      "source":"Vincent <vincent@segmail.io>",
      "sourceArn":"arn:aws:ses:us-east-1:071815939000:identity/vincent@segmail.io",
      "sendingAccountId":"071815939000",
      "messageId":"0100015857abf138-30a9f3e8-4fc2-4ae5-9687-030297b02bce-000000",
      "destination":"[kiathaw@segmail.io]"
   }
}
 */