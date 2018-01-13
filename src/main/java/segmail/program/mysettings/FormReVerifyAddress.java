/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.mysettings;

import eds.component.client.ClientAWSService;
import eds.component.client.ClientService;
import eds.entity.client.VerifiedSendingAddress;
import java.util.List;
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
@Named("FormReVerifyAddress")
public class FormReVerifyAddress {
    
    @Inject MySettingsProgram program;
    
    @EJB ClientAWSService clientAWSService;
    
    @Inject ClientContainer clientCont;
    
    
    public String getExistingAddress() {
        return program.getExistingAddress();
    }

    public void setExistingAddress(String sno) {
        program.setExistingAddress(sno);
    }
    
    public void reverify() {
        List<VerifiedSendingAddress> address = clientAWSService.getVerifiedAddress(clientCont.getClient().getOBJECTID(), this.getExistingAddress());
        if(address == null)
            throw new RuntimeException("Address " + getExistingAddress() + " does not exist!");
        
        clientAWSService.reverifyAddress(address.get(0), true);
        
        FacesMessenger.setFacesMessage("ContactDetailsForm", FacesMessage.SEVERITY_FATAL, "Your verification email has been re-sent. Please check your email in a while.", "");
        
        program.refresh();
    }
}
