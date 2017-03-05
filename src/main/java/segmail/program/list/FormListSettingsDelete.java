/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import eds.component.data.EntityNotFoundException;
import eds.component.user.UserAccountLockedException;
import eds.component.user.UserLoginException;
import eds.component.user.UserService;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.UserSessionContainer;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.ListService;
import segmail.component.subscription.SubscriptionService;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormListSettingsDelete")
public class FormListSettingsDelete {
    
    @Inject private ProgramList program;
    @Inject private UserSessionContainer userContainer;
    @Inject private UserRequestContainer reqContainer;
    @Inject private ClientContainer clientContainer;
    
    @EJB private SubscriptionService subService;
    @EJB private UserService userService;
    @EJB private ListService listService;
    
    private String passwordForDeletion;
    
    public void deleteList(){
        try {
            //This is potentially a scheduled background processing, but the 
            //background scheduling capability is not up yet, so we will just 
            //try to delete it at the frontend.
            
            //Check if password provided is correct
            userService.login(userContainer.getUsername(), passwordForDeletion);
            
            listService.deleteList(program.getListEditing().getOBJECTID(),clientContainer.getClient().getOBJECTID());
            
            //Display success message
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, 
                    "List "+program.getListEditing().getLIST_NAME()+" has been deleted!", null);
            //Remove the list from editing panel once it has been deleted
            program.setListEditing(null);
            
            //Reload the entire page to refresh all other components in the page
            reqContainer.getProgramParamsOrdered().clear(); //So that it's not refreshed with the deleted list ID
            program.refresh();
            
        } catch (UserAccountLockedException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR,  ex.getMessage(), null);
        } catch (UserLoginException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR,  ex.getMessage(), null);
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR,  ex.getMessage(), null);
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR,  ex.getMessage(), null);
        }
    }

    public String getPasswordForDeletion() {
        return passwordForDeletion;
    }

    public void setPasswordForDeletion(String passwordForDeletion) {
        this.passwordForDeletion = passwordForDeletion;
    }
}
