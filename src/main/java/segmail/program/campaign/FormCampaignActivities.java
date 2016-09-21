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
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()){
            this.loadAllActivities();
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
}
