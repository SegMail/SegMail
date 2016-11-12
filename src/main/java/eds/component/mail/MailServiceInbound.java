/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.mail;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.model.NotificationType;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import eds.component.GenericObjectService;
import eds.component.client.ClientAWSService;
import eds.entity.client.ClientAWSAccount;
import eds.entity.client.VerifiedSendingAddress;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class MailServiceInbound {
    
    @Inject
    @Password
    BasicAWSCredentials awsCredentials;
    
    @EJB GenericObjectService objService;
    @EJB ClientAWSService clientAWSService;
    
    /**
     * Try to return a List of messages in some format
     * 
     * @param sender 
     * @param type 
     */
    public void retrieveSNSMessage(VerifiedSendingAddress sender, NotificationType type) {
        String endpoint = clientAWSService.getSQSEndpoint();
        String queueName = clientAWSService.getSQSNameForAddress(sender, type);
        
        //Get the queueURL first
        AmazonSQSClient sqsClient = new AmazonSQSClient(awsCredentials);
        GetQueueUrlRequest urlReq = new GetQueueUrlRequest();
        urlReq.setQueueName(queueName);
        GetQueueUrlResult urlRes = sqsClient.getQueueUrl(urlReq);
        
        String queueURL = urlRes.getQueueUrl();
        
        //Get the messages using queueURL
        ReceiveMessageRequest msgReq = new ReceiveMessageRequest();
        msgReq.setQueueUrl(queueURL);
        ReceiveMessageResult msgRes = sqsClient.receiveMessage(msgReq);
        List<Message> messages = msgRes.getMessages();
        
        
    }
}
