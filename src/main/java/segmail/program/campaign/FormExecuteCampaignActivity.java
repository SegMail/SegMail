/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import eds.component.GenericObjectService;
import eds.component.batch.BatchProcessingException;
import eds.component.data.DataValidationException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.campaign.CampaignService;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.subscription.SubscriptionList;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormExecuteCampaignActivity")
public class FormExecuteCampaignActivity {
    @Inject UserRequestContainer reqCont;
    @Inject ProgramCampaign program;
    
    @EJB GenericObjectService objService;
    @EJB CampaignService campService;
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()) {
            
        }
    }
    
    public CampaignActivity getEditingActivity() {
        return program.getEditingActivity();
    }

    public void setEditingActivity(CampaignActivity editingActivity) {
        program.setEditingActivity(editingActivity);
    }
    
    public List<SubscriptionList> getActivityTargetLists() {
        return program.getActivityTargetLists();
    }

    public void setActivityTargetLists(List<SubscriptionList> acitvityTargetLists) {
        program.setActivityTargetLists(acitvityTargetLists);
    }
    
    public boolean renderThis() {
        return reqCont.getPathParser().getOrderedParams().size() == 1;
    }
    
    public void executeAndClose() {
        try {
            campService.startSendingCampaignEmail(program.getEditingActivity());
            FacesMessenger.setFacesMessage(ProgramCampaign.class.getSimpleName(), FacesMessage.SEVERITY_FATAL, "Campaign activity "+program.getEditingActivity().getACTIVITY_NAME()+" has started.", "");
            program.refresh();
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (BatchProcessingException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (IOException ex) {
            Logger.getLogger(FormExecuteCampaignActivity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DataValidationException ex) {
            Logger.getLogger(FormExecuteCampaignActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
    
    
}
