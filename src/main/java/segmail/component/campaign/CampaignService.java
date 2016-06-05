/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.campaign;

import eds.component.GenericObjectService;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import eds.entity.client.Client;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import seca2.bootstrap.module.Client.ClientContainer;
import segmail.entity.campaign.ACTIVITY_STATUS;
import segmail.entity.campaign.ACTIVITY_TYPE;
import segmail.entity.campaign.Assign_Campaign_Activity;
import segmail.entity.campaign.Assign_Campaign_Client;
import segmail.entity.campaign.Campaign;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.campaign.CampaignActivityContent;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class CampaignService {
    
    @Inject ClientContainer clientContainer;
    
    @EJB GenericObjectService objService;
    
    
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
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public CampaignActivity createCampaignActivity(long campaignId, String name, String goals, ACTIVITY_TYPE type) throws IncompleteDataException, EntityNotFoundException {
        if(name == null || name.isEmpty())
            throw new IncompleteDataException("Campaign activities must have a name.");
        
        CampaignActivity newActivity = new CampaignActivity();
        newActivity.setACTIVITY_NAME(name);
        newActivity.setACTIVITY_GOALS(goals);
        newActivity.setACTIVITY_TYPE(type.name());
        newActivity.setSTATUS(ACTIVITY_STATUS.NEW.name);
        
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
    
}
