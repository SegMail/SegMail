/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.mysettings;

import eds.component.client.ClientService;
import eds.component.data.DataValidationException;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import eds.component.user.UserService;
import eds.entity.client.Client;
import eds.entity.client.ClientUserAssignment;
import eds.entity.client.ClientType;
import eds.entity.client.ContactInfo;
import eds.entity.user.UserAccount;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserSessionContainer;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author LeeKiatHaw
 */
@Named("ContactDetailsForm")
@RequestScoped
public class ContactDetailsForm {
    
    @EJB ClientService clientService;
    @EJB UserService userService;
    
    @Inject private UserSessionContainer userContainer;
    @Inject private MySettingsProgram program;
    
    private final String formName = "contact_details_form";
    
    @PostConstruct
    public void init(){
        if(!FacesContext.getCurrentInstance().isPostback()) {
            initContactInfo();
            initUserEmail();
        }
        
    }
    
    public void update(){
        try {
            //Check if the user has a client object created and create one if not yet exist
            if(this.program.getContactInfo().getOWNER() == null){
                //Check which client type is "Person", but we are assuming it's called "Person" and not by other similar names
                ClientType personClientType = clientService.getClientTypeByName("Person");
                ClientUserAssignment newClientAssignment = 
                        clientService.registerClientForUser(userContainer.getUser(), personClientType.getOBJECTID());
                Client newclient = newClientAssignment.getSOURCE();
                program.getContactInfo().setOWNER(newclient);
            }
            clientService.updateClientContact(program.getContactInfo());
            initContactInfo();
            initUserEmail();
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Your contact details has been updated!", null);
        }  catch (DataValidationException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        } catch (EntityExistsException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        } catch (RelationshipExistsException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public ContactInfo getContactInfo(){
        return this.program.getContactInfo();
    }
    
    public void initContactInfo() {
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
    
    public void initUserEmail() {
        UserAccount account = userService.getUserAccountById(userContainer.getUser().getOBJECTID());
        program.setUserEmail(account.getCONTACT_EMAIL());
    }

    private void setContactInfo(ContactInfo contactInfoForObject) {
        this.program.setContactInfo(contactInfoForObject);
    }
    
    public String getUserEmail() {
        return program.getUserEmail();
    }

    public void setUserEmail(String userEmail) {
        program.setUserEmail(userEmail);
    }
}
