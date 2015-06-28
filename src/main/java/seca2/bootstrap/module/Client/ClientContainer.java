/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Client;

import eds.entity.client.Client;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;

/**
 *
 * @author LeeKiatHaw
 */
@SessionScoped
public class ClientContainer implements Serializable {
    
    private Client client;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
