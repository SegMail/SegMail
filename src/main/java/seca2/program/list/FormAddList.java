/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.list;

import eds.component.data.DBConnectionException;
import eds.component.data.EnterpriseObjectNotFoundException;
import eds.component.subscription.SubscriptionService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
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
    
    @EJB private SubscriptionService subscriptionService;
    
    public void addList(){
        try {
            subscriptionService.addList(0, listName, remote);
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } catch (EnterpriseObjectNotFoundException ex) {
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
    
    
    
}
