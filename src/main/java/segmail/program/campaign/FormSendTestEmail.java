/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import eds.component.data.DataValidationException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipNotFoundException;
import eds.component.mail.InvalidEmailException;
import eds.component.mail.MailServiceOutbound;
import eds.entity.client.VerifiedSendingAddress;
import eds.entity.mail.Email;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import org.joda.time.DateTime;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.campaign.CampaignExecutionService;
import segmail.component.subscription.mailmerge.MailMergeService;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.campaign.Trigger_Email_Activity;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormSendTestEmail")
public class FormSendTestEmail {
    
    @Inject ProgramCampaign program;
    @Inject ClientContainer clientCont;
    
    @EJB CampaignExecutionService exeService;
    
    public CampaignActivity getEditingActivity() {
        return program.getEditingActivity();
    }

    public void setEditingActivity(CampaignActivity editingActivity) {
        program.setEditingActivity(editingActivity);
    }
    
    public List<VerifiedSendingAddress> getVerifiedAddresses() {
        return program.getVerifiedAddresses();
    }

    public void setVerifiedAddresses(List<VerifiedSendingAddress> verifiedAddresses) {
        program.setVerifiedAddresses(verifiedAddresses);
    }
    
    public List<String> getSelectedPreviewAddress() {
        return program.getSelectedPreviewAddress();
    }

    public void setSelectedPreviewAddress(List<String> selectedPreviewAddress) {
        program.setSelectedPreviewAddress(selectedPreviewAddress);
    }
    
    public void sendPreview() {
        try {
            exeService.sendPreview(this.getEditingActivity(), this.getSelectedPreviewAddress(), clientCont.getClient().getOBJECTID());
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Preview sent!", "");
            program.refresh();
        } catch (DataValidationException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (InvalidEmailException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (RelationshipNotFoundException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
        
    }
}
