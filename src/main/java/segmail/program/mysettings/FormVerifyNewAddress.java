/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.mysettings;

import eds.component.client.ClientAWSService;
import eds.component.client.ClientService;
import eds.component.data.DataValidationException;
import eds.entity.client.VerifiedSendingAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormVerifyNewAddress")
public class FormVerifyNewAddress {
    
    @Inject MySettingsProgram program;
    
    @EJB ClientService clientService;
    @EJB ClientAWSService clientAWSService;
    
    @Inject ClientContainer clientCont;
    
    private String newEmailAddress;

    public String getNewEmailAddress() {
        return newEmailAddress;
    }

    public void setNewEmailAddress(String newEmailAddress) {
        this.newEmailAddress = newEmailAddress;
    }
    
    public void verifyNewEmailAddress() {
        try {
            VerifiedSendingAddress newAddress = clientAWSService.verifyNewSendingAddress(clientCont.getClient(), newEmailAddress);
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "A confirmation email from Amazon Web Services will be sent to your address shortly. Please click on the confirmation link in that email to activate your sending address.", "");
            
            program.refresh();
        } catch (DataValidationException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }
    
    
}
