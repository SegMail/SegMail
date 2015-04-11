/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.mysettings;

import eds.component.client.ClientService;
import eds.component.data.DBConnectionException;
import eds.entity.client.Client;
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
    
    private ContactInfo contactInfo;
    
    @PostConstruct
    public void init(){
        try {
            this.initContactInfo();
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.mySettingsProgram.getPageName(), FacesMessage.SEVERITY_ERROR, "Could not connect to DB!", "Please contact administrators.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.mySettingsProgram.getPageName(), FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void initContactInfo() throws DBConnectionException, Exception{
        if(!userContainer.isLoggedIn() || userContainer.getUser() == null){
            //This will most likely not happen in production, hence we don't really have to handle it
            throw new Exception("You are not logged in and you cannot execute any functionalities on this page.");
        }
        
        this.contactInfo = clientService.getContactInfoForObject(userContainer.getUser().getOBJECTID());
        
        //You don't want a nullpointerexception on your page!
        //This is the temporary solution, may or may not be the best.
        if(contactInfo == null){
            contactInfo = new ContactInfo();
            
            //Get the user's clientid
            Client thisClient = clientService.getClientByAssignedObjectId(userContainer.getUser().getOBJECTID());
            contactInfo.setOWNER(thisClient);
        }
            
    }
    
    public void update(){
        try {
            this.clientService.updateClientContact(contactInfo);
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, "Could not connect to DB!", "Please contact administrators.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    
    
    
}
