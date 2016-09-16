/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.campaign;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.batch.BatchProcessingException;
import eds.component.batch.BatchSchedulingService;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import eds.entity.client.Client;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.joda.time.DateTime;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.component.landing.LandingServerGenerationStrategy;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;
import seca2.entity.landing.ServerInstance;
import segmail.component.subscription.ListService;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.campaign.ACTIVITY_STATUS;
import segmail.entity.campaign.ACTIVITY_TYPE;
import segmail.entity.campaign.Assign_Campaign_Activity;
import segmail.entity.campaign.Assign_Campaign_Client;
import segmail.entity.campaign.Assign_Campaign_List;
import segmail.entity.campaign.Assign_Campaign_List_;
import segmail.entity.campaign.Campaign;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.campaign.CampaignActivityOutboundLink;
import segmail.entity.campaign.CampaignActivitySchedule;
import segmail.entity.campaign.LinkClick;
import segmail.entity.campaign.LinkClick_;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberAccount_;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.Subscription_;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class CampaignService {
    
    @Inject ClientContainer clientContainer;
    
    @EJB GenericObjectService objService;
    @EJB UpdateObjectService updService;
    @EJB SubscriptionService subService;
    @EJB CampaignExecutionService campExecService; 
    @EJB BatchSchedulingService batchScheduleService;
    @EJB LandingService landingService;
    @EJB ListService listService;
    
    
    public Campaign getCampaign(long campaignId) {
        Campaign campaign = objService.getEnterpriseObjectById(campaignId, Campaign.class);
        return campaign;
    }
    
    
    public List<Campaign> getAllCampaignForClient(long clientId) {
        List<Campaign> results = objService.getAllSourceObjectsFromTarget(clientId, Assign_Campaign_Client.class, Campaign.class);
        return results;
    }
    
    /**
     * Creates and assigns Campaign to the calling Client.
     * 
     * @param campaignName
     * @param campaignGoals
     * @return
     * @throws RelationshipExistsException
     * @throws EntityNotFoundException
     * @throws IncompleteDataException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Campaign createCampaign(String campaignName, String campaignGoals) 
            throws RelationshipExistsException, EntityNotFoundException, IncompleteDataException {
        
        Campaign newCampaign = new Campaign();
        newCampaign.setCAMPAIGN_NAME(campaignName);
        newCampaign.setCAMPAIGN_GOALS(campaignGoals);
        
        validateCampaign(newCampaign);
        
        objService.getEm().persist(newCampaign);
        
        //Assign
        assignCampaignToClient(newCampaign.getOBJECTID(), clientContainer.getClient().getOBJECTID());
        
        return newCampaign;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Assign_Campaign_Client assignCampaignToClient(long campaignId, long clientId) throws RelationshipExistsException, EntityNotFoundException {
        //Only 1 unique campaign per client
        List<Assign_Campaign_Client> existingAssigns = objService.getRelationshipsForObject(campaignId, clientId, Assign_Campaign_Client.class);
        if(existingAssigns != null && !existingAssigns.isEmpty())
            throw new RelationshipExistsException(existingAssigns.get(0));
        
        Campaign campaign = this.getCampaign(campaignId);
        if(campaign == null)
            throw new EntityNotFoundException(Campaign.class,campaignId);
        
        Client client = objService.getEnterpriseObjectById(clientId, Client.class);
        if(client == null)
            throw new EntityNotFoundException(Client.class,clientId);
        
        Assign_Campaign_Client newAssign = new Assign_Campaign_Client();
        newAssign.setSOURCE(campaign);
        newAssign.setTARGET(client);
        
        objService.getEm().persist(newAssign);
        
        return newAssign;
    }
    
    /**
     * Validates and updates the Campaign in database.
     * 
     * @param campaign
     * @return
     * @throws IncompleteDataException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Campaign updateCampaign(Campaign campaign) throws IncompleteDataException {
        this.validateCampaign(campaign);
        
        return objService.getEm().merge(campaign);
    }
    
    public void validateCampaign(Campaign campaign) throws IncompleteDataException {
        if(campaign.getCAMPAIGN_NAME() == null || campaign.getCAMPAIGN_NAME().isEmpty())
            throw new IncompleteDataException("Please enter a Campaign name.");
        
        if(campaign.getCAMPAIGN_GOALS() == null || campaign.getCAMPAIGN_GOALS().isEmpty())
            throw new IncompleteDataException("Please enter at least 1 Campaign goal.");
        
    }
    
    /**
     * Creates a CampaignActivity, assigns it to the Campaign with campaignId,
     * and creates an default CampaignActivitySchedule.
     * 
     * @param campaignId
     * @param name
     * @param goals
     * @param type
     * @return
     * @throws IncompleteDataException
     * @throws EntityNotFoundException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CampaignActivity createCampaignActivity(long campaignId, String name, String goals, ACTIVITY_TYPE type) throws IncompleteDataException, EntityNotFoundException {
        
        //Assign campaign to activity (save 1 SQL query)
        //assignCampaignToActivity(campaignId,newActivity.getOBJECTID());
        Campaign campaign = getCampaign(campaignId);
        if(campaign == null)
            throw new EntityNotFoundException(Campaign.class,campaignId);
        
        CampaignActivity newActivity = new CampaignActivity();
        newActivity.setACTIVITY_NAME(name);
        newActivity.setACTIVITY_GOALS(goals);
        newActivity.setACTIVITY_TYPE(type.name());
        newActivity.setSTATUS(ACTIVITY_STATUS.NEW.name);
        
        validateCampaignActivity(newActivity);
        
        objService.getEm().persist(newActivity);
        
        Assign_Campaign_Activity newAssign = new Assign_Campaign_Activity();
        newAssign.setSOURCE(campaign);
        newAssign.setTARGET(newActivity);
        
        objService.getEm().persist(newAssign);
        
        //Have to create schedule here or else if the creation fails, this object
        //will not be rolled back
        createActivitySchedule(newActivity.getOBJECTID(), 0, 0);
        
        return newActivity;
    }
    
    
    /**
     * More than 1 Campaigns can share the same activity - eg. collaboration between
     * 2 organizations. Client access to CampaignActivities are transitive for now
     * - ie. If the Client has the access to a Campaign, they will have access to 
     * all activities under that Campaign by this relationship.
     * 
     * @param campaignId
     * @param activityId
     * @return 
     * @throws eds.component.data.EntityNotFoundException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Assign_Campaign_Activity assignCampaignToActivity(long campaignId, long activityId) 
            throws EntityNotFoundException {
        Campaign campaign = getCampaign(campaignId);
        if(campaign == null)
            throw new EntityNotFoundException(Campaign.class,campaignId);
        
        CampaignActivity activity = objService.getEnterpriseObjectById(activityId, CampaignActivity.class);
        if(activity == null)
            throw new EntityNotFoundException(CampaignActivity.class,activityId);
        
        Assign_Campaign_Activity newAssign = new Assign_Campaign_Activity();
        newAssign.setSOURCE(campaign);
        newAssign.setTARGET(activity);
        
        objService.getEm().persist(newAssign);
        
        return newAssign;
        
    }
    
    public List<CampaignActivity> getAllActivitiesForCampaign(long campaignId) {
        List<CampaignActivity> results = objService.getAllTargetObjectsFromSource(campaignId, Assign_Campaign_Activity.class, CampaignActivity.class);
        return results;
    }
    
    public CampaignActivity getCampaignActivity(long campaignActivityId) {
        return objService.getEnterpriseObjectById(campaignActivityId, CampaignActivity.class);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CampaignActivity updateCampaignActivity(CampaignActivity activity) 
            throws IncompleteDataException {
        validateCampaignActivity(activity);
        activity.setSTATUS(ACTIVITY_STATUS.EDITING.name); //Not a good idea to put this in validation.
        
        return objService.getEm().merge(activity);
    }
    
    /**
     * Should validators be in EJB services? 
     * 
     * @param activity
     * @throws IncompleteDataException 
     */
    public void validateCampaignActivity(CampaignActivity activity) 
            throws IncompleteDataException {
        if(activity.getACTIVITY_NAME() == null || activity.getACTIVITY_NAME().isEmpty())
            throw new IncompleteDataException("Campaign activities must have a name.");
        
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int deleteCampaignActivity(long campaignActivityId) throws EntityNotFoundException {
        int deleted = updService.deleteObjectDataAndRelationships(campaignActivityId);
        return deleted;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CampaignActivitySchedule createActivitySchedule(long activityId, int everyNHours, long sendInBatch) throws EntityNotFoundException {
        CampaignActivity activity = this.getCampaignActivity(activityId);
        
        if(activity == null)
            throw new EntityNotFoundException(CampaignActivity.class,activityId);
        
        CampaignActivitySchedule schedule = new CampaignActivitySchedule();
        schedule.generateCronExp();
        schedule.setOWNER(activity);
        schedule.setSEND_IN_BATCH(sendInBatch);
        
        objService.getEm().persist(schedule);
        
        return schedule;
    }

    public CampaignActivitySchedule getCampaignActivitySchedule(long activityId) {
        List<CampaignActivitySchedule> results = objService.getEnterpriseData(activityId, CampaignActivitySchedule.class);
        if(results == null || results.isEmpty())
            return null;
        return results.get(0);
    }
    
    public List<SubscriptionList> getTargetedLists(long campaignId) {
        List<SubscriptionList> targetLists = objService.getAllTargetObjectsFromSource(campaignId, Assign_Campaign_List.class, SubscriptionList.class);
        return targetLists;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CampaignActivitySchedule updateCampaignActivitySchedule(CampaignActivitySchedule schedule) {
        //schedule.generateCronExp();
        return objService.getEm().merge(schedule);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Assign_Campaign_List> assignTargetListToCampaign(List<Long> targetLists, long campaignId) throws EntityNotFoundException {
        //Get all available lists for client and make sure client is authorized. (1 SQL query)
        List<SubscriptionList> toBeAssigned = new ArrayList<>();
        List<SubscriptionList> availableLists = listService.getAllListForClient(clientContainer.getClient().getOBJECTID());
        for(Long inList : targetLists) {
            boolean found = false;
            for(SubscriptionList availList : availableLists) {
                if(inList.equals(availList.getOBJECTID())) {
                    toBeAssigned.add(availList); //To be updated to the DB later.
                    found = true;
                }
                    
            }
            if(!found)
                throw new EntityNotFoundException(SubscriptionList.class,inList);
        }
        
        //Get Campaign object (1 SQL query)
        Campaign campaign = this.getCampaign(campaignId);
        
        //Delete all existing (1 SQL query)
        int results = updService.deleteRelationshipBySource(campaignId, Assign_Campaign_List.class);
        
        //Create all new (1 SQL query?)
        List<Assign_Campaign_List> newAssigns = new ArrayList<>();
        for(SubscriptionList targetList : toBeAssigned) {
            Assign_Campaign_List newAssign = new Assign_Campaign_List();
            newAssign.setSOURCE(campaign);
            newAssign.setTARGET(targetList);
            
            newAssigns.add(newAssign);
            updService.getEm().persist(newAssign);
            
            //Set Sender's attributes in Campaign with the first list that was assigned
            if(campaign.getOVERRIDE_SEND_AS_EMAIL() == null || campaign.getOVERRIDE_SEND_AS_EMAIL().isEmpty()) {
                campaign.setOVERRIDE_SEND_AS_EMAIL(targetList.getSEND_AS_EMAIL());
                updService.getEm().merge(campaign);
            }
            
            if(campaign.getOVERRIDE_SEND_AS_NAME()== null || campaign.getOVERRIDE_SEND_AS_NAME().isEmpty()) {
                campaign.setOVERRIDE_SEND_AS_NAME(targetList.getSEND_AS_NAME());
                updService.getEm().merge(campaign);   
            }
            
        }
        return newAssigns;
    }
    
    /**
     * This is it. The real thing.
     * 
     * 1) Update the status of the email activity to STARTED.
     * 2) Create a sending schedule, which is the process to load up the mail queue so that 
     * EmailService can send them out asynchronously. This process shall also be 
     * executed asynchronously. 
     * 3) If this doesn't throw any exceptions, it means that the sending schedule 
     * is guaranteed to execute even after a system restart. Otherwise, rollback
     * will occur and the sending schedule created halfway will be destroyed.
     * 
     * @param emailActivity 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void startSendingCampaignEmail(CampaignActivity emailActivity) 
            throws BatchProcessingException, EntityNotFoundException, IncompleteDataException {
        /**
         * Schedule the first one and let the subsequent ones keep scheduling 
         * the subsequent ones until the lists are done.
         */
        
        List<CampaignActivitySchedule> scheduleList = objService.getEnterpriseData(emailActivity.getOBJECTID(), CampaignActivitySchedule.class);
        if(scheduleList == null || scheduleList.isEmpty())
            throw new IncompleteDataException("No CampaignActivitySchedule set for CampaignActivity "+emailActivity.getACTIVITY_NAME());
        CampaignActivitySchedule schedule = scheduleList.get(0);
        Object[] params = {emailActivity.getOBJECTID(), (int)schedule.getSEND_IN_BATCH() };
        
        DateTime now = DateTime.now();
        batchScheduleService.createSingleStepJob(
                emailActivity.getACTIVITY_TYPE()+" "+emailActivity.getACTIVITY_NAME(), 
                "CampaignExecutionService", 
                "executeCampaignActivity", 
                params,
                landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN, ServerNodeType.ERP).getOBJECTID(),
                schedule.generateCronExp(now).getCRON_EXPRESSION(),
                now);
        
        //Actually we don't need to do this but just for consistency sake
        //CRON_EXPRESSION don't need to be stored in the first place
        objService.getEm().merge(schedule);
        
        //Update the status of the activity
        emailActivity.setSTATUS(ACTIVITY_STATUS.EXECUTING.name);
        objService.getEm().merge(emailActivity);
        
    }
    
    public List<SubscriberAccount> getTargetedSubscribers(long campaignId, int startIndex, int maxResults) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<SubscriberAccount> query = builder.createQuery(SubscriberAccount.class);
        Root<SubscriberAccount> fromSubscrAcc = query.from(SubscriberAccount.class);
        Root<Subscription> fromSubscr = query.from(Subscription.class);
        Root<Assign_Campaign_List> fromAssign = query.from(Assign_Campaign_List.class);
        
        query.where(
                builder.and(
                        builder.equal(fromAssign.get(Assign_Campaign_List_.SOURCE), campaignId),
                        builder.equal(fromAssign.get(Assign_Campaign_List_.TARGET), fromSubscr.get(Subscription_.TARGET)),
                        builder.equal(fromSubscrAcc.get(SubscriberAccount_.OBJECTID), fromSubscr.get(Subscription_.SOURCE))
                )
        );
        
        query.distinct(true);
        query.select(fromSubscrAcc);
        
        List<SubscriberAccount> results  = objService.getEm().createQuery(query)
                .setFirstResult(startIndex)
                .setMaxResults(maxResults)
                .getResultList();
        
        return results;
        
    }
    
    public List<String> getTargetedSubscribersEmail(long campaignId, int startIndex, int maxResults) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<String> query = builder.createQuery(String.class);
        Root<SubscriberAccount> fromSubscrAcc = query.from(SubscriberAccount.class);
        Root<Subscription> fromSubscr = query.from(Subscription.class);
        Root<Assign_Campaign_List> fromAssign = query.from(Assign_Campaign_List.class);
        
        query.where(
                builder.and(
                        builder.equal(fromAssign.get(Assign_Campaign_List_.SOURCE), campaignId),
                        builder.equal(fromAssign.get(Assign_Campaign_List_.TARGET), fromSubscr.get(Subscription_.TARGET)),
                        builder.equal(fromSubscrAcc.get(SubscriberAccount_.OBJECTID), fromSubscr.get(Subscription_.SOURCE))
                )
        );
        
        query.distinct(true);
        query.select(fromSubscrAcc.get(SubscriberAccount_.EMAIL));
        
        List<String> results  = objService.getEm().createQuery(query)
                .setFirstResult(startIndex)
                .setMaxResults(maxResults)
                .getResultList();
        
        return results;
        
    }
    
    /**
     * There are normal links and also mailmerge links. If it is a normal link,
     * return the correct redirect URL that will lead to the intended target. If 
     * it is a mailmerge link, return an example link that will allow the user to
     * test out by clicking the link and come to the intended page. 
     * 
     * @param activity
     * @param linkTarget
     * @param linkText
     * @param index
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CampaignActivityOutboundLink createOrUpdateLink(CampaignActivity activity, String linkTarget, String linkText, int index) {
        
        //Get the existing link first
        List<CampaignActivityOutboundLink> allLinks = objService.getEnterpriseData(activity.getOBJECTID(), CampaignActivityOutboundLink.class);
        
        CampaignActivityOutboundLink selectedLink = new CampaignActivityOutboundLink();
        selectedLink.setOWNER(activity);
        selectedLink.setSNO(index);
        //If found
        for(CampaignActivityOutboundLink link : allLinks) {
            if(link.getSNO() == index){
                selectedLink = objService.getEm().merge(link);
                break;
            }
        }
        selectedLink.setLINK_TARGET(linkTarget);
        selectedLink.setLINK_TEXT(linkText);
        //If not found
        if(selectedLink.getLINK_KEY() == null || selectedLink.getLINK_KEY().isEmpty())
            objService.getEm().persist(selectedLink);
        
        
        return selectedLink;
    }
    
    /**
     * 
     * @param link
     * @return
     * @throws IncompleteDataException 
     */
    public String constructLink(CampaignActivityOutboundLink link) throws IncompleteDataException {
        ServerInstance server = landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN, ServerNodeType.WEB);
        
        
        
        return server.getURI() + "/link/" + link.getLINK_KEY();
    }
    
    public long getLinkClicks(String key) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<LinkClick> fromClicks = query.from(LinkClick.class);
        
        query.select(builder.count(fromClicks));
        query.where(builder.equal(fromClicks.get(LinkClick_.LINK_KEY), key));
        
        Long result = objService.getEm().createQuery(query)
                .getSingleResult();
        
        return result;
    }
    
    
}
