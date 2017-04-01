/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
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
import segmail.entity.campaign.ACTIVITY_STATUS;
import segmail.entity.campaign.CampaignActivity;

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
        
        Collections.sort(allActivities, new Comparator<CampaignActivity>(){

            @Override
            public int compare(CampaignActivity o1, CampaignActivity o2) {
                if (o1.getDATE_CHANGED()== null) {
                    return 1;
                }

                if (o2.getDATE_CHANGED() == null) {
                    return -1;
                }
                
                if (o1.getDATE_CREATED().equals(o2.getDATE_CREATED())) {
                    ACTIVITY_STATUS s1 = ACTIVITY_STATUS.valueOf(o1.getSTATUS());
                    ACTIVITY_STATUS s2 = ACTIVITY_STATUS.valueOf(o2.getSTATUS());
                    
                    return s1.compareTo(s2);
                }
                    
                
                return o2.getDATE_CREATED().compareTo(o1.getDATE_CREATED());
            }
            
        });
        
        setAllActivities(allActivities);
        
        loadClickthroughRates();
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
            double totalClicks = campaignService.countConvertedEmails(activity.getOBJECTID());
            double totalSent = campaignService.countEmailsSentForActivity(activity.getOBJECTID());
            double clickthrough =  0.0;
            
            if(totalSent > 0) {
                clickthrough = (totalClicks/totalSent) * 100.0;
            }
            BigDecimal rounded = new BigDecimal(clickthrough);
            clickthrough = rounded.setScale(3,RoundingMode.HALF_UP).doubleValue();
            
            getClickthroughRates().put(activity.getOBJECTID(), clickthrough);
        }
    }
}
