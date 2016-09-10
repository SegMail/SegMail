/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.autoresponder;

import eds.component.client.ClientFacade;
import eds.component.data.DBConnectionException;
import eds.component.user.UserService;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;
import eds.entity.user.UserType;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserSessionContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.Program;
import segmail.component.subscription.autoresponder.AutoresponderService;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.autoresponder.AUTO_EMAIL_TYPE;

/**
 *
 * @author LeeKiatHaw
 */
@Named("ProgramAutoresponder")
@SessionScoped
public class ProgramAutoresponder extends Program{
    
    @EJB 
    private AutoresponderService autoresponderService;
    @EJB
    private UserService userService;
    
    @Inject private UserSessionContainer userContainer;
    
    @Inject private ClientFacade clientFacade;
    
    private List<AutoresponderEmail> confirmationTemplates;
    
    private List<AutoresponderEmail> welcomeTemplates;
    
    private List<UserType> allUserTypes;
    
    private AutoresponderEmail editingTemplate;
    
    private long editingTemplateId;
    
    private boolean edit;
    
    private List<SubscriptionList> lists;
    
    private long selectedListId;
    
    private List<SubscriptionListField> listFields;

    public void initializeAllUserTypes() {
        try {
            this.setAllUserTypes(userService.getAllUserTypes());

        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "Could not connect to DB!", "Please contact administrators.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
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

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public long getEditingTemplateId() {
        return editingTemplateId;
    }

    public void setEditingTemplateId(long editingTemplateId) {
        this.editingTemplateId = editingTemplateId;
    }

    public List<SubscriptionList> getLists() {
        return lists;
    }

    public void setLists(List<SubscriptionList> lists) {
        this.lists = lists;
    }

    public long getSelectedListId() {
        return selectedListId;
    }

    public void setSelectedListId(long selectedListId) {
        this.selectedListId = selectedListId;
    }

    public List<SubscriptionListField> getListFields() {
        return listFields;
    }

    public void setListFields(List<SubscriptionListField> listFields) {
        this.listFields = listFields;
    }

    @Override
    public void clearVariables() {
        
        
    }

    @Override
    public void initRequestParams() {
        
    }

    @Override
    public void initProgram() {
        
        
    }
    
}
