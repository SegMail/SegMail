/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.mysettings;

import eds.component.data.DBConnectionException;
import eds.component.user.UserService;
import eds.entity.user.APIAccount;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserSessionContainer;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author LeeKiatHaw
 */
@Named("DevAPIForm")
@RequestScoped
public class DevAPIForm {
    
    @EJB private UserService userService;
    
    @Inject private UserSessionContainer userContainer;
    @Inject private MySettingsProgram mySettingsProgram;
    
    private final String formName = "dev_api_form";
    
    private APIAccount apiAccount;
    
    @PostConstruct
    public void init(){
        try{
            this.initAPIAccount();
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.mySettingsProgram.getPageName(), FacesMessage.SEVERITY_ERROR, "Could not connect to DB!", "Please contact administrators.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.mySettingsProgram.getPageName(), FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void initAPIAccount(){
        //apiAccount = userService.(userContainer.getUser().getOBJECTID());
    }
    
    public void generateAPIKey(){
        
    }
    
}
