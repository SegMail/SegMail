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
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.User.UserContainer;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author LeeKiatHaw
 */
@SessionScoped
@Named("MySettingsProgram")
public class MySettingsProgram implements Serializable {
    
    private final String pageName = "my_settings_program";
    
    @Inject private UserContainer userContainer;
    
    @EJB private ClientService clientService;
    
    private ContactInfo contactInfo;
    
    @PostConstruct
    public void init(){
        try {
            this.initContactInfo();
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(getPageName(), FacesMessage.SEVERITY_ERROR, "Could not connect to DB!", "Please contact administrators.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(getPageName(), FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void initContactInfo() throws DBConnectionException, Exception{
        if(!userContainer.isLoggedIn() || userContainer.getUser() == null){
            //This will most likely not happen in production, hence we don't really have to handle it
            throw new RuntimeException("You are not logged in and you cannot execute any functionalities on this page.");
        }
        
        if(this.contactInfo != null)
            return;
        //Retrieve the contact info for this particular user
        setContactInfo(clientService.getContactInfoForObject(userContainer.getUser().getOBJECTID()));
        
        //You don't want a nullpointerexception on your page!
        //This is the temporary solution, may or may not be the best.
        if(getContactInfo() == null){
            ContactInfo newContactInfo = new ContactInfo();
            
            //Get the user's clientid
            Client thisClient = clientService.getClientByAssignedObjectId(userContainer.getUser().getOBJECTID());
            newContactInfo.setOWNER(thisClient); //May be null at this point of time
            setContactInfo(newContactInfo);
        }
            
    }

    public String getPageName() {
        return pageName;
    }


    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }
    
    
}
