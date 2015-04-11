/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.test.client;

import eds.component.GenericEnterpriseObjectService;
import eds.component.client.ClientRegistrationException;
import eds.component.client.ClientService;
import eds.component.client.ClientTypeRegistrationException;
import eds.component.data.DBConnectionException;
import eds.component.user.UserService;
import eds.entity.EnterpriseObject;
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
@Named("FormRegisterClientForObjectname")
@RequestScoped
public class FormRegisterClientForObjectname {
    
    @Inject private ProgramTest programTest;
    
    @EJB private ClientService clientService;
    //@EJB private UserService userService;
    @EJB private GenericEnterpriseObjectService genericDBService;
    
    private final String formName = "registerClientForm";
    
    private long clientTypeId;
    private String objectname;

    public void registerClientForUsername(){
        try{
            
            List<EnterpriseObject> objects = this.genericDBService.getEnterpriseObjectByName(objectname);
            if(objects == null || objects.size() <= 0)
                throw new Exception("Object "+objectname+" is not found!");
            
            //Only chose the first object found
            EnterpriseObject object = objects.get(0);
            this.clientService.registerClientForObject(object, clientTypeId);
            
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_FATAL, "Client registered for object successfully.", null);
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } catch (ClientRegistrationException ex) {
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

    public String getObjectname() {
        return objectname;
    }

    public void setObjectname(String objectname) {
        this.objectname = objectname;
    }

    public List<ClientType> getAllClientTypes(){
        return this.programTest.getAllClientTypes();
    }
}
