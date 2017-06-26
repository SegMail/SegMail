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
import eds.component.data.DataValidationException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import eds.entity.batch.BatchJobContainer;
import eds.entity.client.Client;
import eds.entity.client.ContactInfo;
import eds.entity.client.VerifiedSendingAddress;
import java.io.IOException;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.component.landing.LandingServerGenerationStrategy;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;
import seca2.entity.landing.ServerInstance;
import segmail.component.subscription.ListService;
import segmail.component.subscription.SubscriptionService;
import segmail.component.subscription.mailmerge.MailMergeService;
import segmail.entity.campaign.ACTIVITY_STATUS;
import segmail.entity.campaign.ACTIVITY_TYPE;
import segmail.entity.campaign.Assign_Campaign_Activity;
import segmail.entity.campaign.Assign_Campaign_Activity_;
import segmail.entity.campaign.Assign_Campaign_Client;
import segmail.entity.campaign.Assign_Campaign_List;
import segmail.entity.campaign.Assign_Campaign_List_;
import segmail.entity.campaign.Campaign;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.campaign.link.CampaignActivityOutboundLink;
import segmail.entity.campaign.link.CampaignActivityOutboundLink_;
import segmail.entity.campaign.CampaignActivitySchedule;
import segmail.entity.campaign.link.CampaignLinkClick;
import segmail.entity.campaign.link.CampaignLinkClick_;
import segmail.entity.campaign.Trigger_Email_Activity;
import segmail.entity.campaign.Trigger_Email_Activity_;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberAccount_;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.SubscriptionListField_;
import segmail.entity.subscription.Subscription_;
import segmail.entity.subscription.email.mailmerge.MAILMERGE_REQUEST;

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
    @EJB MailMergeService mmService;
    
    @EJB BatchJobContainer batchJobCont;
    
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
     * Sets the client's first VerifiedSendingAddress as the Send As.
     * 
     * @param campaignName
     * @param campaignGoals
     * @return
     * @throws RelationshipExistsException
     * @throws EntityNotFoundException
     * @throws IncompleteDataException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Campaign createCampaign(String campaignName, String campaignGoals, Client client) 
            throws RelationshipExistsException, EntityNotFoundException, IncompleteDataException {
        
        Campaign newCampaign = new Campaign();
        newCampaign.setCAMPAIGN_NAME(campaignName);
        newCampaign.setCAMPAIGN_GOALS(campaignGoals);
        
        validateCampaign(newCampaign);
        
        objService.getEm().persist(newCampaign);
        
        //Assign
        assignCampaignToClient(newCampaign.getOBJECTID(), client.getOBJECTID());
        
        //Set default attributes for Send As email
        List<VerifiedSendingAddress> addresses = objService.getEnterpriseData(client.getOBJECTID(), VerifiedSendingAddress.class);
        if(addresses != null && !addresses.isEmpty()) {
            VerifiedSendingAddress address = addresses.get(0);
            newCampaign.setOVERRIDE_SEND_AS_EMAIL(address.getVERIFIED_ADDRESS());
        }
        //Set default attributes for Send As name
        List<ContactInfo> contacts = objService.getEnterpriseData(client.getOBJECTID(), ContactInfo.class);
        if(contacts != null && !contacts.isEmpty()) {
            ContactInfo contact = contacts.get(0);
            newCampaign.setOVERRIDE_SEND_AS_NAME(contact.getFIRSTNAME());
        }
        
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
        newActivity.setACTIVITY_TYPE(type.name);
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
            throws IncompleteDataException, DataValidationException, EntityNotFoundException {
        
        validateCampaignActivity(activity);
        activity.setSTATUS(ACTIVITY_STATUS.EDITING.name); //Not a good idea to put this in validation.
        activity = parsePreview(activity);
        
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
        int deleted = updService.deleteObjectDataAndRelationships(campaignActivityId,CampaignActivity.class);
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
    
    public List<SubscriptionListField> getTargetedListFields(long campaignId) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<SubscriptionListField> query = builder.createQuery(SubscriptionListField.class);
        Root<SubscriptionListField> fromFields = query.from(SubscriptionListField.class);
        Root<Assign_Campaign_List> fromAssign = query.from(Assign_Campaign_List.class);
        
        query.select(fromFields);
        query.where(builder.and(
                builder.equal(fromAssign.get(Assign_Campaign_List_.SOURCE), campaignId),
                builder.equal(fromAssign.get(Assign_Campaign_List_.TARGET), fromFields.get(SubscriptionListField_.OWNER))
                ));
        
        List<SubscriptionListField> results = objService.getEm().createQuery(query)
                .getResultList();
        
        return results;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CampaignActivitySchedule updateCampaignActivitySchedule(CampaignActivitySchedule schedule) {
        //schedule.generateCronExp();
        return objService.getEm().merge(schedule);
    }
    
    /**
     * Full refresh of all targeted SubscriptionList. Checks if there are any 
     * existing executing CampaignActivities.
     * 
     * @param targetLists
     * @param campaignId
     * @return
     * @throws EntityNotFoundException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Assign_Campaign_List> assignTargetListToCampaign(List<Long> targetLists, long campaignId) 
            throws EntityNotFoundException, DataValidationException {
        //Check if there are any executing CampaignActivities
        List<CampaignActivity> activities = this.getAllActivitiesForCampaign(campaignId);
        for(CampaignActivity activity : activities) {
            if(activity.getSTATUS() != null && activity.getSTATUS().equals(ACTIVITY_STATUS.EXECUTING.name))
                throw new DataValidationException("Campaign activity \""+activity.getACTIVITY_NAME()+"\" is executing. "
                        + "No list should be assigned or unassigned at this point in time. "
                        + "Please wait till it is completed.");
        }
        
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
                //updService.getEm().merge(campaign); //no need because it is already managed
            }
            
            if(campaign.getOVERRIDE_SEND_AS_NAME()== null || campaign.getOVERRIDE_SEND_AS_NAME().isEmpty()) {
                campaign.setOVERRIDE_SEND_AS_NAME(targetList.getSEND_AS_NAME());
                //updService.getEm().merge(campaign);   //no need because it is already managed
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
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void startSendingCampaignEmail(CampaignActivity emailActivity) 
            throws BatchProcessingException, EntityNotFoundException, IncompleteDataException, IOException, DataValidationException {
        /**
         * Schedule the first one and let the subsequent ones keep scheduling 
         * the subsequent ones until the lists are done.
         */
        List<Campaign> campaigns = objService.getAllSourceObjectsFromTarget(emailActivity.getOBJECTID(), Assign_Campaign_Activity.class, Campaign.class);
        if(campaigns == null || campaigns.isEmpty()) {
            throw new EntityNotFoundException("No Campaign found for this CampaignActivity.");
        }
        Campaign campaign = campaigns.get(0);
        if(campaign.getOVERRIDE_SEND_AS_EMAIL() == null || campaign.getOVERRIDE_SEND_AS_EMAIL().isEmpty() ||
                campaign.getOVERRIDE_SEND_AS_NAME() == null || campaign.getOVERRIDE_SEND_AS_NAME().isEmpty()) {
            throw new IncompleteDataException("You need to set Send As name and address for all Campaigns");
        }
        
        List<CampaignActivitySchedule> scheduleList = objService.getEnterpriseData(emailActivity.getOBJECTID(), CampaignActivitySchedule.class);
        if(scheduleList == null || scheduleList.isEmpty())
            throw new IncompleteDataException("No CampaignActivitySchedule set for CampaignActivity "+emailActivity.getACTIVITY_NAME());
        CampaignActivitySchedule schedule = scheduleList.get(0);
        
        List<SubscriptionList> targetLists = objService.getAllTargetObjectsFromSource(campaign.getOBJECTID(), Assign_Campaign_List.class, SubscriptionList.class); //DB hit
        if (targetLists == null || targetLists.isEmpty())
            throw new IncompleteDataException("Campaign "+campaign.getOBJECTID()+" is not assigned any target lists.");
        
        
        DateTime now = DateTime.now();
        batchJobCont.create(Campaign.class.getSimpleName()+" "+emailActivity.getACTIVITY_TYPE()+" "+emailActivity.getACTIVITY_NAME());
        batchJobCont.addStep(CampaignExecutionService.class.getSimpleName(),"executeCampaignActivity",new Object[]{emailActivity.getOBJECTID(),(int)schedule.getSEND_IN_BATCH()});
        batchJobCont.addCondition(CampaignExecutionService.class.getSimpleName(),"continueCampaignActivity",new Object[]{emailActivity.getOBJECTID()});
        batchJobCont.setSchedule(schedule.generateCronExp(now).getCRON_EXPRESSION());
        batchJobCont.schedule(now);
        /*
        batchScheduleService.createSingleStepJob(
                emailActivity.getACTIVITY_TYPE()+" "+emailActivity.getACTIVITY_NAME(), 
                "CampaignExecutionService", 
                "executeCampaignActivity", 
                params,
                landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN, ServerNodeType.ERP).getOBJECTID(),
                schedule.generateCronExp(now).getCRON_EXPRESSION(),
                now);
        */
        
        //Update the status of the activity
        emailActivity.setSTATUS(ACTIVITY_STATUS.EXECUTING.name);
        updService.merge(emailActivity); //New transaction
        
    }
    
    /**
     * Using campaignId, get all current targeted subscribers.
     * Campaign -> Assign_Campaign_List -> Subscription -> SubscriberAccount
     * 
     * @param campaignId
     * @param startIndex
     * @param maxResults
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
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
        query.orderBy(builder.asc(fromSubscrAcc.get(SubscriberAccount_.EMAIL)));
        
        List<SubscriberAccount> results  = objService.getEm().createQuery(query)
                .setFirstResult(startIndex)
                .setMaxResults(maxResults)
                .getResultList();
        
        return results;
        
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
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
        query.orderBy(builder.asc(fromSubscrAcc.get(SubscriberAccount_.EMAIL)));
        
        List<String> results  = objService.getEm().createQuery(query)
                .setFirstResult(startIndex)
                .setMaxResults(maxResults)
                .getResultList();
        
        return results;
        
    }
    
    /**
     * If the link exists, update its LINK_TARGET and LINK_TEXT. If not, create 
     * it. Set its ACTIVE flag to true. All links that have been created or updated
     * are now active within the CampaignActivity.
     * 
     * @param activity
     * @param linkTarget
     * @param linkText
     * @param index
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CampaignActivityOutboundLink createOrUpdateLink(CampaignActivity activity, String linkTarget, String linkText, int index) throws IncompleteDataException {
        
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
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public long getLinkClicks(String key) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<CampaignLinkClick> fromClicks = query.from(CampaignLinkClick.class);
        
        query.select(builder.count(fromClicks));
        query.where(builder.equal(fromClicks.get(CampaignLinkClick_.LINK_KEY), key));
        
        Long result = objService.getEm().createQuery(query)
                .getSingleResult();
        
        return result;
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public long getTotalLinkClicksForActivity(long activityId) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<CampaignLinkClick> fromClicks = query.from(CampaignLinkClick.class);
        Root<CampaignActivityOutboundLink> fromLinks = query.from(CampaignActivityOutboundLink.class);
        
        query.select(builder.count(fromClicks));
        query.where(
                builder.and(
                        builder.equal(fromLinks.get(CampaignActivityOutboundLink_.OWNER), activityId),
                        builder.equal(fromLinks.get(CampaignActivityOutboundLink_.LINK_KEY),fromClicks.get(CampaignLinkClick_.LINK_KEY))
                )
        );
        
        Long result = objService.getEm().createQuery(query)
                .getSingleResult();
        
        return result;
    } 
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public long getTotalLinkClicksForCampaign(long campaignId) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<CampaignLinkClick> fromClicks = query.from(CampaignLinkClick.class);
        Root<CampaignActivityOutboundLink> fromLinks = query.from(CampaignActivityOutboundLink.class);
        Root<Assign_Campaign_Activity> fromAssign = query.from(Assign_Campaign_Activity.class);
        
        query.select(builder.count(fromClicks));
        query.where(
                builder.and(
                        builder.equal(fromAssign.get(Assign_Campaign_Activity_.SOURCE), campaignId),
                        builder.equal(fromLinks.get(CampaignActivityOutboundLink_.OWNER), fromAssign.get(Assign_Campaign_Activity_.TARGET)),
                        builder.equal(fromLinks.get(CampaignActivityOutboundLink_.LINK_KEY),fromClicks.get(CampaignLinkClick_.LINK_KEY))
                )
        );
        
        Long result = objService.getEm().createQuery(query)
                .getSingleResult();
        
        return result;
    } 
    
    /**
     * Processes 3 things:
     * 1) Mailmerge links eg. confirm, unsubscribe, etc.
     * 2) Outbound links eg. http://segmail.io
     * 3) Campaign-specific tags eg. Sender's name, Sender's email, Support email, etc.
     * 
     * The 1st 2 are to be delegated to MailmergeService while the last is to be 
     * done here. The result will be stored in CampaignActivity.ACTIVITY_CONTENT_PREVIEW.
     * 
     * This is strictly for generating previews for CampaignActivities. If you need
     * to send out actual Campaign emails, use the 
     * {@link #campExecService.sendEmails(Campaign,CampaignActivity,List<SubscriberAccount>,
     * List<Client>,List<SubscriptionListField>)} method.
     * 
     * 
     * @param editingContent
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CampaignActivity parsePreview(CampaignActivity activity) throws DataValidationException, IncompleteDataException, EntityNotFoundException {
        
        //Retrieve this first because it has 2 DB hits
        List<Campaign> campaigns = this.objService.getAllSourceObjectsFromTarget(activity.getOBJECTID(), Assign_Campaign_Activity.class, Campaign.class);
        if(campaigns == null || campaigns.isEmpty())
            throw new EntityNotFoundException("No Campaign found for CampaignActivity "+activity.getOBJECT_NAME());
        
        String processedContent = parseAndUpdateLinks(activity, activity.getACTIVITY_CONTENT());
        
        //Mailmerge links - generate test links
        MAILMERGE_REQUEST[] mmReqs = MAILMERGE_REQUEST.values();
        for(MAILMERGE_REQUEST mmReq : mmReqs) {
            String tag = mmReq.label;
            
            Element a = new Element(Tag.valueOf("a"),"");
            a.attr("href", mmService.getSystemTestLink(tag));
            a.html(mmReq.defaultHtmlText);
            a.attr("target", "_blank");
            
            String replacementElem = a.outerHtml();
            processedContent = processedContent.replace(tag, replacementElem);
            
        }
        
        //Mailmerge tags (with random subscriber)
        //Can't do it here because the exact values will be saved into the DB!
        //This must be done in the UI 
        //But we can do Campaign attributes
        processedContent = mmService.parseStandardCampaignTags(processedContent, campaigns.get(0));
        
        activity.setACTIVITY_CONTENT_PREVIEW(processedContent);
        
        return activity;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String parseAndUpdateLinks(CampaignActivity activity, String body) throws IncompleteDataException {
        //Outbound links - generate tracking links without subscriber ID
        //links will be appended with the subscriber's ID when it is sent out
        String editingContent = body;
        Document doc = Jsoup.parse(editingContent);
        Elements links = doc.select("a");
        //Set all as inactive first
        updService.deleteAllEnterpriseDataByType(activity.getOBJECTID(), CampaignActivityOutboundLink.class);
        
        //Update those that are still active (in use)
        for(int i=0; i<links.size(); i++) {
            Element link = links.get(i);
            String target = link.attr("href");
            String text = link.html();
            //If text is image, replace it with a generic image tab
            if(!link.getElementsByTag("img").isEmpty())
                text = "[image]";
            
            CampaignActivityOutboundLink outboundLink = new CampaignActivityOutboundLink();
            outboundLink.setOWNER(activity);
            outboundLink.setSNO(i);
            outboundLink.setLINK_TARGET(target);
            outboundLink.setLINK_TEXT(text);
            outboundLink.setACTIVE(true);
            objService.getEm().persist(outboundLink);
            
            //Construct link
            String redirectLink = constructLink(outboundLink);
            
            //Modify the existing link
            link.attr("href", redirectLink);
            link.attr("data-link", (String) outboundLink.generateKey());
            link.attr("target", "_blank");
        }
        
        editingContent = doc.outerHtml();
        
        return editingContent;
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<String> getSentEmails(long campaignActivityId, int startIndex, int size) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<String> query = builder.createQuery(String.class);
        Root<Trigger_Email_Activity> fromActivity = query.from(Trigger_Email_Activity.class);
        
        query.distinct(true);
        query.select(fromActivity.get(Trigger_Email_Activity_.SUBCRIBER_EMAIL));
        query.where(builder.equal(fromActivity.get(Trigger_Email_Activity_.TRIGGERING_OBJECT),campaignActivityId));
        query.orderBy(builder.asc(fromActivity.get(Trigger_Email_Activity_.SUBCRIBER_EMAIL)));
        
        List<String> results = objService.getEm().createQuery(query)
                .setFirstResult(startIndex)
                .setMaxResults(size)
                .getResultList();
        
        return results;
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public long countEmailsSentForActivity(long activityId) {
        //Just count the total number of Trigger_Email_Activity !
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Trigger_Email_Activity> fromTrigger = query.from(Trigger_Email_Activity.class);
        
        query.select(builder.count(fromTrigger));
        query.where(builder.and(
                builder.equal(fromTrigger.get(Trigger_Email_Activity_.TRIGGERING_OBJECT), activityId)//,
                ));
        
        Long result = objService.getEm().createQuery(query)
                .getSingleResult();
        
        return result;
    }
    
    /**
     * The implementation of this method is based on the way we execute the activities.
     * At the point of execution, we do not know who we are going to send to, if 
     * a subscriber is actively subscribed to a targeted list, then we will send 
     * to this subscriber, otherwise, if the subscription turns inactive at the point
     * of execution, we won't be able to send this campaign to this subscriber.
     * So the only way to forecast how many subscribers we are going to send to,
     * we use 2 numbers:
     * <ul>
     * <li>Unsent</li>
     * <li>Sent</li>
     * </ul>
     * Unsent will be changing throughout the lifetime of the campaign as new subscribers
     * are activated or existing subscribers unsubscribes. Sent will be a growing 
     * number but will not change after the activity has completed.
     * <br>
     * Hence, once completed, the targeted count of a campaign should be the number 
     * sent because the unsent number will keep growing even though the activity
     * has completed and the new subscribers will never be targeted for this activity.
     * <br>
     * Returns -1 if the campaignActivityId provided is not valid.
     * 
     * @param campaignActivityId
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public long countTargetedSubscribersForActivity(long campaignActivityId) {
        CampaignActivity activity = objService.getEnterpriseObjectById(campaignActivityId, CampaignActivity.class);
        if(activity == null)
            return -1;
        if(activity.getSTATUS().equals(ACTIVITY_STATUS.COMPLETED.name))
            return countEmailsSentForActivity(campaignActivityId);
        
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<SubscriberAccount> fromSubscrAcc = query.from(SubscriberAccount.class);
        Root<Subscription> fromSubscr = query.from(Subscription.class);
        Root<Assign_Campaign_List> fromAssign = query.from(Assign_Campaign_List.class);
        Root<Assign_Campaign_Activity> fromCamp = query.from(Assign_Campaign_Activity.class);
        
        query.where(
                builder.and(
                        builder.equal(fromCamp.get(Assign_Campaign_Activity_.TARGET), campaignActivityId),
                        builder.equal(fromAssign.get(Assign_Campaign_List_.SOURCE), fromCamp.get(Assign_Campaign_Activity_.SOURCE)),
                        builder.equal(fromAssign.get(Assign_Campaign_List_.TARGET), fromSubscr.get(Subscription_.TARGET)),
                        builder.equal(fromSubscrAcc.get(SubscriberAccount_.OBJECTID), fromSubscr.get(Subscription_.SOURCE))
                )
        );
        
        query.select(builder.countDistinct(fromSubscrAcc));
        
        Long result  = objService.getEm().createQuery(query)
                .getSingleResult();
        
        return result;
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public long countConvertedEmails(long campaignActivityId) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<CampaignActivityOutboundLink> fromLink = query.from(CampaignActivityOutboundLink.class);
        Root<CampaignLinkClick> fromClicks = query.from(CampaignLinkClick.class);
        
        query.select(builder.countDistinct(fromClicks.get(CampaignLinkClick_.SOURCE_KEY)));
        query.where(builder.and(
                builder.equal(fromLink.get(CampaignActivityOutboundLink_.OWNER), campaignActivityId),
                builder.equal(fromLink.get(CampaignActivityOutboundLink_.LINK_KEY), fromClicks.get(CampaignLinkClick_.LINK_KEY))
        ));
        
        long result = objService.getEm().createQuery(query)
                .getSingleResult();
        
        return result;
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public long countTotalClicksForActivity(long campaignActivityId) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<CampaignActivityOutboundLink> fromLink = query.from(CampaignActivityOutboundLink.class);
        Root<CampaignLinkClick> fromClicks = query.from(CampaignLinkClick.class);
        
        query.select(builder.countDistinct(fromClicks));
        query.where(builder.and(
                builder.equal(fromLink.get(CampaignActivityOutboundLink_.OWNER), campaignActivityId),
                builder.equal(fromLink.get(CampaignActivityOutboundLink_.LINK_KEY), fromClicks.get(CampaignLinkClick_.LINK_KEY))
        ));
        
        long result = objService.getEm().createQuery(query)
                .getSingleResult();
        
        return result;
    }

    /**
     * Opportunity for an EDS copy method for EnterpriseObjects.
     * 
     * @param objectid
     * @param activityName
     * @param activityGoals
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CampaignActivity copyCampaign(long objectid, String activityName, String activityGoals) throws InstantiationException, IllegalAccessException, EntityNotFoundException, NoSuchFieldException, NoSuchMethodException {
        CampaignActivity copy = updService.copyObjectDataAndRelationship(objectid, CampaignActivity.class);
        copy.setACTIVITY_NAME(activityName);
        copy.setACTIVITY_GOALS(activityGoals);
        copy.setSTATUS(ACTIVITY_STATUS.NEW.name);
        
        //updService.persist(copy);
        
        return copy;
    }

    public List<String> getSentEmails(long campaignActivityId, List<String> sent) {
        if(sent == null || sent.isEmpty())
            return new ArrayList<>();
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<String> query = builder.createQuery(String.class);
        Root<Trigger_Email_Activity> fromActivity = query.from(Trigger_Email_Activity.class);
        
        //query.distinct(true);//We don't need distinct here as there is no result limit and we want to eliminate case sensitivity for duplicate emails.
        query.select(fromActivity.get(Trigger_Email_Activity_.SUBCRIBER_EMAIL));
        query.where(builder.and(
                builder.equal(fromActivity.get(Trigger_Email_Activity_.TRIGGERING_OBJECT),campaignActivityId),
                fromActivity.get(Trigger_Email_Activity_.SUBCRIBER_EMAIL).in(sent)
        ));
        query.orderBy(builder.asc(fromActivity.get(Trigger_Email_Activity_.SUBCRIBER_EMAIL)));
        
        List<String> results = objService.getEm().createQuery(query)
                .getResultList();
        
        return results;
    }
}
