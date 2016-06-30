/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.campaign;

import eds.component.GenericObjectService;
import eds.component.client.ClientFacade;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipNotFoundException;
import eds.entity.data.EnterpriseRelationship_;
import eds.entity.mail.Email;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import seca2.bootstrap.module.Client.ClientContainer;
import segmail.component.subscription.SubscriptionService;
import segmail.component.subscription.mailmerge.MailMergeService;
import segmail.entity.campaign.Assign_Campaign_Activity;
import segmail.entity.campaign.Assign_Campaign_Activity_;
import segmail.entity.campaign.Assign_Campaign_List;
import segmail.entity.campaign.Assign_Campaign_List_;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.campaign.CampaignActivityExecutionSchedule;
import segmail.entity.campaign.CampaignExecutionError;
import segmail.entity.campaign.Trigger_Email_Activity;
import segmail.entity.campaign.Trigger_Email_Activity_;
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
public class CampaignExecutionService {

    public final int BATCH_SIZE = 1000;
    
    @Inject ClientFacade clientFacade;

    @EJB
    GenericObjectService objService;
    @EJB
    CampaignService campService;
    @EJB
    MailMergeService mmService;
    @EJB
    SubscriptionService subService;

    /**
     *
     *
     * @param campaignActivityId
     * @return
     */
    public CampaignActivityExecutionSchedule createActivityExecutionSchedule(long campaignActivityId, List<Long> targetedLists) {

        CampaignActivityExecutionSchedule newExecution = new CampaignActivityExecutionSchedule();
        newExecution.setCAMPAIGN_ACTIVITY_ID(campaignActivityId);

        String targetedListsString = "";
        for (Long list : targetedLists) {
            if (!targetedListsString.isEmpty()) {
                targetedListsString += ",";
            }
            targetedListsString += list.toString();
        }

        newExecution.setTARGETED_LIST_ID(targetedListsString);

        objService.getEm().persist(newExecution);

        return newExecution;
    }

    /**
     * Executes the campaign activity from the [start]th subscriber to
     * [start]+size th subscriber.
     *
     * @param campaignActivityId
     * @param startIndex
     * @param size
     * @throws eds.component.data.EntityNotFoundException
     * @throws eds.component.data.RelationshipNotFoundException
     */
    public void executeCampaignActivity(long campaignActivityId, int startIndex, int size)
            throws EntityNotFoundException, RelationshipNotFoundException {
        int currIndex = startIndex;
        final int targetIndex = currIndex + size; //Process until currIndex >= targetIndex

        CampaignActivity campaignActivity = campService.getCampaignActivity(campaignActivityId);
        if (campaignActivity == null) {
            throw new EntityNotFoundException(CampaignActivity.class, campaignActivityId);
        }

        //List<Campaign> campaigns = objService.getAllSourceObjectsFromTarget(campaignActivityId, Assign_Campaign_Activity.class, Campaign.class);
        //if(campaigns == null || campaigns.isEmpty())
        //    throw new RelationshipNotFoundException("Campaign not found for CampaignActivity "+campaignActivityId);
        //Campaign campaign = campaigns.get(0);
        while (currIndex < targetIndex) {
            //Retrieve all subscriber emails
            List<String> subscribers = this.getUnsentSubscriberEmailsForCampaign(campaignActivityId, currIndex, BATCH_SIZE);
            
            //Retrieve all unsubscribe codes
            Map<String,String> unsubCodes = this.getUnsubscribeCodes(subscribers, clientFacade.getClient().getOBJECTID());
            
            for (String subscriber : subscribers) {

                try {
                    //Before sending, check again
                    //WARNING: This result might be cached
                    //This doesn't solve the concurrent issue. We need something at EDS layer to 
                    //facilitate object locking.
                    List<Trigger_Email_Activity> checkTriggers = this.getEmailTriggers(campaignActivityId, subscriber);
                    if (checkTriggers != null && !checkTriggers.isEmpty()) {
                        continue;
                    }
                    
                    Email email = new Email();
                    email.setSUBJECT(campaignActivity.getACTIVITY_NAME());
                    email.addRecipient(subscriber);
                    
                    String content = campaignActivity.getACTIVITY_CONTENT();
                    content = mmService.parseUnsubscribeLink(content, unsubCodes.get(subscriber));
                    
                    email.setBODY(content);
                    
                    objService.getEm().persist(email);
                    
                    Trigger_Email_Activity trigger = new Trigger_Email_Activity();
                    trigger.setTRIGGERED_TRANSACTION(email);
                    trigger.setTRIGGERING_OBJECT(campaignActivity);
                    
                    objService.getEm().persist(trigger);
                    
                } catch (IncompleteDataException ex) {
                    CampaignExecutionError error = new CampaignExecutionError();
                    error.setCAMPAIGN_ACTIVITY_ID(campaignActivityId);
                    error.setERROR_MESSAGE(ex.getMessage());
                    error.setRECIPIENT(subscriber);
                    
                    objService.getEm().persist(error);
                } 
            }

            currIndex = currIndex + BATCH_SIZE;
        }

    }

    public List<String> getSubscriberEmailsForCampaign(long campaignId, int startIndex, int size) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<String> query = builder.createQuery(String.class);
        Root<SubscriberAccount> fromSubAcc = query.from(SubscriberAccount.class);
        Root<Subscription> fromSubp = query.from(Subscription.class);
        Root<Assign_Campaign_List> fromAssignCampList = query.from(Assign_Campaign_List.class);

