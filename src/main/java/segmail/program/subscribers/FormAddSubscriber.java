/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.ListService;
import segmail.component.subscription.MassSubscriptionService;
import segmail.component.subscription.SubscriptionException;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormAddSubscriber")
public class FormAddSubscriber {
    
    @Inject ProgramSubscribers program;
    
    @Inject ClientContainer clientCont;
    
    @EJB
    private SubscriptionService subService;
    @EJB
    private ListService listService;
    @EJB
    private MassSubscriptionService massSubService;
    
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
    
    public Map<String,Object> getFieldValues() {
        return program.getFieldValues();
    }
    
    public void setFieldValues(Map<String, Object> fieldValues) {
        program.setFieldValues(fieldValues);
    }
    
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
    
    public void addSubscriber() {
        try {
            if (getSelectedLists() == null || getSelectedLists().isEmpty()) {
                throw new RuntimeException("List is not set yet but you still manage to come to this page? Notify your admin immediately! =)");
            }
            List<Map<String,Object>> subscMap = new ArrayList<>();
            subscMap.add(getFieldValues());
            
            subService.subscribe(clientCont.getClient().getOBJECTID(), getSelectedLists(), getFieldValues(), true);
            
            FacesMessenger.setFacesMessage(ProgramSubscribers.class.getSimpleName(), FacesMessage.SEVERITY_FATAL, "Subscriber added! A welcome email will be sent to the subscriber soon.", null);
            //How to redirect to List editing panel?
            program.refresh();
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (SubscriptionException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (RelationshipExistsException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "Subscriber is already on this list.", "");
        }  
    }
    
}
