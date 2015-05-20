/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.mysettings;

import eds.component.client.ClientService;
import eds.component.data.DBConnectionException;
import eds.entity.client.Client;
import eds.entity.client.ClientAssignment;
import eds.entity.client.ClientType;
import eds.entity.client.ContactInfo;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.User.UserContainer;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author LeeKiatHaw
 */
@Named("ContactDetailsForm")
@RequestScoped
public class ContactDetailsForm {
    
    @EJB private ClientService clientService;
    
    @Inject private UserContainer userContainer;
    @Inject private MySettingsProgram mySettingsProgram;
    
    private final String formName = "contact_details_form";
    
    @PostConstruct
    public void init(){
        System.out.println("Contact form initiated!");
    }
    
    public void update(){
        try {
            //Check if the user has a client object created and create one if not yet exist
            if(this.mySettingsProgram.getContactInfo().getOWNER() == null){
                //Check which client type is "Person", but we are assuming it's called "Person" and not by other similar names
                ClientType personClientType = clientService.getClientTypeByName("Person");
                ClientAssignment newClientAssignment = 
                        clientService.registerClientForObject(userContainer.getUser(), personClientType.getOBJECTID());
                Client newclient = newClientAssignment.getSOURCE();
                this.mySettingsProgram.getContactInfo().setOWNER(newclient);
            }
            this.clientService.updateClientContact(mySettingsProgram.getContactInfo());
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_FATAL, "Your contact details has been updated!", null);
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, "Could not connect to DB!", "Please contact administrators.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public ContactInfo getContactInfo(){
        return this.mySettingsProgram.getContactInfo();
    }
    
}
