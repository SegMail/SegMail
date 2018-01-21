/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.campaign;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.mail.MailServiceOutbound;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import segmail.component.subscription.mailmerge.MailMergeService;
import segmail.entity.campaign.ACTIVITY_STATUS;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.campaign.filter.CampaignActivityFilter;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberAccount_;
import segmail.entity.subscription.SubscriberOwnership;
import segmail.entity.subscription.SubscriberOwnership_;
import segmail.entity.subscription.Subscription;
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
    @EJB
    UpdateObjectService updService;
    
    /**
     * 
     * @param campaignId
     * @param campaignActivityId
     * @param clientId
     * @param listIds The targeted lists. If empty or null, all subscribers belonging to the user will be used.
     * @param filters The list of CampaignActivityFilters. If empty, no filters will be applied.
     * @param searchStartIndex the nth batch of targeted subscribers that we want to start searching from
     * this is a wrapper object so that the calling client can retrieve the updated value
     * @param size
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SubscriberAccount> getUnsentSubscriberEmailsForCampaign(
            long campaignId, 
            long campaignActivityId, 
            long clientId, 
            List<Long> listIds,
            List<CampaignActivityFilter> filters, 
            Counter searchStartIndex, 
            int size) {
        
        //New algorithm
        //Select both SubscriberAccount and Trigger_Email_Activity batch by batch, 
        //compare and accumulate the difference until it reaches the required size
        List<SubscriberAccount> result = new ArrayList<>();
        List<SubscriberAccount> subscribers = new ArrayList<>();
        
        final int BATCH_SIZE = 1000;
        
        do {
            List<String> targetedEmails = new ArrayList<>();
            // Change here to select by campaign activity 
            subscribers = campService.getTargetedSubscribers(
                    campaignActivityId, 
                    clientId, 
                    listIds,
                    filters,
                    searchStartIndex.getValue()*BATCH_SIZE, BATCH_SIZE);
            searchStartIndex.increment();
            for(SubscriberAccount subscriber : subscribers) {
                targetedEmails.add(subscriber.getEMAIL());
            }
            List<String> sent = campService.getSentEmails(campaignActivityId, targetedEmails);
            //Check if these emails are already sent
            for(SubscriberAccount subscriber : subscribers) {
                String email = subscriber.getEMAIL();
                if(!sent.contains(email)) {
                    result.add(subscriber);
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
    public void updateActivityStatus(CampaignActivity activity, ACTIVITY_STATUS status, int lastIndex) {
        activity = objService.getEm().merge(activity);
        activity.setSTATUS(status.name);
        activity.setLAST_INDEX(lastIndex);
    }
    
}
