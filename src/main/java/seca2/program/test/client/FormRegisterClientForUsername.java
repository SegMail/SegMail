/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.test.client;

import eds.component.GenericEnterpriseObjectService;
import eds.component.client.ClientService;
import eds.component.client.ClientTypeRegistrationException;
import eds.component.data.DBConnectionException;
import eds.component.layout.LayoutAssignmentException;
import eds.component.user.UserService;
import eds.entity.client.ClientType;
import eds.entity.user.User;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.test.ProgramTest;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormRegisterClientType")
@RequestScoped
public class FormRegisterClientForUsername {
    
    @Inject private ProgramTest programTest;
    
    @EJB private ClientService clientService;
    @EJB private UserService userService;
    
    private final String formName = "registerClientForUsernameForm";
    
    private long clientTypeId;
    private String username;

    public void registerClientForUsername(){
        try{
            
            
            
            User user = userService.getUserByUsername(username);
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_FATAL, "Client registered successfully.", null);
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } catch (ClientTypeRegistrationException ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public long getClientTypeId() {
        return clientTypeId;
    }

    public void setClientTypeId(long clientTypeId) {
        this.clientTypeId = clientTypeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<ClientType> getAllClientTypes(){
        return this.programTest.getAllClientTypes();
    }
}
