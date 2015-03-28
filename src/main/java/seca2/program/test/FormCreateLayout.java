/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import eds.component.data.DBConnectionException;
import eds.component.layout.LayoutRegistrationException;
import eds.component.layout.LayoutService;
import eds.component.user.UserAccountLockedException;
import seca2.bootstrap.module.User.UserContainer;
import eds.component.user.UserLoginException;
import eds.component.user.UserNotFoundException;
import eds.component.user.UserRegistrationException;
import eds.component.user.UserService;
import eds.component.user.UserTypeException;
import eds.entity.user.UserType;
import java.util.logging.Level;
import java.util.logging.Logger;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.Form;

/**
 *
 * @author vincent.a.lee
 */

public class FormCreateLayout extends Form implements Serializable {
    
    private String layoutName;
    private String viewRoot;
    
    @EJB private LayoutService layoutService;
    
    @PostConstruct
    public void init(){
        this.FORM_NAME = "createLayoutForm";
    }
    
    public void registerLayout(){
        try{
            this.layoutService.registerLayout(layoutName, viewRoot);
            FacesMessenger.setFacesMessage(this.FORM_NAME, FacesMessage.SEVERITY_FATAL, "Layout successfully registered.",null);
        } catch (LayoutRegistrationException ex) {
            FacesMessenger.setFacesMessage(this.FORM_NAME, FacesMessage.SEVERITY_ERROR, "Layout Registration error: ",ex.getMessage());
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.FORM_NAME, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } 
    }

    public String getLayoutName() {
        return layoutName;
    }

    public void setLayoutName(String layoutName) {
        this.layoutName = layoutName;
    }

    public String getViewRoot() {
        return viewRoot;
    }

    public void setViewRoot(String viewRoot) {
        this.viewRoot = viewRoot;
    }
    
    
}
