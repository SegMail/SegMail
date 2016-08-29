/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.UserSessionContainer;
import segmail.component.campaign.CampaignService;
import segmail.entity.campaign.Campaign;

/**
 * Impt! If this request scoped bean loads the editing Campaign, then it must be
 * high up in the view ID, earlier than all the other beans that will access the
 * editing Campaign.
 *
 * Alternatively, the editing campaign can be instantiated by a BootstrapModule.
 *
 * @author Administrator
 */
@RequestScoped
@Named("FormProgramModeSwitch")
public class FormProgramModeSwitch {

    @EJB
    CampaignService campaignService;

    @Inject
    UserRequestContainer reqCont;
    @Inject
    UserSessionContainer sessCont;

    @Inject
    ProgramCampaign program;

    @PostConstruct
    public void init() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            reloadCampaign();
            initEditCampaignMode();
            program.modifySessionContainer();
        }
    }

    public boolean isEditCampaignMode() {
        return program.isEditCampaignMode();
    }

    public void setEditCampaignMode(boolean editCampaignMode) {
        program.setEditCampaignMode(editCampaignMode);
    }

    public Campaign getEditingCampaign() {
        return program.getEditingCampaign();
    }

    public void setEditingCampaign(Campaign editingCampaign) {
        program.setEditingCampaign(editingCampaign);
    }

    public long getEditingCampaignId() {
        return program.getEditingCampaignId();
    }

    public void setEditingCampaignId(long editingCampaignId) {
        program.setEditingCampaignId(editingCampaignId);
    }

    public void reloadCampaign() {

        List<String> params = reqCont.getProgramParamsOrdered();
        if (params == null || params.isEmpty()) {
            setEditingCampaignId(-1);
            setEditingCampaign(null);
            return;
        }
        if (params != null && !params.isEmpty()) {
            long newId = Long.parseLong(params.get(0));
            if (getEditingCampaignId() == newId) { //this is screwing up with the
                return;
            }
            setEditingCampaignId(newId);
        }

        Campaign editingCampaign = campaignService.getCampaign(getEditingCampaignId());
        setEditingCampaign(editingCampaign);
    }

    public void initEditCampaignMode() {
        //Initialize the campaign ID, if exist and decide whether to load the campaign or not.
        if (this.getEditingCampaignId() > 0) {
            this.setEditCampaignMode(true);
            return;
        }
        this.setEditCampaignMode(false);
    }

    
}
