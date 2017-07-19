/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Client;

import eds.component.client.ClientFacade;
import eds.entity.client.Client;
import eds.entity.client.ContactInfo;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author LeeKiatHaw
 */
@SessionScoped
@Named("ClientContainer")
public class ClientContainer implements Serializable, ClientFacade {
    
    private Client client;
    private ContactInfo contact;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ContactInfo getContact() {
        return contact;
    }

    public void setContact(ContactInfo contact) {
        this.contact = contact;
    }
}
