/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.mysettings;

import eds.component.client.ClientService;
import eds.component.data.DBConnectionException;
import eds.entity.client.Client;
import eds.entity.client.ClientAccessAssignment;
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
        try {
            this.initContactInfo();
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, "Could not connect to DB!", "Please contact administrators.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void update(){
        try {
            //Check if the user has a client object created and create one if not yet exist
            if(this.mySettingsProgram.getContactInfo().getOWNER() == null){
                //Check which client type is "Person", but we are assuming it's called "Person" and not by other similar names
                ClientType personClientType = clientService.getClientTypeByName("Person");
                ClientAccessAssignment newClientAssignment = 
                        clientService.registerClientForUser(userContainer.getUser(), personClientType.getOBJECTID());
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
    
    public void initContactInfo() throws DBConnectionException, Exception{
        if(!userContainer.isLoggedIn() || userContainer.getUser() == null){
            //This will most likely not happen in production, hence we don't really have to handle it
            throw new RuntimeException("You are not logged in and you cannot execute any functionalities on this page.");
        }
        
        //Retrieve the contact info for this particular user
        this.setContactInfo(clientService.getContactInfoForUser(userContainer.getUser().getOBJECTID()));
        
        //You don't want a nullpointerexception on your page!
        //This is the temporary solution, may or may not be the best.
        if(getContactInfo() == null){
            ContactInfo newContactInfo = new ContactInfo();
            
            //Get the user's clientid
            Client thisClient = clientService.getClientByAssignedUser(userContainer.getUser().getOBJECTID());
            newContactInfo.setOWNER(thisClient); //May be null at this point of time
            setContactInfo(newContactInfo);
        }
            
    }

    private void setContactInfo(ContactInfo contactInfoForObject) {
        this.mySettingsProgram.setContactInfo(contactInfoForObject);
    }
}
