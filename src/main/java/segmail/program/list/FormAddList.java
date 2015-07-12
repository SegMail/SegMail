/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import eds.component.GenericEnterpriseObjectService;
import eds.component.client.ClientService;
import segmail.component.subscription.SubscriptionService;
import eds.entity.client.Client;
import segmail.entity.subscription.SubscriptionList;
import eds.entity.user.User;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Program.ProgramContainer;
import seca2.bootstrap.module.User.UserContainer;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormAddList")
@RequestScoped
public class FormAddList {
    
    private String listName;
    
    private boolean remote;
    
    @Inject private UserContainer userContainer;
    @Inject private ProgramContainer programContainer;
    
    @Inject private ProgramList programList;
    
    @EJB private SubscriptionService subscriptionService;
    @EJB private ClientService clientService;
    @EJB private GenericEnterpriseObjectService genericDBService;
    private boolean startFirstList;
    
    private final String formName = "FormAddList";
    
    @PostConstruct
    public void init(){
        this.checkNoListYet();
    }
    
    public void addList(){
        try {
            User user = userContainer.getUser();
            if(user == null)
                throw new RuntimeException("No user object found for this session "+userContainer);
            
            Client client = clientService.getClientByAssignedUser(user.getOBJECTID());
            if(client == null)
                throw new RuntimeException("No client object found for this user "+user);
            
            SubscriptionList SubscriptionList = subscriptionService.addList(listName, remote);
            
            
            
            //this.checkNoListYet(); //refresh the editing panel
            
            //redirect to itself after setting list editing
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            //ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI()); can't do this else it will show .xhtml
            ec.redirect(programContainer.getCurrentURL());
            this.programList.setListEditing(SubscriptionList);
            
        } /*catch (IOException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }*/ catch (EJBException ex) { //Transaction did not go through
            Throwable cause = ex.getCause();
            String message = "Don't know what happened!";
            if(cause != null) message = cause.getMessage();
            
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, message, null);
            
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
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
        /*User user = userContainer.getUser();

        if (user == null) {
            throw new RuntimeException("No user object found for this session " + userContainer);
        }

        Client client = clientService.getClientByAssignedUser(user.getOBJECTID());
        if (client == null) {
            throw new RuntimeException("No client object found for this user " + user);
        }*/

        startFirstList = (programList.getAllLists() == null || programList.getAllLists().isEmpty());
    }

    public boolean isStartFirstList() {
        return startFirstList;
    }

    public void setStartFirstList(boolean startFirstList) {
        this.startFirstList = startFirstList;
    }
    
    
}
