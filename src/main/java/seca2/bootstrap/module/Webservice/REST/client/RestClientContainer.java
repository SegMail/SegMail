/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Webservice.REST.client;

import java.io.Serializable;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * This is a global Singleton that stores the application's API keys.
 * 
 * @author LeeKiatHaw
 */
//@SessionScoped
@ApplicationScoped
@Startup
@Named("RestClientContainer")
public class RestClientContainer implements Serializable {
    
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
