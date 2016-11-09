/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.test.client;

import eds.component.client.ClientAWSService;
import eds.component.client.ClientService;
import eds.component.data.EntityNotFoundException;
import eds.entity.client.Client;
import eds.entity.client.ClientAWSAccount;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormCreateAWSAccountForUser")
@RequestScoped
public class FormCreateAWSAccountForUser {
    
    @EJB private ClientService clientService;
    @EJB private ClientAWSService clientAWSService;
    
    private String clientname;

    public String getClientname() {
        return clientname;
    }

    public void setClientname(String clientname) {
        this.clientname = clientname;
    }
    
    public void create(){
        try {
            Client client = clientService.getClientByClientname(clientname);
            if(client == null)
                throw new EntityNotFoundException("Client name not found.");
            
            ClientAWSAccount account = clientAWSService.registerAWSForClient(client);
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Account registered with ARN "+account.getARN(), "");
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }
    
}
