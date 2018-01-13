/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.wizard;

import eds.component.GenericObjectService;
import eds.component.client.ClientAWSService;
import eds.component.data.DataValidationException;
import eds.component.data.EntityExistsException;
import eds.entity.client.Client;
import eds.entity.client.VerifiedSendingAddress;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormWizardAddress")
public class FormWizardAddress {
    @Inject ProgramSetupWizard program;
    @Inject FormWizardInit formWizard;
    
    @Inject ClientContainer clientCont;
    
    @EJB GenericObjectService objService;
    @EJB ClientAWSService clientAWSService;
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()) {
            loadExistingAddresses();
        }
    }
    
    public List<VerifiedSendingAddress> getExistingAddresses() {
        return program.getExistingAddresses();
    }

    public void setExistingAddresses(List<VerifiedSendingAddress> existingAddresses) {
        program.setExistingAddresses(existingAddresses);
    }
    
    public String getAddress() {
        return program.getAddress();
    }

    public void setAddress(String address) {
        program.setAddress(address);
    }
    
    public VerifiedSendingAddress getSelectedAddress() {
        return program.getSelectedAddress();
    }

    public void setSelectedAddress(VerifiedSendingAddress selectedAddress) {
        program.setSelectedAddress(selectedAddress);
    }
    
    public void loadExistingAddresses() {
        Client client = clientCont.getClient();
        List<VerifiedSendingAddress> addresses = objService.getEnterpriseData(client.getOBJECTID(), VerifiedSendingAddress.class);
        
        program.setExistingAddresses(addresses);
    }
    
    public void verify() {
        try {
            setAddress(getAddress().trim());
            String email = getAddress();
            if(getExistingAddresses().stream().noneMatch(
                    add -> add.getVERIFIED_ADDRESS().equalsIgnoreCase(email)
            )){
                VerifiedSendingAddress newAddress = clientAWSService.verifyNewSendingAddress(clientCont.getClient(), email, true);
                getExistingAddresses().add(newAddress);
                
            }
            getExistingAddresses().forEach(add -> {
                if(email.equalsIgnoreCase(add.getVERIFIED_ADDRESS())){
                    setSelectedAddress(add);
                }
            });
            formWizard.nextPage();
            
        } catch (DataValidationException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            FacesMessenger.setFacesMessage(ProgramSetupWizard.class.getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (EntityExistsException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            FacesMessenger.setFacesMessage(ProgramSetupWizard.class.getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (EJBException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            FacesMessenger.setFacesMessage(ProgramSetupWizard.class.getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getCause().getMessage(), "");
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            FacesMessenger.setFacesMessage(ProgramSetupWizard.class.getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
        
    }
}
