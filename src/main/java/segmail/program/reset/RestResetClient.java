package segmail.program.reset;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import seca2.bootstrap.module.Webservice.REST.client.GenericRestClient;

/**
 *
 * @author LeeKiatHaw
 */
@Named("RestResetClient")
public class RestResetClient extends GenericRestClient {
    
    public String sendResetEmail(String email) {
        WebTarget target = getWebTarget("account/segmail/reset/init").queryParam("email", email);
        
        Response response = target.request(
                MediaType.APPLICATION_JSON_TYPE).post(
                        Entity.entity(null, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        
        if(response.getStatus() != Response.Status.OK.getStatusCode()) {
            String error = response.readEntity(String.class);
            throw new RuntimeException(error);
        }
        
        String result = response.readEntity(String.class);
        
        return result;
    }
    
    public Response retrieveRequestByToken(String token) {
        WebTarget target = getWebTarget("account/segmail/reset/retrieve").queryParam("token", token);
        
        Response response = target.request(
                MediaType.APPLICATION_JSON_TYPE).get();
        
        return response;
    }
    
    public Response resetPassword(String token, String password) {
        WebTarget target = getWebTarget("account/segmail/reset/password")
                .queryParam("token", token)
                .queryParam("password", password);
        
        Response response = target.request(
                MediaType.APPLICATION_JSON_TYPE).post(
                        Entity.entity(null, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        
        return response;
    }
}
