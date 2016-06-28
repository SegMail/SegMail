/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import eds.component.batch.BatchProcessingException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
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
    
    @Inject ProgramCampaign program;
    
    @EJB CampaignService campService;
    @EJB CampaignExecutionService campExecService;
    
    public void executeAndClose() {
        try {
            //campService.startSendingCampaignEmail(program.getEditingActivity());
            campExecService.executeCampaignActivity(program.getEditingActivity().getOBJECTID(), 0, 10);
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Campaign activity "+program.getEditingActivity().getACTIVITY_NAME()+" has started.", "");
            program.refresh();
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }/* catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (BatchProcessingException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }*/ catch (RelationshipNotFoundException ex) {
            Logger.getLogger(FormExecuteCampaignActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
