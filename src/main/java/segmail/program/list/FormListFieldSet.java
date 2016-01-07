/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import eds.component.data.DataValidationException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.FIELD_TYPE;
import segmail.entity.subscription.SubscriptionListField;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormListFieldSet")
@RequestScoped
public class FormListFieldSet {

    @Inject
    private ProgramList program;
    @Inject
    private ClientContainer clientContainer;

    @EJB
    private SubscriptionService subscriptionService;

    private final String formName = "form_list_fieldset";

    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (!fc.isPostback()) {
            //loadListFields(); 
            initNewEmptyField();
        }
    }

    public void save() {
        updateExistingFields();
        addNewField();
        //Don't do page refresh, just update the field list
        loadListFields();
    }

    public void initNewEmptyField() {
        try {
            int initSNO = (getFieldList() == null) ? 1 : getFieldList().size();
            this.setNewField(new SubscriptionListField(initSNO + 1, false, "", FIELD_TYPE.TEXT, ""));
        } catch (EJBException ex) {
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }

    public void loadListFields() {
        try {
            long listId = program.getListEditingId();
            //to improve performance
            //no! it's necessary else there will be nullpointerexception :p
            if (listId <= 0) {
                return;
            }
            List<SubscriptionListField> fieldList = subscriptionService.getFieldsForSubscriptionList(listId);
            this.program.setFieldList(fieldList);

        } catch (EJBException ex) {
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }

    public void addNewField() {
        try {

            long currentList = program.getListEditingId();
            SubscriptionListField newField = program.getNewField();
            //If no field name is set, take it as user did not intend to add a new field
            if (newField.getFIELD_NAME() == null || newField.getFIELD_NAME().isEmpty()) {
                return;
            }

            newField = subscriptionService.addFieldForSubscriptionList(currentList, newField);
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_FATAL, "New field added.", null);
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (DataValidationException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (EJBException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }

    public void updateExistingFields() {
        try {
            subscriptionService.updateSubscriptionListFields(program.getFieldList());
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_FATAL, "List fields updated.", null);
        } catch (DataValidationException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (EJBException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }

    /**
     * Setters and Getters access variables in the program object, which is
     * session scoped. This is because users usually navigate in between
     * programs in a single session and we do not want all forms to be session
     * scoped.
     *
     * @return
     */
    public List<SubscriptionListField> getFieldList() {
        return program.getFieldList();
    }

    public void setFieldList(List<SubscriptionListField> fieldList) {
        program.setFieldList(fieldList);
    }

    public FIELD_TYPE[] getFieldTypes() {
        return program.getFieldTypes();
    }

    public SubscriptionListField getNewField() {
        return program.getNewField();
    }

    public void setNewField(SubscriptionListField newField) {
        program.setNewField(newField);
    }
}
