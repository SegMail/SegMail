/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import eds.component.data.DataValidationException;
import eds.component.data.IncompleteDataException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.ListService;
import segmail.entity.subscription.FIELD_TYPE;
import segmail.entity.subscription.SubscriptionList;
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
    @EJB
    private ListService listService;

    private final String formName = "form_list_fieldset";
    
    private SubscriptionListField newField;

    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (!fc.isPostback()) {
            loadExistingFields();
            
        }
        loadNewField();
    }
    
    public void loadNewField() {
        
        this.newField = new SubscriptionListField(
                program.getListEditing(),
                getFieldList().size() + 1,
                false,
                "",
                FIELD_TYPE.TEXT,
                ""
        );
    }
    
    public void loadExistingFields() {
        if(getListEditing() == null )
            return;
        
        List<SubscriptionListField> existingFields = listService.getFieldsForSubscriptionList(getListEditing().getOBJECTID());
        setFieldList(existingFields);
    }
    
    public void saveFields() {
        try {
            //Add the new field into the mix
            /**
             * Not needed as we will have addField() method and button to explicitly add a new field
             * if(newField.getFIELD_NAME() != null && !newField.getFIELD_NAME().isEmpty())
                getFieldList().add(newField);
                */
            
            //Remove fields that have no FIELD_NAME to delete them
            /**
             * Not needed as we have deleteField(sno) method now
             * List<SubscriptionListField> fields = new ArrayList<>();
            for(SubscriptionListField field : getFieldList()) {
                if(field.getFIELD_NAME() != null && !field.getFIELD_NAME().isEmpty())
                    fields.add(field);
            }*/
            
            listService.fullRefreshUpdateSubscriptionListFields(getFieldList());
            loadExistingFields();
            loadNewField();
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Fields updated.", "");
        } catch (DataValidationException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
            loadExistingFields();
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
            loadExistingFields();
        } 
    }
    
    public void deleteField(int sno) {
        for(int i=0; i<getFieldList().size(); i++) {
            SubscriptionListField field = getFieldList().get(i);
            if(sno == field.getSNO()) {
                getFieldList().remove(field);
            }
        }
        saveFields();
    }
    
    public void addField() {
        if(newField.getFIELD_NAME() != null && !newField.getFIELD_NAME().isEmpty())
            getFieldList().add(newField);
        saveFields();
    }
    
    public void moveUp(int sno) {
        //If it is 1 or 2, do not allow to move up
        if(sno <= 2)
            return;
        //Index out of bounds
        if(sno > getFieldList().size())
            return;
        
        //Assuming list is sorted, swap places with the previous index
        getFieldList().get(sno-1).setSNO(sno-1); //This is the field
        getFieldList().get(sno-2).setSNO(sno); //This is the field before it
        saveFields();
    }
    
    public void moveDown(int sno) {
        //If it is 1, do not allow to move down
        if(sno <= 1)
            return;
        //If it is the last object, do not allow move down
        if(sno >= getFieldList().size())
            return;
        
        //Assuming list is sorted, swap places with the previous index
        getFieldList().get(sno-1).setSNO(sno+1); //This is the field
        getFieldList().get(sno).setSNO(sno); //This is the field before it
        saveFields();
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
        return this.newField;
    }

    public void setNewField(SubscriptionListField newField) {
        this.newField = newField;
    }
    
    public SubscriptionList getListEditing() {
        return program.getListEditing();
    }

    public void setListEditing(SubscriptionList listEditing) {
        program.setListEditing(listEditing);
    }  
}
