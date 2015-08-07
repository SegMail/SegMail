/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.template;

import eds.component.GenericObjectService;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.email.EmailTemplate;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormEditExistingTemplate")
@RequestScoped
public class FormEditExistingTemplate {
    
    @EJB private SubscriptionService subscriptionService;
    @EJB private GenericObjectService objectService;
    //@EJB private UserService userService;
    
    @Inject private ProgramTemplate program;
    //@Inject private UserContainer userContainer;
    
    //private long templateId;
    
    private EmailTemplate editingTemplate;
    
    private final String formName = "FormEditExistingTemplate";
    
    @PostConstruct
    public void init(){
        
    }
    
    public void loadTemplate(long templateId){
        try {
            // Retrieve the template based on the Id
            editingTemplate = objectService.getEnterpriseObjectById(templateId, EmailTemplate.class);
            
        } catch (EJBException ex) { //Transaction did not go through
            //Throwable cause = ex.getCause();
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void saveTemplate(){
        try {
            subscriptionService.saveTemplate(editingTemplate);
            
            //Refresh the list of email templates on the page
            program.initializeAllTemplates();
            
        } catch (EJBException ex) { //Transaction did not go through
            //Throwable cause = ex.getCause();
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public EmailTemplate getEditingTemplate() {
        return editingTemplate;
    }

    public void setEditingTemplate(EmailTemplate editingTemplate) {
        this.editingTemplate = editingTemplate;
    }

    
    
}
