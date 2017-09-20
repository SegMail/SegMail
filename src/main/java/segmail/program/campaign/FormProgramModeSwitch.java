/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import java.util.List;
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
import segmail.entity.campaign.CampaignActivity;

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
    
    /**
     * Small trick to init this bean a little earlier than the rest
     */
    private String activate = "";

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
            reloadEntities();
            //initEditCampaignMode();
            modifySessionContainer();
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
    
    public CampaignActivity getEditingActivity() {
        return program.getEditingActivity();
    }

    public void setEditingActivity(CampaignActivity editingActivity) {
        program.setEditingActivity(editingActivity);
    }

    public String getActivate() {
        return activate;
    }

    public void setActivate(String activate) {
        this.activate = activate;
    }
    
    public void reloadEntities() {

        List<String> params = reqCont.getProgramParamsOrdered();
        
        if(params.size() >= 1) {
            long newCampaignId = Long.parseLong(params.get(0));
            if (getEditingCampaign() == null 
                    || getEditingCampaign().getOBJECTID() != newCampaignId) { 

                Campaign editingCampaign = campaignService.getCampaign(newCampaignId);
                setEditingCampaign(editingCampaign);
            }
        }
        
        if(params.size() >= 2) {
            long newActivityId = Long.parseLong(params.get(1));
            if (getEditingActivity() == null
                    || getEditingActivity().getOBJECTID() != newActivityId) { 

                CampaignActivity editingActivy = campaignService.getCampaignActivity(newActivityId);
                setEditingActivity(editingActivy);
            }
        }
    }

    /**
     * Gets the "level" of the program based on the request URL in UserRequestContainer.
     * Eg. "/campaign" is 0, "/campaign/32/112" is 2.
     * 
     * @return 
     */
    public int programLevel() {
        return reqCont.getProgramParamsOrdered().size();
    }
    
    public void modifySessionContainer() {
        List<String> params = reqCont.getProgramParamsOrdered();
        if(params.size() >= 2) {
            reqCont.setRenderPageToolbar(false);
            reqCont.setRenderPageBreadCrumbs(false);
        }
        
        if(params.size() >= 1) {
            sessCont.overwriteProgramTitle(this.getEditingCampaign().getCAMPAIGN_NAME());
            sessCont.overwriteProgramDescription(this.getEditingCampaign().getCAMPAIGN_GOALS());
        }
        
        if(params.size() <= 0) {
            reqCont.setRenderPageToolbar(true);
            reqCont.setRenderPageBreadCrumbs(true);
            sessCont.revertProgramOverwrite();
        }
        
        
    }

    
}
