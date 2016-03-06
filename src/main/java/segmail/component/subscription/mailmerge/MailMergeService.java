/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription.mailmerge;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.entity.transaction.EnterpriseTransaction;
import eds.entity.transaction.EnterpriseTransactionParam;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.landing.ServerInstance;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.email.mailmerge.MailMergeLabel;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class MailMergeService {
    
    public static final String DEFAULT_KEY_FOR_LIST = "LIST";
    
    @EJB private GenericObjectService objectService;
    @EJB private UpdateObjectService updateService;
    
    @EJB private SubscriptionService subscriptionService;
    
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
     * Generates the confirmation link, which has an expiry date.
     * 
     * @param emailBody
     * @param landingServerAddress
     * @param landingServer
     * @param email
     * @param listId
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String generateConfirmationLink(
            String emailBody, //Don't pass in the AutoConfirmEmail class because that was a huge mistake and we might want to correct it in the future
            String landingServerAddress, 
            String email,
            long listId) {
        //1. Create a transaction with expiry date
        EnterpriseTransaction trans = new EnterpriseTransaction();
        updateService.getEm().persist(trans);
        
        //2. Create the transaction parameters
        EnterpriseTransactionParam subscriberParam = new EnterpriseTransactionParam();
        subscriberParam.setOWNER(trans);
        subscriberParam.setPARAM_KEY(SubscriptionService.DEFAULT_EMAIL_FIELD_NAME);
        subscriberParam.setPARAM_VALUE(email);
        updateService.getEm().persist(subscriberParam);
        
        EnterpriseTransactionParam listParam = new EnterpriseTransactionParam();
        listParam.setOWNER(trans);
        listParam.setPARAM_KEY(DEFAULT_KEY_FOR_LIST);
        listParam.setPARAM_VALUE(Long.toString(listId));
        updateService.getEm().persist(listParam);
        
        //3. Return the link with program name "confirm" and the generated transaction ID
        String confirmLink = landingServerAddress.concat("/").concat(Long.toString(trans.getTRANSACTION_ID()));
        
        String newEmailBody = emailBody.replace(MailMergeLabel.CONFIRM.label(), confirmLink);
        
        return newEmailBody;
    }
}
