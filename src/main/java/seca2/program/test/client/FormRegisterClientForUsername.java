/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.test.client;

import eds.component.GenericObjectService;
import eds.component.client.ClientService;
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
@Named("FormRegisterClientForUsername")
@RequestScoped
public class FormRegisterClientForUsername {
    
    
    @Inject private ProgramTest programTest;
    
    @EJB private ClientService clientService;
    //@EJB private UserService userService;
    @EJB private GenericObjectService genericDBService;
    
    private final String formName = "registerClientForm";
    
    private long clientTypeId;
    private String username;

    public void registerClientForUsername(){
        try{
            
            List<User> users = this.genericDBService.getEnterpriseObjectsByName(username,User.class);
            if(users == null || users.size() <= 0)
                throw new Exception("User "+username+" is not found!");
            
            //Only chose the first user found
            User user = users.get(0);
            this.clientService.registerClientForUser(user, clientTypeId);
            
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_FATAL, "Client registered for user successfully.", null);
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
    
    public void registerClientForUser(String clientTypename, String username){
        ClientType clientType = this.clientService.getClientTypeByName(clientTypename);
        if(clientType == null)
            throw new RuntimeException("Clienttype "+clientTypename+" could not be found.");
        
        this.setClientTypeId(clientType.getOBJECTID());
        this.setUsername(username);
        
        this.registerClientForUsername();
    }
}
