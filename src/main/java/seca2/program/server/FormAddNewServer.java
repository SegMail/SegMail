/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.server;

import eds.component.data.DataValidationException;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.user.UserService;
import eds.entity.user.User;
import eds.entity.user.UserAccount;
import java.net.URISyntaxException;
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
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormAddNewServer")
@RequestScoped
public class FormAddNewServer {
    
    @Inject private ProgramServer program;
    
    @EJB private LandingService landingService;
    @EJB private UserService userService;
    
    private final String formName = "FormAddNewServer";
    
    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (!fc.isPostback()) {
            initNewServerForm();
        }
    }
    
    public void addServer() {
        try {
            landingService.addServerInstance(program.getName(), program.getUri(), program.getUserIdNew(), ServerNodeType.getNodeType(program.getServerNodeType()));
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "Server added!", null);
            program.refresh();
        } catch (EJBException ex) { 
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (EntityExistsException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, "Please choose a different server name.", "");
        } catch (DataValidationException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (URISyntaxException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }
    
    public String getUri() {
        return program.getUri();
    }
    
    public void setUri(String hostname) {
        program.setUri(hostname);
    }
    
    public long getUserId() {
        return program.getUserIdNew();
    }
    
    public void setUserId(long userId) {
        program.setUserIdNew(userId);
    }
    
    public void setUserId(String username) {
        User user = userService.getUserByUsername(username);
        setUserId(user.getOBJECTID());
    }
    
    public List<UserAccount> getUserAccounts() {
        return program.getUserAccounts();
    }

    public void setUserAccounts(List<UserAccount> userAccounts) {
        program.setUserAccounts(userAccounts);
    }
    
    public String getName() {
        return program.getName();
    }

    public void setName(String name) {
        program.setName(name);
    }
    
    public String getServerNodeType() {
        return program.getServerNodeType();
    }

    public void setServerNodeType(String serverNodeType) {
        program.setServerNodeType(serverNodeType);
    }
    
    public List<String> getTypes() {
        return program.getTypes();
    }

    public void setTypes(List<String> types) {
        program.setTypes(types);
    }

    private void initNewServerForm() {
        this.setName("");
        this.setUri("");
        this.setUserId(-1);
    }
}
