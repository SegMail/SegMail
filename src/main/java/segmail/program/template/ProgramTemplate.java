/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.template;

import eds.component.client.ClientService;
import eds.component.data.DBConnectionException;
import segmail.component.subscription.SubscriptionService;
import eds.component.user.UserService;
import eds.entity.client.Client;
import segmail.entity.subscription.email.EmailTemplate;
import eds.entity.user.UserType;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
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
@Named("ProgramTemplate")
@SessionScoped
public class ProgramTemplate implements Serializable {
    
    @EJB
    private SubscriptionService subscriptionService;
    @EJB
    private UserService userService;
    @EJB
    private ClientService clientService;
    
    @Inject private UserContainer userContainer;
    
    @Inject private ProgramTemplateLoader loader;
    
    private List<EmailTemplate> confirmationTemplates;
    
    private List<EmailTemplate> newsletterTemplates;
    
    private List<UserType> allUserTypes;
    
    private Client client;
    
    private final String formName = "ProgramTemplate";
    
    
    // @PostConstruct
    public void init(){
        //this.initializeClient();
        //this.initializeAllConfirmationTemplates();
        //this.initializeAllNewsletterTemplates();
        //this.initializeAllTemplates();
        //this.initializeAllUserTypes();
        
        // Rightfully, a program bean should not be performing any loading logic, 
        // but just be a holding shell for all the required frontend data.
        // Initialize loader for the 1st time
        this.initializeClient();
        this.initializeAllTemplates();
        
    }
    
    public void initializeAllConfirmationTemplates() {
        try {
            if (this.getClient() == null) {
                this.initializeClient();
            }
            this.setConfirmationTemplates(subscriptionService.getAvailableTemplatesForClient(this.getClient().getOBJECTID(),
                    EmailTemplate.EMAIL_TYPE.CONFIRMATION));

        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, "Could not connect to DB!", "Please contact administrators.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public void initializeAllNewsletterTemplates() {
        try {
            if (this.getClient() == null) {
                this.initializeClient();
            }
            this.setNewsletterTemplates(subscriptionService.getAvailableTemplatesForClient(this.getClient().getOBJECTID(),
                    EmailTemplate.EMAIL_TYPE.NEWSLETTER));

        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, "Could not connect to DB!", "Please contact administrators.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public void initializeAllUserTypes() {
        try {
            this.setAllUserTypes(userService.getAllUserTypes());

        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, "Could not connect to DB!", "Please contact administrators.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public void initializeClient() {
        try {
            this.setClient(clientService.getClientByAssignedObjectId(this.getUserContainer().getUser().getOBJECTID()));

        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, "Could not connect to DB!", "Please contact administrators.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public void initializeAllTemplates() {
        this.initializeAllConfirmationTemplates();
        this.initializeAllNewsletterTemplates();
    }

    public List<EmailTemplate> getConfirmationTemplates() {
        return confirmationTemplates;
    }

    public void setConfirmationTemplates(List<EmailTemplate> confirmationTemplates) {
        this.confirmationTemplates = confirmationTemplates;
    }

    public List<UserType> getAllUserTypes() {
        return allUserTypes;
    }

    public void setAllUserTypes(List<UserType> allUserTypes) {
        this.allUserTypes = allUserTypes;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<EmailTemplate> getNewsletterTemplates() {
        return newsletterTemplates;
    }

    public void setNewsletterTemplates(List<EmailTemplate> newsletterTemplates) {
        this.newsletterTemplates = newsletterTemplates;
    }

    public ProgramTemplateLoader getLoader() {
        return loader;
    }

    public void setLoader(ProgramTemplateLoader loader) {
        this.loader = loader;
    }

    UserContainer getUserContainer() {
        return userContainer;
    }

    void setUserContainer(UserContainer userContainer) {
        this.userContainer = userContainer;
    }

    
}
