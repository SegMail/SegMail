/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.program.FormEditEntity;
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
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()) {
            loadLists();
        }
    }
    
    public List<SubscriptionList> getOwnedLists() {
        return program.getOwnedLists();
    }

    public void setOwnedLists(List<SubscriptionList> ownedLists) {
        program.setOwnedLists(ownedLists);
    }
    
    public List<Long> getAssignedLists() {
        return program.getAssignedLists();
    }

    public void setAssignedLists(List<Long> assignedLists) {
        program.setAssignedLists(assignedLists);
    }
    
    public void loadLists() {
        List<SubscriptionList> ownedList = subService.getAllListForClient(clientContainer.getClient().getOBJECTID());
        setOwnedLists(ownedList);
    }

    @Override
    public void saveAndContinue() {
        
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

