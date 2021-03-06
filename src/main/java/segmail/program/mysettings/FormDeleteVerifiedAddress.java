/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.mysettings;

import eds.component.client.AWSException;
import eds.component.client.ClientAWSService;
import eds.component.client.ClientService;
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
@Named("FormDeleteVerifiedAddress")
public class FormDeleteVerifiedAddress {
    
    @Inject MySettingsProgram program;
    @Inject ClientContainer clientCont;
    
    @EJB ClientService clientService;
    @EJB ClientAWSService clientAWSService;
    
    public String getDeleteAddress() {
        return program.getDeleteAddress();
    }

    public void setDeleteAddress(String deleteAddress) {
        program.setDeleteAddress(deleteAddress);
    }
    
    public void delete() {
        try{
            clientAWSService.deleteVerifiedAddressAndSESIdentity(clientCont.getClient(), this.getDeleteAddress());
            FacesMessenger.setFacesMessage(MySettingsProgram.class.getSimpleName(), FacesMessage.SEVERITY_FATAL, "Your sending address has been removed.", "");
            program.refresh();
        } catch(AWSException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getCause().getMessage(), "");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
        
    }
}
