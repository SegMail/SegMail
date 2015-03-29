/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.test.layout;

import eds.component.data.DBConnectionException;
import eds.component.layout.LayoutAssignmentException;
import eds.component.layout.LayoutRegistrationException;
import eds.component.layout.LayoutService;
import eds.component.user.UserService;
import eds.entity.layout.Layout;
import eds.entity.user.User;
import eds.entity.user.UserAccount;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.Form;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
public class FormAssignLayoutUsername extends Form {
    
    @EJB private LayoutService layoutService;
    @EJB private UserService userService;
    
    private List<Layout> allLayouts;
    
    private String username;
    private long layoutId;

    @PostConstruct
    @Override
    protected void init() {
        this.FORM_NAME = "assignLayoutToUsername";
        initializeAllLayout();
    }
    
    public void initializeAllLayout(){
        try{
            this.allLayouts = this.layoutService.getAllLayouts();
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.FORM_NAME, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.FORM_NAME, FacesMessage.SEVERITY_ERROR, ex.getLocalizedMessage().getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void assignLayoutToUsername(){
        try{
            UserAccount userAccount = this.userService.getUserAccountByUsername(username);
            User user = (User) userAccount.getOWNER();
            //this.layoutService.assignLayoutToUser(layoutId, user.getOBJECTID()); //the wrong IDs were passed in, should we pass in objects instead?
            this.layoutService.assignLayoutToUser(user.getOBJECTID(), layoutId);
            
            FacesMessenger.setFacesMessage(this.FORM_NAME, FacesMessage.SEVERITY_FATAL, "Layout successfully registered.",null);
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.FORM_NAME, FacesMessage.SEVERITY_ERROR, "Could not connect to database! ", "Please contact admin.");
        } catch (LayoutAssignmentException ex) {
            FacesMessenger.setFacesMessage(this.FORM_NAME, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName()+": ", ex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.FORM_NAME, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName()+": ", ex.getMessage());
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(long layoutId) {
        this.layoutId = layoutId;
    }

    public List<Layout> getAllLayouts() {
        return allLayouts;
    }

    public void setAllLayouts(List<Layout> allLayouts) {
        this.allLayouts = allLayouts;
    }
    
    
    
}
