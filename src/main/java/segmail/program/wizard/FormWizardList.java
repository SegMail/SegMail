/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.wizard;

import eds.component.GenericObjectService;
import eds.component.data.DataValidationException;
import eds.component.data.EnterpriseObjectNotFoundException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import eds.entity.client.Client;
import eds.entity.client.VerifiedSendingAddress;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.ListService;
import segmail.entity.subscription.Assign_Client_List;
import segmail.entity.subscription.SubscriptionList;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormWizardList")
public class FormWizardList {
    
    @Inject ProgramSetupWizard program;
    @Inject FormWizardInit formWizard;
    
    @Inject ClientContainer clientCont;
    
    @EJB GenericObjectService objService;
    @EJB private ListService listService;
    
    @PostConstruct
    public void init() {
        loadExistingLists();
    }
    
    public List<SubscriptionList> getExistingLists() {
        return program.getExistingLists();
    }

    public void setExistingLists(List<SubscriptionList> existingLists) {
        program.setExistingLists(existingLists);
    }
    
    public String getListname() {
        return program.getListname();
    }

    public void setListname(String listname) {
        program.setListname(listname);
    }
    
    public VerifiedSendingAddress getSelectedAddress() {
        return program.getSelectedAddress();
    }

    public void setSelectedAddress(VerifiedSendingAddress selectedAddress) {
        program.setSelectedAddress(selectedAddress);
    }
    
    public SubscriptionList getSelectedList() {
        return program.getSelectedList();
    }

    public void setSelectedList(SubscriptionList selectedList) {
        program.setSelectedList(selectedList);
    }
    
    public void loadExistingLists() {
        Client client = clientCont.getClient();
        List<SubscriptionList> lists = objService.getAllTargetObjectsFromSource(client.getOBJECTID(), Assign_Client_List.class, SubscriptionList.class);
        
        setExistingLists(lists);
    }
    
    public void createList() {
        try {
            setListname(getListname().trim());
            String listname = getListname();
            if(getExistingLists().stream().noneMatch(
                list -> list.getLIST_NAME().equals(listname))){
            
                SubscriptionList newList = listService.createList(listname, true, clientCont.getClient().getOBJECTID());
                getExistingLists().add(newList);
                
            }
            getExistingLists().forEach(list -> {
                if(listname.equals(list.getLIST_NAME())){
                    setSelectedList(list);
                }
            });
            
            // Update SEND_AS address and name if it is not the same
            String sender = program.getAddress();
            if(!getSelectedList().getSEND_AS_EMAIL().equals(sender)) {
                SubscriptionList list = getSelectedList();
                listService.saveList(list);
            }
            
            formWizard.nextPage();
            
        } catch (IncompleteDataException ex) {
            Logger.getLogger(FormWizardList.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessenger.setFacesMessage(ProgramSetupWizard.class.getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (EnterpriseObjectNotFoundException ex) {
            Logger.getLogger(FormWizardList.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessenger.setFacesMessage(ProgramSetupWizard.class.getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (RelationshipExistsException ex) {
            Logger.getLogger(FormWizardList.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessenger.setFacesMessage(ProgramSetupWizard.class.getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (EntityNotFoundException ex) {
            Logger.getLogger(FormWizardList.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessenger.setFacesMessage(ProgramSetupWizard.class.getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (DataValidationException ex) {
            Logger.getLogger(FormWizardList.class.getName()).log(Level.SEVERE, null, ex);
            FacesMessenger.setFacesMessage(ProgramSetupWizard.class.getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
        
    }
    
}
