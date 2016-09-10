/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.autoresponder;

import eds.component.GenericObjectService;
import eds.component.data.DBConnectionException;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import java.util.List;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.UserSessionContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.FormEditEntity;
import segmail.component.subscription.ListService;
import segmail.component.subscription.autoresponder.AutoresponderService;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.autoresponder.Assign_AutoresponderEmail_List;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormEditExistingTemplate")
@RequestScoped
public class FormEditExistingTemplate implements FormEditEntity {

    @EJB
    private AutoresponderService autoresponderService;
    @EJB
    private GenericObjectService objectService;
    //@EJB private UserService userService;
    @EJB
    private ListService listService;

    @Inject
    private ProgramAutoresponder program;

    @Inject
    private UserSessionContainer userContainer;
    @Inject
    private UserRequestContainer requestContainer;

    @PostConstruct
    public void init() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            loadListFields();
        }
    }

    @Override
    public void closeWithoutSaving() {
        program.refresh();
    }

    public ProgramAutoresponder getProgram() {
        return program;
    }

    public void setProgram(ProgramAutoresponder program) {
        this.program = program;
    }

    
    public AutoresponderEmail getEditingTemplate() {
        return program.getEditingTemplate();
    }

    public void setEditingTemplate(AutoresponderEmail editingTemplate) {
        program.setEditingTemplate(editingTemplate);
    }
    
    public List<SubscriptionListField> getListFields() {
        return program.getListFields();
    }

    public void setListFields(List<SubscriptionListField> listFields) {
        program.setListFields(listFields);
    }

    @Override
    public void saveAndContinue() {
        try {
            AutoresponderEmail newTemplate = autoresponderService.saveAutoEmail(program.getEditingTemplate());

            program.setEditingTemplate(newTemplate);
            loadListFields();
            //Set success message
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Template updated.", null);

        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (EntityExistsException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (EJBException ex) { //Transaction did not go through
            //Throwable cause = ex.getCause();
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    @Override
    public void saveAndClose() {
        saveAndContinue();
        closeWithoutSaving();
    }

    @Override
    public void delete() {
        try {
            autoresponderService.deleteAutoEmail(program.getEditingTemplate().getOBJECTID());
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Template deleted.", null);
            program.refresh();

        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }
    
    public void loadListFields() {
        if(this.getEditingTemplate() == null) 
            return;
        
        List<SubscriptionList> assignedLists = this.objectService.getAllTargetObjectsFromSource(
                getEditingTemplate().getOBJECTID(), 
                Assign_AutoresponderEmail_List.class, 
                SubscriptionList.class);
        
        if(assignedLists == null || assignedLists.isEmpty())
            throw new RuntimeException("List is not assigned.");
        
        SubscriptionList assignedList = assignedLists.get(0);
        
        List<SubscriptionListField> listFields = listService.getFieldsForSubscriptionList(assignedList.getOBJECTID());
        
        setListFields(listFields);
    }
}
