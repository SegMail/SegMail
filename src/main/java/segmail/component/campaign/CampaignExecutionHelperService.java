/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.campaign;

import eds.component.GenericObjectService;
import eds.component.data.DataValidationException;
import eds.component.data.IncompleteDataException;
import eds.component.mail.InvalidEmailException;
import eds.component.mail.MailServiceOutbound;
import eds.entity.client.Client;
import eds.entity.mail.Email;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.joda.time.DateTime;
import segmail.component.subscription.mailmerge.MailMergeService;
import segmail.entity.campaign.ACTIVITY_STATUS;
import segmail.entity.campaign.Assign_Campaign_Activity;
import segmail.entity.campaign.Assign_Campaign_List;
import segmail.entity.campaign.Campaign;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.campaign.Trigger_Email_Activity;
import segmail.entity.campaign.Trigger_Email_Activity_;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberAccount_;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.SubscriberOwnership;
import segmail.entity.subscription.SubscriberOwnership_;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.Subscription_;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class CampaignExecutionHelperService {
    
    @EJB
    GenericObjectService objService;
    @EJB
    MailMergeService mmService;
    @EJB
    MailServiceOutbound mailService;
    @EJB
    CampaignService campService;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int sendEmails(
            Campaign campaign,
            CampaignActivity campaignActivity,
            List<SubscriberAccount> subscribers, 
            List<Client> clientLists,
            List<SubscriptionListField> targetListFields) throws IncompleteDataException, DataValidationException, InvalidEmailException {
        
        int count = 0;
        if(subscribers.isEmpty())
            return 0;
        //Retrieve all unsubscribe codes
        Map<SubscriberAccount,String> unsubCodes = this.getUnsubscribeCodes(subscribers, clientLists.get(0).getOBJECTID()); //DB hit
        //Retrieve all subscriber's field values
        //List<SubscriberFieldValue> fieldValues = subService.getSubscriberValuesBySubscriberObjects(subscribers); //DB hit
        List<Long> subscrIds = objService.extractIds(subscribers);
        List<SubscriberFieldValue> fieldValues = objService.getEnterpriseDataByIds(subscrIds, SubscriberFieldValue.class);

        List<Long> subscriberIds = new ArrayList<>();
        for(SubscriberAccount subscriber : subscribers) {
            subscriberIds.add(subscriber.getOBJECTID());
        }
        Map<Long,Map<String,String>> fieldValuesMap = mmService.createMMValueMap(subscriberIds,targetListFields, fieldValues);

        for (SubscriberAccount subscriber : subscribers) {

            Email email = new Email();

            //Set the header info of the email
            email.setSUBJECT(campaignActivity.getACTIVITY_NAME());
            email.addRecipient(subscriber.getEMAIL());
            email.setSENDER_ADDRESS(campaign.getOVERRIDE_SEND_AS_EMAIL());
            email.setSENDER_NAME(campaign.getOVERRIDE_SEND_AS_NAME());

            //Set the body of the email
            String content = campaignActivity.getACTIVITY_CONTENT_PROCESSED();
            content = mmService.parseUnsubscribeLink(content, unsubCodes.get(subscriber)); //we'll use the WS method to edit unsub links [update] not now, let's stick to hardcoding as there isn't enough time
            content = mmService.parseMailmergeTagsSubscriber(content, fieldValuesMap.get(subscriber.getOBJECTID()));

            email.setBODY(content);

            mailService.queueEmail(email, DateTime.now());

            Trigger_Email_Activity trigger = new Trigger_Email_Activity();
            trigger.setTRIGGERED_TRANSACTION(email);
            trigger.setTRIGGERING_OBJECT(campaignActivity);

            objService.getEm().persist(trigger);

            count++;
        }
        return count;
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SubscriberAccount> getUnsentSubscriberEmailsForCampaign(long campaignId, long campaignActivityId, int startIndex, int size) {
        
        //New algorithm
        //Select both SubscriberAccount and Trigger_Email_Activity batch by batch, 
        //compare and accumulate the difference until it reaches the required size
        List<SubscriberAccount> result = new ArrayList<>();
        List<SubscriberAccount> subscribers = new ArrayList<>();
        List<String> sent = new ArrayList<>();
        int i = 0; //For subscribers
        int j = 0; //For sent
        final int BATCH_SIZE = 1000;
        
        sent = campService.getSentEmails(campaignActivityId, j++*BATCH_SIZE, BATCH_SIZE); //initial read
        do {
            subscribers = campService.getTargetedSubscribers(campaignId, i++*BATCH_SIZE, BATCH_SIZE);
            for(SubscriberAccount subscriber : subscribers) {
                String email = subscriber.getEMAIL();
                //If the email has been sent to already
                if(sent.contains(email)) {
                    sent.remove(email);
                    //Only after a successful remove operation then we reload sent
                    //If not it is going to reload for each and every one of the subscribers
                    if(sent.isEmpty())
                        sent = campService.getSentEmails(campaignActivityId, j++*BATCH_SIZE, BATCH_SIZE);    
                } 
                //If the email is not sent to yet, add it in result
                else {
                    result.add(subscriber);
                    //Exit this do while
                    if(result.size() >= size)
                        return result;
                }
                
            }
        } while(result.size() < size && !subscribers.isEmpty());
        
        return result;
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Map<SubscriberAccount,String> getUnsubscribeCodes(List<SubscriberAccount> subscribers, long clientId) {
        //Simplest and least expensive solution.
        if(subscribers == null || subscribers.isEmpty())
            return new HashMap<>();
        
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<Object[]> query = builder.createQuery( Object[].class );
        Root<Subscription> fromSubscrp = query.from(Subscription.class);
        Root<SubscriberAccount> fromSubscrb = query.from(SubscriberAccount.class);
        Root<SubscriberOwnership> fromOwner = query.from(SubscriberOwnership.class);
        
        query.multiselect(fromSubscrb,fromSubscrp.get(Subscription_.UNSUBSCRIBE_KEY));
        
        query.where(builder.and(fromSubscrb.in(subscribers),
                        builder.equal(fromSubscrb.get(SubscriberAccount_.OBJECTID), fromSubscrp.get(Subscription_.SOURCE)),
                        builder.equal(fromSubscrb.get(SubscriberAccount_.OBJECTID), fromOwner.get(SubscriberOwnership_.SOURCE)),
                        builder.equal(fromOwner.get(SubscriberOwnership_.TARGET), clientId)
                )
        );
        List<Object[]> results = objService.getEm().createQuery(query)
                .getResultList();
        
        Map<SubscriberAccount,String> resultMap = new HashMap<>();
        for(Object[] result : results){
            resultMap.put((SubscriberAccount)result[0], result[1].toString());
        }
        
        return resultMap;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateActivityStatus(CampaignActivity activity) {
        long targeted = campService.countTargetedSubscribersForCampaign(activity.getOBJECTID());
        long sent = campService.countEmailsSentForActivity(activity.getOBJECTID());
        
        if(targeted == sent)
            activity.setSTATUS(ACTIVITY_STATUS.COMPLETED.name);
        
        objService.getEm().merge(activity);
        
    }
    
    
}
