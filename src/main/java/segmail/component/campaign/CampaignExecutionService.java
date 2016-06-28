/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.campaign;

import eds.component.GenericObjectService;
import eds.component.data.EntityNotFoundException;
import eds.component.data.RelationshipNotFoundException;
import eds.entity.audit.AuditedObject_;
import eds.entity.data.EnterpriseRelationship_;
import eds.entity.mail.Email;
import eds.entity.mail.Email_;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import segmail.entity.campaign.Assign_Campaign_Activity;
import segmail.entity.campaign.Assign_Campaign_Activity_;
import segmail.entity.campaign.Assign_Campaign_List;
import segmail.entity.campaign.Assign_Campaign_List_;
import segmail.entity.campaign.Campaign;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.campaign.CampaignActivityExecutionSchedule;
import segmail.entity.campaign.Trigger_Email_Activity;
import segmail.entity.campaign.Trigger_Email_Activity_;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberAccount_;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.Subscription_;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class CampaignExecutionService {
    
    public final int BATCH_SIZE = 1000;
    
    @EJB GenericObjectService objService;
    @EJB CampaignService campService;
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
        for(Long list : targetedLists) {
            if(!targetedListsString.isEmpty())
                targetedListsString += ",";
            targetedListsString += list.toString();
        }
        
        newExecution.setTARGETED_LIST_ID(targetedListsString);
        
        objService.getEm().persist(newExecution);
        
        return newExecution;        
    }
    
    /**
     * Executes the campaign activity from the [start]th subscriber to [start]+size th subscriber.
     * 
     * @param campaignActivityId
     * @param size 
     */
    public void executeCampaignActivity(long campaignActivityId, int startIndex, int size) 
            throws EntityNotFoundException, RelationshipNotFoundException {
        int currIndex = startIndex;
        final int targetIndex = currIndex + size; //Process until currIndex >= targetIndex
        
        CampaignActivity campaignActivity = campService.getCampaignActivity(campaignActivityId);
        if(campaignActivity == null)
            throw new EntityNotFoundException(CampaignActivity.class,campaignActivityId);
        
        //List<Campaign> campaigns = objService.getAllSourceObjectsFromTarget(campaignActivityId, Assign_Campaign_Activity.class, Campaign.class);
        //if(campaigns == null || campaigns.isEmpty())
        //    throw new RelationshipNotFoundException("Campaign not found for CampaignActivity "+campaignActivityId);
        
        //Campaign campaign = campaigns.get(0);
        
        //while(currIndex < targetIndex) {
            //Retrieve all subscriber emails
            List<String> subscriberEmails = this.getUnsentSubscriberEmailsForCampaign(campaignActivityId, currIndex, BATCH_SIZE);
            //Retreive all emails that were already sent 
            
            
            for(String email : subscriberEmails) {
                //Check if this email already exists
            }
        //}
        
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
    
    //Too much trouble!
    public List<String> getUnsentSubscriberEmailsForCampaign(long campaignActivityId, int startIndex, int size) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<String> query = builder.createQuery(String.class);
        Root<SubscriberAccount> fromSubAcc = query.from(SubscriberAccount.class);
        Root<Subscription> fromSubp = query.from(Subscription.class);
        Root<Assign_Campaign_List> fromAssignCampList = query.from(Assign_Campaign_List.class);
        Root<Assign_Campaign_Activity> fromAssignCampAct = query.from(Assign_Campaign_Activity.class);
        
        //Subquery
        Subquery<String> emailQuery = query.subquery(String.class);
        //Root<Email> fromEmail = emailQuery.from(Email.class);
        Root<Trigger_Email_Activity> fromTrigger = emailQuery.from(Trigger_Email_Activity.class);
        emailQuery.select(fromTrigger.get(Trigger_Email_Activity_.SUBCRIBER_EMAIL));
        
        emailQuery.where(
                builder.and(
                        builder.equal(fromTrigger.get(Trigger_Email_Activity_.TRIGGERING_OBJECT),campaignActivityId)
                )
        );
        
        query.select(fromSubAcc.get(SubscriberAccount_.EMAIL));
        query.where(
                builder.and(
                        builder.equal(fromAssignCampAct.get(Assign_Campaign_Activity_.TARGET), campaignActivityId),
                        builder.equal(fromAssignCampList.get(Assign_Campaign_List_.SOURCE), fromAssignCampAct.get(Assign_Campaign_Activity_.SOURCE)),
                        builder.equal(fromAssignCampList.get(Assign_Campaign_List_.TARGET), fromSubp.get(Subscription_.TARGET)),
                        builder.equal(fromSubp.get(Subscription_.SOURCE), fromSubAcc.get(SubscriberAccount_.OBJECTID))
                        //builder.not(fromSubAcc.get(SubscriberAccount_.EMAIL).in(emailQuery))
                )
        );
        
        List<String> results = objService.getEm().createQuery(query)
                .setFirstResult(startIndex)
                .setMaxResults(size)
                .getResultList();
        
        return results;
    }
    
}