        query.select(fromSubAcc.get(SubscriberAccount_.EMAIL));
        query.where(
                builder.and(
                        builder.equal(fromAssignCampList.get(Assign_Campaign_List_.SOURCE), campaignId),
                        builder.equal(fromAssignCampList.get(Assign_Campaign_List_.TARGET), fromSubp.get(Subscription_.TARGET)),
                        builder.equal(fromSubp.get(Subscription_.TARGET), fromSubAcc.get(SubscriberAccount_.OBJECTID))
                )
        );

        List<String> results = objService.getEm().createQuery(query)
                .setFirstResult(startIndex)
                .setMaxResults(size)
                .getResultList();

        return results;

    }

    /**
     * A long query but worth the performance. If a subscriber appears in more
     * than 1 list, only 1 email will be sent. 1 Email per CampaignActivity per
     * SubscriberAccount.
     *
     * @param campaignActivityId
     * @param startIndex
     * @param size
     * @return
     */
    public List<String> getUnsentSubscriberEmailsForCampaign(long campaignActivityId, int startIndex, int size) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<String> query = builder.createQuery(String.class);
        Root<SubscriberAccount> fromSubAcc = query.from(SubscriberAccount.class);
        Root<Subscription> fromSubp = query.from(Subscription.class);
        Root<Assign_Campaign_List> fromAssignCampList = query.from(Assign_Campaign_List.class);
        Root<Assign_Campaign_Activity> fromAssignCampAct = query.from(Assign_Campaign_Activity.class);

        //Subquery
        Subquery<String> emailQuery = query.subquery(String.class);
        Root<Trigger_Email_Activity> fromTrigger = emailQuery.from(Trigger_Email_Activity.class);
        emailQuery.select(fromTrigger.get(Trigger_Email_Activity_.SUBCRIBER_EMAIL));

        emailQuery.where(
                builder.and(
                        builder.equal(fromTrigger.get(Trigger_Email_Activity_.TRIGGERING_OBJECT), campaignActivityId)
                )
        );

        query.select(fromSubAcc.get(SubscriberAccount_.EMAIL));
        query.distinct(true);
        query.where(
                builder.and(
                        builder.equal(fromAssignCampAct.get(Assign_Campaign_Activity_.TARGET), campaignActivityId),
                        builder.equal(fromAssignCampList.get(Assign_Campaign_List_.SOURCE), fromAssignCampAct.get(Assign_Campaign_Activity_.SOURCE)),
                        builder.equal(fromAssignCampList.get(Assign_Campaign_List_.TARGET), fromSubp.get(Subscription_.TARGET)),
                        builder.equal(fromSubp.get(Subscription_.SOURCE), fromSubAcc.get(SubscriberAccount_.OBJECTID)),
                        builder.not(fromSubAcc.get(SubscriberAccount_.EMAIL).in(emailQuery))
                )
        );

        List<String> results = objService.getEm().createQuery(query)
                .setFirstResult(startIndex)
                .setMaxResults(size)
                .getResultList();

        return results;
    }

    public List<Trigger_Email_Activity> getEmailTriggers(long campaignActivityId, String email) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<Trigger_Email_Activity> query = builder.createQuery(Trigger_Email_Activity.class);
        Root<Trigger_Email_Activity> fromTrigger = query.from(Trigger_Email_Activity.class);
        query.select(fromTrigger);

        query.where(
                builder.and(
                        builder.equal(fromTrigger.get(Trigger_Email_Activity_.TRIGGERING_OBJECT), campaignActivityId),
                        builder.equal(fromTrigger.get(Trigger_Email_Activity_.SUBCRIBER_EMAIL), email)
                )
        );

        List<Trigger_Email_Activity> results = objService.getEm().createQuery(query)
                .getResultList();

        return results;
    }
    
    /**
     * By using a map, this actually guarantees the uniqueness of the results. 
     * 1 subcriber to 1 code. If the subscriber is subscribed to multiple lists,
     * only the last subscription will be returned. During unsubcribing, we just
     * need to let the subscriber choose which list they would like to unsubscribe from.
     * 
     * @param emails
     * @param clientId
     * @return 
     */
    public Map<String,String> getUnsubscribeCodes(List<String> emails, long clientId) {
        //Simplest and least expensive solution.
        if(emails == null || emails.isEmpty())
            return new HashMap<>();
        
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<Object[]> query = builder.createQuery( Object[].class );
        Root<Subscription> fromSubscrp = query.from(Subscription.class);
        Root<SubscriberAccount> fromSubscrb = query.from(SubscriberAccount.class);
        Root<SubscriberOwnership> fromOwner = query.from(SubscriberOwnership.class);
        
        query.multiselect(fromSubscrb.get(SubscriberAccount_.EMAIL),fromSubscrp.get(Subscription_.UNSUBSCRIBE_KEY));
        
        query.where(
                builder.and(
                        fromSubscrb.get(SubscriberAccount_.EMAIL).in(emails),
                        builder.equal(fromSubscrb.get(SubscriberAccount_.OBJECTID), fromSubscrp.get(Subscription_.SOURCE)),
                        builder.equal(fromSubscrb.get(SubscriberAccount_.OBJECTID), fromOwner.get(SubscriberOwnership_.SOURCE)),
                        builder.equal(fromOwner.get(SubscriberOwnership_.TARGET), clientId)
                )
        );
        List<Object[]> results = objService.getEm().createQuery(query)
                .getResultList();
        
        Map<String,String> resultMap = new HashMap<>();
        for(Object[] result : results){
            resultMap.put(result[0].toString(), result[1].toString());
        }
        
        return resultMap;
    }
}
