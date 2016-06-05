/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import eds.entity.batch.BATCH_JOB_RUN_STATUS;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import seca2.program.Program;
import segmail.component.campaign.CampaignService;
import segmail.entity.campaign.ACTIVITY_STATUS;
import segmail.entity.campaign.Campaign;
import segmail.entity.campaign.CampaignActivity;

/**
 *
 * @author Administrator
 */
@Named("ProgramCampaign")
@SessionScoped
public class ProgramCampaign extends Program{
    
    @EJB CampaignService campaignService;

    private boolean editCampaignMode = false; //Determines whether to show the list of campaign or an individual campaign.
    private long editingCampaignId;
    private Campaign editingCampaign;
    
    private List<Campaign> allCampaigns;
    
    private List<CampaignActivity> allActivities;
    private Map<String,String> activityStatusMapping;
    
    private CampaignActivity editingActivity;
    
    @Override
    public void clearVariables() {
        
    }

    @Override
    public void initRequestParams() {
        //loadCampaign(); do this in forms
    }

    @Override
    public void initProgram() {
        //initEditCampaignMode(); //On first load do this in Forms
        activityStatusMapping = new HashMap<String,String>();
        activityStatusMapping.put(ACTIVITY_STATUS.NEW.name, "primary");
        activityStatusMapping.put(ACTIVITY_STATUS.STARTED.name, "info");
        activityStatusMapping.put(ACTIVITY_STATUS.COMPLETED.name, "success");
        activityStatusMapping.put(ACTIVITY_STATUS.STOPPED.name, "default");
    }

    
    public boolean isEditCampaignMode() {
        return editCampaignMode;
    }

    public void setEditCampaignMode(boolean editCampaignMode) {
        this.editCampaignMode = editCampaignMode;
    }

    public long getEditingCampaignId() {
        return editingCampaignId;
    }

    public void setEditingCampaignId(long editingCampaignId) {
        this.editingCampaignId = editingCampaignId;
    }

    public Campaign getEditingCampaign() {
        return editingCampaign;
    }

    public void setEditingCampaign(Campaign editingCampaign) {
        this.editingCampaign = editingCampaign;
    }

    public List<Campaign> getAllCampaigns() {
        return allCampaigns;
    }

    public void setAllCampaigns(List<Campaign> allCampaigns) {
        this.allCampaigns = allCampaigns;
    }

    public List<CampaignActivity> getAllActivities() {
        return allActivities;
    }

    public void setAllActivities(List<CampaignActivity> allActivities) {
        this.allActivities = allActivities;
    }

    public Map<String, String> getActivityStatusMapping() {
        return activityStatusMapping;
    }

    public void setActivityStatusMapping(Map<String, String> activityStatusMapping) {
        this.activityStatusMapping = activityStatusMapping;
    }

    public CampaignActivity getEditingActivity() {
        return editingActivity;
    }

    public void setEditingActivity(CampaignActivity editingActivity) {
        this.editingActivity = editingActivity;
    }
    
    
    
}
