/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import segmail.component.campaign.CampaignExecutionService;
import segmail.component.campaign.CampaignService;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.campaign.CampaignActivitySchedule;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormCampaignActivities")
public class FormCampaignActivities {
    
    @Inject ProgramCampaign program;
    
    @EJB CampaignService campaignService;
    @EJB CampaignExecutionService campExeService;
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()){
            loadAllActivities();
            loadClickthroughRates();
        }
    }
    
    public List<CampaignActivity> getAllActivities() {
        return program.getAllActivities();
    }

    public void setAllActivities(List<CampaignActivity> allActivities) {
        program.setAllActivities(allActivities);
    }
    
    public Map<String, String> getActivityStatusMapping() {
        return program.getActivityStatusMapping();
    }

    public void setActivityStatusMapping(Map<String, String> activityStatusMapping) {
        program.setActivityStatusMapping(activityStatusMapping);
    }
    
    public Map<Long, Double> getClickthroughRates() {
        return program.getClickthroughRates();
    }

    public void setClickthroughRates(Map<Long, Double> clickthroughRates) {
        program.setClickthroughRates(clickthroughRates);
    }
    
    public void loadAllActivities() {
        if(program.getEditingCampaign() == null)
            return;
        
        List<CampaignActivity> allActivities = campaignService.getAllActivitiesForCampaign(program.getEditingCampaignId());
        
        setAllActivities(allActivities);
        
    }
    
    public String toReadableActivityStatus(String activityStatus) {
        if(activityStatus == null || activityStatus.isEmpty())
            return "";
        
        if(activityStatus.length() <= 1)
            return activityStatus.toUpperCase();
        
        String readableStatus = activityStatus.substring(0, 1).toUpperCase() + activityStatus.substring(1).toLowerCase();
        
        return readableStatus;
    }
    
    public void loadClickthroughRates() {
        List<CampaignActivity> allActivities = getAllActivities();
        for(CampaignActivity activity : allActivities) {
            long totalClicks = campaignService.getTotalLinkClicksForActivity(activity.getOBJECTID());
            long totalSent = campExeService.countEmailsSentForActivity(activity.getOBJECTID());
            
            double clickthrough = (totalSent <= 0) ? 0.0 : (totalClicks/totalSent) * 100;
            getClickthroughRates().put(activity.getOBJECTID(), clickthrough);
        }
    }
}
