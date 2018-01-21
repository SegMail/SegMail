/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import eds.component.GenericObjectService;
import eds.component.client.ClientService;
import eds.component.data.EnterpriseObjectNotFoundException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import segmail.component.subscription.SubscriptionService;
import eds.entity.client.Client;
import eds.entity.client.VerifiedSendingAddress;
import segmail.entity.subscription.SubscriptionList;
import eds.entity.user.User;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserSessionContainer;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.ListService;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormAddList")
@RequestScoped
public class FormAddList {
    
    private String listName;
    
    private boolean remote;
    
    @Inject private UserSessionContainer userContainer;
    @Inject private ClientContainer clientCont;
    
    @Inject private ProgramList program;
    
    @EJB private GenericObjectService objService;
    @EJB private ListService listService;
    @EJB private ClientService clientService;
    private boolean startFirstList;
    
    private final String formName = "FormAddList";
    
    @PostConstruct
    public void init(){
        checkNoListYet();
        if(!FacesContext.getCurrentInstance().isPostback()) {
            loadVerifiedAddresses();
        }
    }
    
    public void addList(){
        try {
            User user = userContainer.getUser();
            if(user == null)
                throw new RuntimeException("No user object found for this session "+userContainer);
            
            Client client = clientService.getClientByAssignedUser(user.getOBJECTID());
            if(client == null)
                throw new RuntimeException("No client object found for this user "+user);
            
            SubscriptionList SubscriptionList = listService.createList(listName, remote, client.getOBJECTID());
            
            //this.checkNoListYet(); //refresh the editing panel
            
            //redirect to itself after setting list editing
            //ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            //ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI()); can't do this else it will show .xhtml
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_FATAL, "List added. ", "You can set up your list now!");
            this.program.setListEditing(SubscriptionList);
            program.refresh();
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (EnterpriseObjectNotFoundException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (EJBException ex) { //Transaction did not go through
            Throwable cause = ex.getCause();
            String message = "Don't know what happened!";
            if(cause != null) message = cause.getMessage();
            
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, message, null);
        } catch (RelationshipExistsException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } 
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public boolean isRemote() {
        return remote;
    }

    public void setRemote(boolean remote) {
        this.remote = remote;
    }
    
    public void checkNoListYet() {
        startFirstList = (program.getAllLists() == null || program.getAllLists().isEmpty());
    }

    public boolean isStartFirstList() {
        return startFirstList;
    }

    public void setStartFirstList(boolean startFirstList) {
        this.startFirstList = startFirstList;
    }
    
    public List<VerifiedSendingAddress> getVerifiedAddresses() {
        return program.getVerifiedAddresses();
    }
    
    public String getSendingAddress() {
        return program.getSendingAddress();
    }

    public void setSendingAddress(String sendingAddress) {
        program.setSendingAddress(sendingAddress);
    }

    public void setVerifiedAddresses(List<VerifiedSendingAddress> verifiedAddresses) {
        program.setVerifiedAddresses(verifiedAddresses);
    }
    
    public void loadVerifiedAddresses() {
        List<VerifiedSendingAddress> verifiedAddresses = objService.getEnterpriseData(clientCont.getClient().getOBJECTID(), VerifiedSendingAddress.class);
        setVerifiedAddresses(verifiedAddresses);
    }
}
