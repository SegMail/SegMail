/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import seca2.program.Program;
import segmail.component.campaign.CampaignService;
import segmail.entity.campaign.Campaign;

/**
 *
 * @author Administrator
 */
@Named("ProgramCampaign")
public class ProgramCampaign extends Program{
    
    @EJB CampaignService campaignService;

    private boolean editCampaignMode = false; //Determines whether to show the list of campaign or an individual campaign.
    private long editingCampaignId;
    private Campaign editingCampaign;
    
    private List<Campaign> allCampaigns;
    
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
    
    
}
