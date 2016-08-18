/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Webservice.REST.client;

import eds.component.data.IncompleteDataException;
import eds.component.webservice.WebserviceSOAPKeys;
import eds.component.webservice.WebserviceService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import seca2.component.landing.LandingServerGenerationStrategy;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;
import seca2.entity.landing.ServerInstance;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
public abstract class GenericRestClient {

    private static final String USERNAME_KEY = "WS_CLIENT_USERNAME";
    private static final String PASSWORD_KEY = "WS_PASSWORD_KEY";

    @Inject
    RestClientContainer restContainer;

    @EJB
    LandingService landingService;
    @EJB
    WebserviceService wsService;

    protected WebTarget webTarget;
    protected Client client;
    
    protected String restPath;
    
    protected String targetEndpointURL;

    @PostConstruct
    public void init(){
        initAuth();
    }
    
    public void initAuth() {
        try {
            restPath = wsService.getRestPath();
            
            if (restContainer.getApiKey() == null || restContainer.getApiKey().isEmpty()) {
                authenticate();
            }
            
            ServerInstance targetServer = landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN, ServerNodeType.ERP);
            if (targetServer == null) {
                throw new RuntimeException("No ERP servers setup.");
            }
            
            targetEndpointURL = targetServer.getURI();
            client = javax.ws.rs.client.ClientBuilder.newClient();
            
        } catch (IncompleteDataException ex) {
            Logger.getLogger(GenericRestClient.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }

    }

    public void authenticate() throws IncompleteDataException {
        String username = System.getProperty(USERNAME_KEY);
        String password = System.getProperty(PASSWORD_KEY);
        String server = landingService.getOwnServerName();

        //Call authentication endpoint
        Form form = new Form();
        form.param(WebserviceSOAPKeys.USERNAME, username);
        form.param(WebserviceSOAPKeys.PASSWORD, password);
        form.param(WebserviceSOAPKeys.SERVER_NAME, server);

        ServerInstance targetServer = landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN, ServerNodeType.ERP);
        if (targetServer == null) {
            throw new RuntimeException("No target server configured.");
        }

        Client authClient = ClientBuilder.newClient();
        WebTarget authWebTarget = authClient.target(targetServer.getURI()).path(restPath).path("authentication");
        Response response = authWebTarget.request(
                MediaType.APPLICATION_JSON_TYPE).post(
                        Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        String token = response.readEntity(String.class);

        restContainer.setApiKey(token);
    }

    /**
     * 
     * @param endpointPath only the class that extends this client will know what is its endpoint path to call
     * @return 
     */
    protected WebTarget getWebTarget(String endpointPath) {
        webTarget = client.target(targetEndpointURL).path(restPath).path(endpointPath);
        
        return webTarget;
    }
}
