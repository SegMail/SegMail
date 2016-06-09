/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import eds.component.data.IncompleteDataException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.FormEditEntity;
import segmail.component.campaign.CampaignService;
import segmail.entity.campaign.CampaignActivity;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormEditEmailActivity")
public class FormEditEmailActivity implements FormEditEntity {
    
    @Inject ProgramCampaign program;
    
    @EJB CampaignService campaignService;

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
