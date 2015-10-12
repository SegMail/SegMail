/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import eds.component.user.UserService;
import segmail.component.subscription.SubscriptionService;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.User.UserContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.entity.subscription.SubscriptionList;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormListSettings")
@RequestScoped
public class FormListSettings {
    @Inject private ProgramList program;
    @Inject private UserContainer userContainer;
    
    @EJB private SubscriptionService subService;
    @EJB private UserService userService;
    
    private final String formName = "FormListSettings";
    
    @PostConstruct
    public void init(){
        
    }
    
    /**
     * Dirty trick to invoke the PostConstruct method of this RequestScoped class
     * @return 
     */
    public SubscriptionList getList(){
        return program.getListEditing();
    }
    
    public void saveSettings(){
        try {
            subService.saveList(program.getListEditing());
        } catch (EJBException ex) { //Transaction did not go through
            //Throwable cause = ex.getCause();
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    
    
    
}
