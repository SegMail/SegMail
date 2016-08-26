/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Webservice.REST.client;

import java.io.IOException;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.http.HttpHeaders;

/**
 * Intercepts outbound client requests and put in the authentication information.
 * 
 * 
 * @author LeeKiatHaw
 */
@Provider
public class RestClientAuthOutboundFilter implements ClientRequestFilter {

    @Inject RestClientContainer restContainer;
    
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        
        System.out.println("JAX-WS client filter called.");//debug
        
        //If it is an authentication request, don't block it
        if(requestContext.getUri().getPath().endsWith("authentication"))
            return;
        
        //If it does not have an API key, block it
        if(restContainer.getApiKey() == null || restContainer.getApiKey().isEmpty()) {
            requestContext.abortWith(
                    Response.status(Response.Status.BAD_GATEWAY)
                            .entity("No API key. Please request one from the target endpoint /authentication")
                            .build());
        }
        System.out.println("JAX-WS api key: "+restContainer.getApiKey());//debug
        requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer "+restContainer.getApiKey());
        
    }
    
}
