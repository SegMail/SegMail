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
import com.sun.istack.logging.Logger;
import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.client.ClientAWSService;
import eds.component.transaction.TransactionLockingException;
import eds.component.transaction.TransactionService;
import eds.entity.client.VerifiedSendingAddress;
import eds.entity.mail.EMAIL_PROCESSING_STATUS;
import eds.entity.mail.Email;
import eds.entity.mail.SentEmail;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.joda.time.DateTime;
import segmail.component.subscription.SubscriptionService;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class MailServiceInbound {
    
    final int MAX_SENDERS_PROCESSED = 100;
    final int MAX_BOUNCE_PROCESSED = 100;
    final int MAX_QUEUE_MSG_READ = 50;
    
    @Inject
    @Password
    BasicAWSCredentials awsCredentials;
    
    @EJB GenericObjectService objService;
    @EJB UpdateObjectService updService;
    @EJB ClientAWSService clientAWSService;
    @EJB SubscriptionService subService;
    @EJB TransactionService txService;
    
    @EJB MailServiceInboundHelper helper;
    
    /**
     * Try to return a List of messages in some format
     * 
     * @param sender 
     * @param type 
     * @return  
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Message> retrieveFromSQSMessage(AmazonSQSClient sqsClient, String queueURL, NotificationType type) {
        
        //Get the messages using queueURL
        ReceiveMessageRequest msgReq = new ReceiveMessageRequest();
        msgReq.setQueueUrl(queueURL);
        msgReq.setMaxNumberOfMessages(10);
        Logger.getLogger(this.getClass()).log(Level.INFO, "Started reading SQS msg at "+DateTime.now());
        ReceiveMessageResult msgRes = sqsClient.receiveMessage(msgReq);
        List<Message> messages = msgRes.getMessages();
        Logger.getLogger(this.getClass()).log(Level.INFO, messages.size() + " msgs read at "+DateTime.now());
        
        return messages;
    }
    
    public List<SentEmail> retrieveEmailsFromDB(List<Message> messages) {
        List<SentEmail> emails = new ArrayList<>();
        List<String> messageIds = new ArrayList<>();
        List<String> recipientEmails = new ArrayList<>();
        
        for(Message message : messages) {
            JsonReader reader = Json.createReader(new StringReader(message.getBody()));
            JsonObject body = reader.readObject();
            
            JsonObject msgBody = Json.createReader(new StringReader(body.getJsonString("Message").getString())).readObject();
            JsonString notifType = msgBody.getJsonString("notificationType");
            
            if(!"Bounce".equalsIgnoreCase(notifType.getString()))
                continue;
            JsonObject bounce = msgBody.getJsonObject("bounce");
            JsonString bounceType = bounce.getJsonString("bounceType");
            JsonObject mail = msgBody.getJsonObject("mail");
            JsonString messageId = mail.getJsonString("messageId");
            // We'll think of how to use this 
            JsonArray recipients = mail.getJsonArray("destination");
            
            if("Permanent".equalsIgnoreCase(bounceType.getString())) {
                messageIds.add(messageId.getString());
            } else {//Haven't figure out what to do yet
                // To be safe, we update all bounce types
                messageIds.add(messageId.getString());
            }
        }
        emails.addAll(getEmailsBySESMessageId(messageIds));
        
        return emails;
    }
    
    public void deleteSQSMessages(AmazonSQSClient sqsClient, String queueURL, List<Message> messages) {
        for(Message message : messages) {
            String receiptHandle = message.getReceiptHandle();
            sqsClient.deleteMessage(queueURL, receiptHandle);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void updateBounceStatusForEmails(List<SentEmail> emails, long clientId) {
        if(emails == null || emails.isEmpty())
            return;
        List<String> subscriberAddress = new ArrayList<>();
        for(Email email : emails) {
            try {
                //helper.updateBounceStatus(email);
                //email.PROCESSING_STATUS(EMAIL_PROCESSING_STATUS.BOUNCED);
                //email = (Email) updService.merge(email);
                email = txService.transitTx(email, EMAIL_PROCESSING_STATUS.BOUNCED, DateTime.now());
                Set<String> recipients = email.getRECIPIENTS();
                subscriberAddress.addAll(recipients);
            } catch (TransactionLockingException ex) {
                // Forget about it...
                Logger.getLogger(this.getClass()).log(Level.SEVERE, "Bounce issue: " + ex.getMessage());
            }
            
        }
        // These shouldn't be here. They should be in SubscriptionService, but 
        // for convenience's sake they are put here.
        // Update Subscription and SubscriberAccount
        Logger.getLogger(this.getClass()).log(Level.INFO, "Starting the update for "
                +" list " + subscriberAddress.stream().collect(Collectors.joining(","))
                +" at "+DateTime.now());
        subService.updateSubscriptionSubscriberBounce(subscriberAddress, clientId);
        Logger.getLogger(this.getClass()).log(Level.INFO, "Completed the update "
                +" at "+DateTime.now());
        // Update subscriber count for SubscriptionLists
        subService.updateAllSubscriberCountForClient(clientId);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void processAllBounce() {
        Logger.getLogger(this.getClass()).log(Level.INFO, "Bounced processing started at "+DateTime.now());
        List<VerifiedSendingAddress> senders = new ArrayList<>();
        int sendersIndex = 0;
        int totalEmailCount = 0;
        
        do { // Looping through Senders
            senders = getAllVerifiedSenders(MAX_SENDERS_PROCESSED*sendersIndex++, MAX_SENDERS_PROCESSED);
            Logger.getLogger(this.getClass()).log(Level.INFO, senders.size() + " Senders retrieved at "+DateTime.now());
            for(VerifiedSendingAddress sender : senders) {
                Logger.getLogger(this.getClass()).log(Level.INFO, "Processing sender "
                        + sender.getVERIFIED_ADDRESS() +" ARN:"
                        + sender.getAWS_SQS_BOUNCE_QUEUE_ARN() + " at "+DateTime.now());
                List<SentEmail> emails = new ArrayList<>();
                List<SentEmail> incEmails = new ArrayList<>();
                List<Message> msg = new ArrayList<>();
                List<Message> incMsg = new ArrayList<>();
                
                String endpoint = clientAWSService.getSQSEndpoint();
                String queueName = clientAWSService.getSQSNameForAddress(sender, NotificationType.Bounce);

                //Get the queueURL first
                AmazonSQSClient sqsClient = new AmazonSQSClient(awsCredentials); //or use client credentials?
                sqsClient.setEndpoint(endpoint);
                GetQueueUrlRequest urlReq = new GetQueueUrlRequest();
                urlReq.setQueueName(queueName);
                GetQueueUrlResult urlRes = sqsClient.getQueueUrl(urlReq);

                String queueURL = urlRes.getQueueUrl();
                do { // Looping through Emails from each Sender
                    incMsg = retrieveFromSQSMessage(sqsClient,queueURL, NotificationType.Bounce);
                    incEmails = retrieveEmailsFromDB(incMsg);
                    
                    Logger.getLogger(this.getClass()).log(Level.INFO, "Retrieved msg ids "+
                            incEmails.stream().map(s -> 
                                    "{msgId:"+s.getAWS_SES_MESSAGE_ID() 
                                    + ",email:" + s.getRECIPIENTS().stream().collect(Collectors.joining(","))
                                    + "}\n"
                            ).collect(Collectors.joining("\n"))
                            + " at "+DateTime.now());
                    msg.addAll(incMsg);
                    emails.addAll(incEmails);
                } while(incEmails.size() > 0 && emails.size() < MAX_QUEUE_MSG_READ);
                
                updateBounceStatusForEmails(emails, sender.getOWNER().getOBJECTID());
                deleteSQSMessages(sqsClient, queueURL, msg);
                totalEmailCount += emails.size();
            }
        } while (senders.size() > 0 && totalEmailCount < MAX_BOUNCE_PROCESSED);
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
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
    
    public List<SentEmail> getEmailsBySESMessageId(List<String> messageIds) {
        if(messageIds == null || messageIds.isEmpty())
            return new ArrayList<>();
        
        String sql = "SELECT * FROM EMAIL_SENT WHERE AWS_SES_MESSAGE_ID IN ("
                + messageIds.stream().map(m -> "'" + m + "'").collect(Collectors.joining(","))
                + ")";
        
        Query q = objService.getEm().createNativeQuery(sql,SentEmail.class);
        List<SentEmail> results = q.getResultList();
        
        /*CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<Email> query = builder.createQuery(Email.class);
        Root<Email> fromEmail = query.from(Email.class);
        
        query.select(fromEmail);
        query.where(fromEmail.get(Email_.AWS_SES_MESSAGE_ID).in(messageIds));
        
        List<Email> results = objService.getEm().createQuery(query)
                .getResultList();
        */
        return results;
    }
    
    public List<Email> getEmailsFromOriginalEmailTable(List<String> messageIds) {
        if(messageIds == null || messageIds.isEmpty())
            return new ArrayList<>();
        
        // Must use native SQL, cannot use JPA as it will still map back to the 
        // concrete implementations of Email
        String sql = "SELECT * FROM EMAIL WHERE AWS_SES_MESSAGE_ID IN ("
                + messageIds.stream().map(m -> "'" + m + "'").collect(Collectors.joining(","))
                + ")";
        
        Query q = objService.getEm().createNativeQuery(sql,Email.class);
        List<Email> results = q.getResultList();
        
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