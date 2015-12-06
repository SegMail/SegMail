/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserSessionContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.SubscriptionService;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormSetupListTypes")
@RequestScoped
public class FormSetupListTypes {
    @Inject private ProgramList programList;
    @Inject private UserSessionContainer userContainer;
    
    @EJB private SubscriptionService subService;
    
    @PostConstruct
    public void init(){
        
    }
    
    public void setupListType(){
        try {
            subService.setupListTypes();
        } catch (EJBException ex) { //Transaction did not go through
            //Throwable cause = ex.getCause();
            FacesMessenger.setFacesMessage(programList.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } /*catch (Exception ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }*/
    }
}
