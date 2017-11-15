/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import segmail.component.subscription.ListService;
import segmail.entity.subscription.FIELD_TYPE;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormImportSubscribers")
public class FormImportSubscribers {
    
    @Inject ProgramSubscribers program;
    
    @EJB ListService listService;
    
    public List<String> getAssignedLists() {
        return program.getAssignedLists();
    }

    public void setAssignedLists(List<String> assignedLists) {
        program.setAssignedLists(assignedLists);
    }

    public List<SubscriptionList> getOwnedLists() {
        return program.getOwnedLists();
    }

    public void setOwnedLists(List<SubscriptionList> ownedLists) {
        program.setOwnedLists(ownedLists);
    }

    public List<SubscriptionListField> getFieldList() {
        return program.getFieldList();
    }

    public void setFieldList(List<SubscriptionListField> fieldList) {
        program.setFieldList(fieldList);
    }
    
    public List<SubscriptionList> getSelectedLists() {
        return program.getSelectedLists();
    }

    public void setSelectedLists(List<SubscriptionList> selectedLists) {
        program.setSelectedLists(selectedLists);
    }
    
    public void done() {
        program.refresh();
    }
    
    public void cancel() {
        program.refresh();
    }
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()) {
            initVar();
        }
    }
    
    public void initVar() {
        setSelectedLists(new ArrayList<SubscriptionList>());
        setFieldList(new ArrayList<SubscriptionListField>());
    }
    
    public void setupImport() {
        List<SubscriptionList> ownedLists =  getOwnedLists();
        List<String> assignedLists = getAssignedLists();
        
        getFieldList().clear();
        getSelectedLists().clear();
        for(String idString : assignedLists) {
            for(SubscriptionList list : ownedLists) {
                long listId = Long.parseLong(idString);
                if(listId == list.getOBJECTID()) {
                    getSelectedLists().add(list);
                    
                    //load all fields into subCont too
                    List<SubscriptionListField> fields = listService.getFieldsForSubscriptionList(listId);
                    getFieldList().addAll(fields);
                    break;
                }
            }
        }
    }
}
