/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.autoresponder;

import eds.component.client.ClientFacade;
import eds.component.client.ClientService;
import eds.component.data.DBConnectionException;
import eds.component.user.UserService;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;
import eds.entity.user.UserType;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserSessionContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.autoresponder.AutoresponderService;
import segmail.entity.subscription.autoresponder.AUTO_EMAIL_TYPE;

/**
 *
 * @author LeeKiatHaw
 */
@Named("ProgramWelcomeEmail")
@SessionScoped
public class ProgramAutoresponder implements Serializable {
    
    @EJB 
    private AutoresponderService autoresponderService;
    @EJB
    private UserService userService;
    @EJB
    private ClientService clientService;
    
    @Inject private UserSessionContainer userContainer;
    
    @Inject private ProgramTemplateLoader loader;
    
    @Inject private ClientFacade clientFacade;
    
    private List<AutoresponderEmail> confirmationTemplates;
    
    private List<AutoresponderEmail> welcomeTemplates;
    
    private List<UserType> allUserTypes;
    
    private final String formName = "ProgramTemplate";
    
    private AutoresponderEmail editingTemplate;
    
    // @PostConstruct
    public void init(){
        //this.initializeClient();
        //this.initializeAllConfirmationEmails();
        //this.initializeAllWelcomeEmails();
        //this.initializeAllTemplates();
        //this.initializeAllUserTypes();
        
        // Rightfully, a program bean should not be performing any loading logic, 
        // but just be a holding shell for all the required frontend data.
        // Initialize loader for the 1st time
        this.initializeAllTemplates();
        
    }
    
    public void initializeAllConfirmationEmails() {
        try {
            
            //this.setConfirmationTemplates(autoresponderService.getAvailableConfirmationEmailForClient(clientFacade.getClient().getOBJECTID()));
            System.out.println(autoresponderService.getClass().getSimpleName())
;            this.setConfirmationTemplates(autoresponderService.getAvailableAutoEmailsForClient(clientFacade.getClient().getOBJECTID(),AUTO_EMAIL_TYPE.CONFIRMATION));

        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, "Could not connect to DB!", "Please contact administrators.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public void initializeAllWelcomeEmails() {
        try {
            
            //this.setWelcomeTemplates(autoresponderService.getAvailableWelcomeEmailForClient(clientFacade.getClient().getOBJECTID()));
            this.setWelcomeTemplates(autoresponderService.getAvailableAutoEmailsForClient(clientFacade.getClient().getOBJECTID(),AUTO_EMAIL_TYPE.WELCOME));

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
    
    public void initializeAllTemplates() {
        this.initializeAllConfirmationEmails();
        this.initializeAllWelcomeEmails();
    }

    public List<AutoresponderEmail> getConfirmationTemplates() {
        return confirmationTemplates;
    }

    public void setConfirmationTemplates(List<AutoresponderEmail> confirmationTemplates) {
        this.confirmationTemplates = confirmationTemplates;
    }

    public List<UserType> getAllUserTypes() {
        return allUserTypes;
    }

    public void setAllUserTypes(List<UserType> allUserTypes) {
        this.allUserTypes = allUserTypes;
    }

    public List<AutoresponderEmail> getWelcomeTemplates() {
        return welcomeTemplates;
    }

    public void setWelcomeTemplates(List<AutoresponderEmail> welcomeTemplates) {
        this.welcomeTemplates = welcomeTemplates;
    }

    public ProgramTemplateLoader getLoader() {
        return loader;
    }

    public void setLoader(ProgramTemplateLoader loader) {
        this.loader = loader;
    }

    UserSessionContainer getUserContainer() {
        return userContainer;
    }

    void setUserContainer(UserSessionContainer userContainer) {
        this.userContainer = userContainer;
    }

    public AutoresponderEmail getEditingTemplate() {
        return editingTemplate;
    }

    public void setEditingTemplate(AutoresponderEmail editingTemplate) {
        this.editingTemplate = editingTemplate;
    }

    public String getFormName() {
        return formName;
    }

    
}
