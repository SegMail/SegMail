/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription.mailmerge;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.data.IncompleteDataException;
import eds.component.transaction.TransactionService;
import eds.entity.transaction.EnterpriseTransactionParam;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import seca2.component.landing.LandingServerGenerationStrategy;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;
import segmail.component.subscription.SubscriptionService;
import seca2.entity.landing.ServerInstance;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.email.mailmerge.MailMergeLabel;
import segmail.entity.subscription.email.mailmerge.MailMergeRequest;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class MailMergeService {
    
    
    @EJB private GenericObjectService objectService;
    @EJB private UpdateObjectService updateService;
    @EJB private TransactionService transService;
    
    @EJB private SubscriptionService subscriptionService;
    @EJB private LandingService landingService;
    
    /**
     * 
     * @param content
     * @param listId
     * @param subscribers
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String parseMultipleContent(String content, long listId, List<SubscriberAccount> subscribers){
        
        return "";
    }
    
    /**
     * As of now, we only recognize 3 types of mailmerge labels:
     * 1) Confirmation link
     * 2) Subscriber attributes
     * 3) Unsubscribe link
     * 
     * I can't think of any good way to create a logical generator that can be
     * configurable in the frontend. So we will just hard code everything first.
     * <br>
     * Generates the body content with the proper confirmation link, which has 
     * an expiry date.
     * 
     * @param text
     * @param email
     * @param listId
     * @return 
     * @throws eds.component.data.IncompleteDataException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String parseConfirmationLink(
            String text, //Don't pass in the AutoConfirmEmail class because that was a huge mistake and we might want to correct it in the future
            //String landingServerAddress, 
            String email,
            long listId) throws IncompleteDataException {
        //!!! do this only if there is a link to generate!
        if(!text.contains(MailMergeLabel.CONFIRM.label()))
            return text;
        
        //1. Create a transaction with expiry date 
        MailMergeRequest trans = new MailMergeRequest();
        trans.setPROGRAM("confirm");
        updateService.getEm().persist(trans);
        
        //2. Create the transaction parameters
        EnterpriseTransactionParam subscriberParam = new EnterpriseTransactionParam();
        subscriberParam.setOWNER(trans);
        subscriberParam.setPARAM_KEY(SubscriptionService.DEFAULT_EMAIL_FIELD_NAME);
        subscriberParam.setPARAM_VALUE(email);
        updateService.getEm().persist(subscriberParam);
        
        EnterpriseTransactionParam listParam = new EnterpriseTransactionParam();
        listParam.setOWNER(trans);
        listParam.setPARAM_KEY(SubscriptionService.DEFAULT_KEY_FOR_LIST);
        listParam.setPARAM_VALUE(Long.toString(listId));
        updateService.getEm().persist(listParam);
        //Might want to use guid instead.
        
        //3. Return the link with program name "confirm" and the generated transaction ID 
        ServerInstance landingServer = 
                landingService.getNextServerInstance(
                        LandingServerGenerationStrategy.ROUND_ROBIN,
                        ServerNodeType.WEB);
        if(landingServer == null)
            throw new IncompleteDataException("Please contact app administrator to set a landing server.");
        
        String confirmLink = landingServer.getURI().concat("/").concat(trans.getPROGRAM()).concat("/").concat(trans.getTRANSACTION_KEY());
        
        String newEmailBody = text.replace(MailMergeLabel.CONFIRM.label(), confirmLink);
        
        return newEmailBody;
    }
    
    /**
     * Not implemented yet.
     * 
     * @param text
     * @param listId
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String parseListAttributes(String text, long listId){
        return text;
    }
    
    /**
     * Not implemented yet.
     * 
     * @param text
     * @param listId
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String parseUnsubscribeLink(String text, long listId){
        return text;
    }
}
