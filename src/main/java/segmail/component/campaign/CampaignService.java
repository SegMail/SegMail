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
import eds.entity.data.EnterpriseRelationship_;
import eds.entity.mail.Email;
import eds.entity.mail.Email_;
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
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.component.landing.LandingServerGenerationStrategy;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.campaign.ACTIVITY_STATUS;
import segmail.entity.campaign.ACTIVITY_TYPE;
import segmail.entity.campaign.Assign_Campaign_Activity;
import segmail.entity.campaign.Assign_Campaign_Activity_;
import segmail.entity.campaign.Assign_Campaign_Client;
import segmail.entity.campaign.Assign_Campaign_List;
import segmail.entity.campaign.Assign_Campaign_List_;
import segmail.entity.campaign.Campaign;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.campaign.CampaignActivitySchedule;
import segmail.entity.campaign.Trigger_Email_Activity;
import segmail.entity.campaign.Trigger_Email_Activity_;
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
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Campaign getCampaign(long campaignId) {
        Campaign campaign = objService.getEnterpriseObjectById(campaignId, Campaign.class);
        return campaign;
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
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
        
        CampaignActivity newActivity = new CampaignActivity();
        newActivity.setACTIVITY_NAME(name);
        newActivity.setACTIVITY_GOALS(goals);
        newActivity.setACTIVITY_TYPE(type.name());
        newActivity.setSTATUS(ACTIVITY_STATUS.NEW.name);
        
        validateCampaignActivity(newActivity);
        
        objService.getEm().persist(newActivity);
        
        //Assign campaign to activity (save 1 SQL query)
        //assignCampaignToActivity(campaignId,newActivity.getOBJECTID());
        Campaign campaign = getCampaign(campaignId);
        if(campaign == null)
            throw new EntityNotFoundException(Campaign.class,campaignId);
        
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
        return objService.getEm().merge(activity);
    }
    
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
        schedule.generateCronExp();
        
        return objService.getEm().merge(schedule);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Assign_Campaign_List> assignTargetListToCampaign(List<Long> targetLists, long campaignId) throws EntityNotFoundException {
        //Get all available lists for client and make sure client is authorized. (1 SQL query)
        List<SubscriptionList> toBeAssigned = new ArrayList<>();
        List<SubscriptionList> availableLists = subService.getAllListForClient(clientContainer.getClient().getOBJECTID());
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
        }
        return newAssigns;
    }
    
    /**
     * This is it. The real thing.
     * 
     * 1) Update the status of the email activity to STARTED.
     * 2) Create a sending schedule. This will trigger the process to load up the mail queue so that 
     * EmailService can send them out asynchronously. The process to trigger this process is also 
     * executed asynchronously.
     * 
     * @param emailActivity 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void startSendingCampaignEmail(CampaignActivity emailActivity) throws EntityNotFoundException, IncompleteDataException, BatchProcessingException {
        
        List<Campaign> campaigns = objService.getAllSourceObjectsFromTarget(emailActivity.getOBJECTID(), Assign_Campaign_Activity.class, Campaign.class);
        if(campaigns == null || campaigns.isEmpty())
            throw new EntityNotFoundException(Campaign.class,emailActivity.getOBJECTID());
        Campaign campaign = campaigns.get(0);
        
        //Should we write a method in GenericObjectService to retreive IDs only?
        List<SubscriptionList> lists = getTargetedLists(campaign.getOBJECTID());
        List<Long> listIds = new ArrayList<>();
        for(SubscriptionList list : lists) {
            listIds.add(list.getOBJECTID());
        }
        
        List<CampaignActivitySchedule> activitySchedule = objService.getEnterpriseData(emailActivity.getOBJECTID(), CampaignActivitySchedule.class);
        if(activitySchedule == null || activitySchedule.isEmpty())
            activitySchedule.add(this.createActivitySchedule(emailActivity.getOBJECTID(), 0, 0)); //If not found, create a new schedule
        
        //Create a batch job that will execute this exeSchedule until it gets done.
        batchScheduleService.createSingleStepJob(
                "Sending campaign email \""+emailActivity.getACTIVITY_NAME()+"\"", 
                "CampaignExecutionService", "executeCampaignActivity", 
                new Object[]{emailActivity.getOBJECTID()}, 
                landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN, ServerNodeType.ERP).getOBJECTID()       , 
                activitySchedule.get(0).getCRON_EXPRESSION());
        
        //That's all folks! Let's see the magic happening all by itself.
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
}
