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
    
    @Override
    public void clearVariables() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void initProgramParams() {
        initEditCampaignMode(); //On first load
        loadCampaign();
    }

    @Override
    public void initProgram() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    
    public void initEditCampaignMode() {
        //Initialize the campaign ID, if exist and decide whether to load the campaign or not.
        
        if(this.getEditingCampaignId() > 0) {
            this.setEditCampaignMode(true);
            return;
        }
        
        this.setEditCampaignMode(false);
    }
    
    public void loadCampaign() {
        List<String> params = reqContainer.getProgramParamsOrdered();
        if(params != null && !params.isEmpty())
            this.setEditingCampaignId(Long.parseLong(params.get(0)));
        Campaign editingCampaign = campaignService.getCampaign(editingCampaignId);
        setEditingCampaign(editingCampaign);
        
    }
    
}
