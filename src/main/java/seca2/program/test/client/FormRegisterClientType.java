/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.test.client;

import eds.component.client.ClientService;
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
public class FormRegisterClientType {
    
    @Inject private ProgramTest programTest;
    
    @EJB private ClientService clientService;
    
    private final String formName = "registerClientTypeForm";
    
    private String clientType;
    private String clientTypeDesc;

    public void registerClientType(){
        try{
            this.clientService.registerClientType(clientType,clientTypeDesc);
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_FATAL, "Client registered successfully.", null);
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getClientTypeDesc() {
        return clientTypeDesc;
    }

    public void setClientTypeDesc(String clientTypeDesc) {
        this.clientTypeDesc = clientTypeDesc;
    }
    
    
}
