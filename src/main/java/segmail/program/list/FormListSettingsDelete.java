/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import eds.component.data.DBConnectionException;
import eds.component.data.EntityNotFoundException;
import eds.component.user.UserAccountLockedException;
import eds.component.user.UserLoginException;
import eds.component.user.UserService;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.User.UserContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.SubscriptionService;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormListSettingsDelete")
public class FormListSettingsDelete {
    
    @Inject private ProgramList program;
    @Inject private UserContainer userContainer;
    
    @EJB private SubscriptionService subService;
    @EJB private UserService userService;
    
    private String passwordForDeletion;
    
    public void deleteList(){
        try {
            //This is potentially a scheduled background processing, but the 
            //background scheduling capability is not up yet, so we will just 
            //try to delete it at the frontend.
            
            //Check if password provided is correct
            userService.login(userContainer.getUsername(), passwordForDeletion);
            
            subService.deleteList(program.getListEditing().getOBJECTID());
            
            //Display success message
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_FATAL, 
                    "List "+program.getListEditing().getLIST_NAME()+" has been deleted!", null);
            //Remove the list from editing panel once it has been deleted
            program.setListEditing(null);
            
            //Reload the entire page to refresh all other components in the page
            program.refresh();
            
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR,  ex.getMessage(), null);
        } catch (UserAccountLockedException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR,  ex.getMessage(), null);
        } catch (UserLoginException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR,  ex.getMessage(), null);
        } catch (EntityNotFoundException ex) {
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
