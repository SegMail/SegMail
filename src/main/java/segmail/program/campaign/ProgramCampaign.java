/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

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
import segmail.entity.campaign.CampaignActivitySchedule;
import segmail.entity.subscription.SubscriptionList;

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
    private CampaignActivitySchedule editingSchedule;
    
    private List<SubscriptionList> ownedLists;
    private List<SubscriptionList> targetLists;
    private List<String> selectedLists; //JSF selectMany component will store it as String.
    
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
        this.setEditingCampaignId(editingActivity.getOBJECTID());
        this.editingActivity = editingActivity;
    }

    public CampaignActivitySchedule getEditingSchedule() {
        return editingSchedule;
    }

    public void setEditingSchedule(CampaignActivitySchedule editingSchedule) {
        this.editingSchedule = editingSchedule;
    }

    public List<SubscriptionList> getOwnedLists() {
        return ownedLists;
    }

    public void setOwnedLists(List<SubscriptionList> ownedLists) {
        this.ownedLists = ownedLists;
    }

    public List<String> getSelectedLists() {
        return selectedLists;
    }

    public void setSelectedLists(List<String> selectedLists) {
        this.selectedLists = selectedLists;
    }

    public List<SubscriptionList> getTargetLists() {
        return targetLists;
    }

    public void setTargetLists(List<SubscriptionList> targetLists) {
        this.targetLists = targetLists;
    }
    
    
    
}
