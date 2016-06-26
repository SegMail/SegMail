/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.campaign;

import eds.component.GenericObjectService;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import segmail.entity.campaign.Assign_Campaign_Activity;
import segmail.entity.campaign.Campaign;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.campaign.CampaignActivityExecutionSchedule;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class CampaignExecutionService {
    
    public final int BATCH_SIZE = 100;
    
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
     * Executes the campaign activity from the [start]th subscriber to [end]th subscriber.
     * 
     * @param campaignActivityId
     * @param size 
     */
    public void executeCampaignActivity(long campaignActivityId, int size) {
        
        //Retrieve subscribers by 
        //
    }
    
    
}
