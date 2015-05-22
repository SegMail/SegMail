/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.mysettings;

import eds.component.client.ClientService;
import eds.component.data.DBConnectionException;
import eds.entity.client.Client;
import eds.entity.client.ContactInfo;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.User.UserContainer;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author LeeKiatHaw
 */
@SessionScoped
@Named("MySettingsProgram")
public class MySettingsProgram implements Serializable {
    
    private final String pageName = "my_settings_program";
    
    @Inject private UserContainer userContainer;
    
    @EJB private ClientService clientService;
    
    private ContactInfo contactInfo;
    
    @PostConstruct
    public void init(){
        
    }
    
    public String getPageName() {
        return pageName;
    }


    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }
    
    
}
