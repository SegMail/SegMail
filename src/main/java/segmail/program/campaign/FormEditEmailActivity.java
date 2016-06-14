/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.FormEditEntity;
import segmail.component.campaign.CampaignService;
import segmail.entity.campaign.ACTIVITY_STATUS;
import static segmail.entity.campaign.ACTIVITY_STATUS.NEW;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.campaign.CampaignActivitySchedule;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormEditEmailActivity")
public class FormEditEmailActivity implements FormEditEntity {

    @Inject
    ProgramCampaign program;

    @EJB
    CampaignService campaignService;
    
    public CampaignActivitySchedule getEditingSchedule() {
        return program.getEditingSchedule();
    }

    public void setEditingSchedule(CampaignActivitySchedule editingSchedule) {
        program.setEditingSchedule(editingSchedule);
    }
    
    public CampaignActivity getEditingActivity() {
        return program.getEditingActivity();
    }

    public void setEditingActivity(CampaignActivity editingActivity) {
        program.setEditingActivity(editingActivity);
    }

    @Override
    public void saveAndContinue() {
        try {
            
            campaignService.updateCampaignActivity(getEditingActivity());
            campaignService.updateCampaignActivitySchedule(this.getEditingSchedule());
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Email saved", "");
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } 
    }

    @Override
    public void saveAndClose() {
        saveAndContinue();
        closeWithoutSaving();
    }

    @Override
    public void closeWithoutSaving() {
        program.refresh();
    }

    @Override
    public void delete() {
        try {
            campaignService.deleteCampaignActivity(getEditingActivity().getOBJECTID());
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Campaign activity deleted.", "");
            closeWithoutSaving();
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }

    public boolean canEdit() {
        if(getEditingActivity() == null)
            return false;
        switch (ACTIVITY_STATUS.valueOf(getEditingActivity().getSTATUS())) {
            case NEW:
                return true;
            case STARTED:
                return false;
            case COMPLETED:
                return false;
            case STOPPED:
                return false;
            default:
                return false;
        }
    }
    
    public void send() {
        
    }

}
