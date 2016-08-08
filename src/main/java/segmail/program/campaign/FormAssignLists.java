/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import eds.component.data.EntityNotFoundException;
import java.util.ArrayList;
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
import seca2.program.FormEditEntity;
import segmail.component.campaign.CampaignService;
import segmail.component.subscription.ListService;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.SubscriptionList;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormAssignLists")
public class FormAssignLists implements FormEditEntity {
    
    @Inject ProgramCampaign program;
    @Inject ClientContainer clientContainer;
    
    @EJB SubscriptionService subService;
    @EJB CampaignService campService;
    @EJB ListService listService;
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()) {
            loadOwnLists();
            loadTargetLists();
        }
    }
    
    public List<SubscriptionList> getOwnedLists() {
        return program.getOwnedLists();
    }

    public void setOwnedLists(List<SubscriptionList> ownedLists) {
        program.setOwnedLists(ownedLists);
    }
    
    public List<String> getAssignedLists() {
        return program.getSelectedLists();
    }

    public void setAssignedLists(List<String> assignedLists) {
        program.setSelectedLists(assignedLists);
    }
    
    public List<SubscriptionList> getTargetLists() {
        return program.getTargetLists();
    }

    public void setTargetLists(List<SubscriptionList> targetLists) {
        program.setTargetLists(targetLists);
    }
    
    public void loadOwnLists() {
        List<SubscriptionList> ownedList = listService.getAllListForClient(clientContainer.getClient().getOBJECTID());
        setOwnedLists(ownedList);
    }
    
    public void loadTargetLists() {
        List<SubscriptionList> targetList = campService.getTargetedLists(program.getEditingCampaignId());
        setTargetLists(targetList);
        List<String> targetListsIds = new ArrayList<>();
        for(SubscriptionList list : targetList) {
            targetListsIds.add(Long.toString(list.getOBJECTID()));
        }
        setAssignedLists(targetListsIds);
    }

    @Override
    public void saveAndContinue() {
        try {
            //need to convert the list of String to Long
            List<Long> convertedIds = new ArrayList<>();
            for(String idString : program.getSelectedLists()) {
                Long idLong = Long.parseLong(idString);
                convertedIds.add(idLong);
            }
            campService.assignTargetListToCampaign(convertedIds, program.getEditingCampaignId());
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Target lists updated", "");
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (EJBException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }

    @Override
    public void saveAndClose() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void closeWithoutSaving() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

