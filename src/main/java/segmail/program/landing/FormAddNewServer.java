/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.landing;

import eds.component.user.UserService;
import eds.entity.user.UserAccount;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormAddNewServer")
@RequestScoped
public class FormAddNewServer {
    
    @Inject private ProgramLanding program;
    
    @EJB private UserService userService;
    
    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (!fc.isPostback()) {
            initUserAccounts();
        }
    }
    
    public void addServer() {
        try {
            
        } catch (EJBException ex) { 
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        }
    }
    
    public void initUserAccounts() {
        try {
            program.setUserAccounts(userService.getWebServiceUserAccounts());
        } catch (EJBException ex) { 
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        }
    }
    
    public String getAddress() {
        return program.getAddress();
    }
    
    public void setAddress(String address) {
        program.setAddress(address);
    }
    
    public long getUserId() {
        return program.getUserId();
    }
    
    public void setUserId(long userId) {
        program.setUserId(userId);
    }
    
    public List<UserAccount> getUserAccounts() {
        return program.getUserAccounts();
    }

    public void setUserAccounts(List<UserAccount> userAccounts) {
        program.setUserAccounts(userAccounts);
    }
    
}
