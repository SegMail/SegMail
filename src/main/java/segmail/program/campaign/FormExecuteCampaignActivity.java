/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import eds.component.batch.BatchProcessingException;
import eds.component.data.DataValidationException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.campaign.CampaignExecutionService;
import segmail.component.campaign.CampaignService;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormExecuteCampaignActivity")
public class FormExecuteCampaignActivity {
    @Inject UserRequestContainer reqCont;
    @Inject ProgramCampaign program;
    
    @EJB CampaignService campService;
    @EJB CampaignExecutionService campExecService;
    
    public boolean renderThis() {
        return reqCont.getPathParser().getOrderedParams().size() == 1;
    }
    
    public void executeAndClose() {
        try {
            campService.startSendingCampaignEmail(program.getEditingActivity());
            //campExecService.executeCampaignActivity(program.getEditingActivity().getOBJECTID(), 10);
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Campaign activity "+program.getEditingActivity().getACTIVITY_NAME()+" has started.", "");
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
