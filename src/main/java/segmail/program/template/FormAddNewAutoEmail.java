/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.template;

import eds.component.client.ClientFacade;
import segmail.component.subscription.SubscriptionService;
import eds.component.user.UserService;
import segmail.entity.subscription.email.AutoresponderEmail;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.entity.subscription.email.AutoEmailTypeFactory;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormAddNewTemplate")
@RequestScoped
public class FormAddNewAutoEmail {
    
    @EJB private SubscriptionService subscriptionService;
    @EJB private UserService userService;
    
    @Inject private ProgramWelcomeEmail program;
    @Inject private ClientFacade clientFacade;
    @Inject private UserRequestContainer reqContainer;
    
    private String subject;
    
    private String body;
    
    private AutoEmailTypeFactory.TYPE type;
    
    private final String formName = "add_new_auto_email_form";
    
    @PostConstruct
    public void init(){
        
    }
    
    public void addNewAutoEmail(){
        try {
            // Create the new template
            // Get the client associated with the user and assign it
            AutoresponderEmail newTemplate = subscriptionService.createAndAssignAutoEmail(subject, body, type);
            
            //Refresh the list of email templates on the page
            //program.initializeAllTemplates(); //no need because ProgramTemplateLoader is loading all the shit
            
            //redirect to itself after setting list editing
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            //ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI()); can't do this else it will show .xhtml
            //ec.redirect(programContainer.getCurrentURL());
            ec.redirect(ec.getRequestContextPath()+"/".concat(reqContainer.getProgramName()));
            
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

    public AutoEmailTypeFactory.TYPE getType() {
        return type;
    }

    public void setType(AutoEmailTypeFactory.TYPE type) {
        this.type = type;
    }

    
}
