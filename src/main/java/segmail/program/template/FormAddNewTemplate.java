/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.template;

import segmail.component.subscription.SubscriptionService;
import eds.component.user.UserService;
import segmail.entity.subscription.email.EmailTemplate;
import segmail.entity.subscription.email.EmailTemplate.EMAIL_TYPE;
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
@Named("FormAddNewTemplate")
@RequestScoped
public class FormAddNewTemplate {
    
    @EJB private SubscriptionService subscriptionService;
    @EJB private UserService userService;
    
    @Inject private ProgramTemplate program;
    
    private String subject;
    
    private String body;
    
    private EMAIL_TYPE type;
    
    private final String formName = "add_new_template_form";
    
    @PostConstruct
    public void init(){
        
    }
    
    public void addNewTemplate(){
        try {
            // Create the new template
            // Get the client associated with the user and assign it
            EmailTemplate newTemplate = subscriptionService.addTemplate(subject, body, type, program.getClient().getOBJECTID());
            
            //Refresh the list of email templates on the page
            program.initializeAllTemplates();
            
        } catch (EJBException ex) { //Transaction did not go through
            //Throwable cause = ex.getCause();
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public EMAIL_TYPE getType() {
        return type;
    }

    public void setType(EMAIL_TYPE type) {
        this.type = type;
    }

    
}
