/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.subscribe.client;

import eds.component.data.RelationshipExistsException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.OK;
import seca2.bootstrap.module.Webservice.REST.client.GenericRestClient;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;

/**
 * Jersey REST client generated for REST resource:WSHttpSubscribe
 * [/subscribe]<br>
 * USAGE:
 * <pre>
        RestClientSubscribe client = new RestClientSubscribe();
        Object response = client.XXX(...);
        // do whatever with response
        client.close();
 </pre>
 *
 * @author LeeKiatHaw
 */
@Named("RestClientSubscribe")
public class RestClientSubscribe extends GenericRestClient {
    
    public String subscribe(Map<String,String[]> paramMap) throws RelationshipExistsException {
        WebTarget target = getWebTarget("subscribe");
        Form form = new Form();
        for(String key : paramMap.keySet()) {
            String[] params = paramMap.get(key);
            if(params.length <= 0)
                continue;
            
            form.param(key, params[0]);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, key, params[0]);
        }
        
        Response response = target.request(
                MediaType.APPLICATION_JSON_TYPE).post(
                        Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        
        
        if(response.getStatus() != Response.Status.OK.getStatusCode()) {
            
            String error = response.readEntity(String.class);
            throw new RuntimeException(error);
        }
        
        String confirmationKey = response.readEntity(String.class);
        
        return confirmationKey;
    }
    
    
    public void retriggerConfirmation(String confirmationKey) {
        WebTarget target = getWebTarget("/subscription/retriggerConfirmation");
        Form form = new Form();
        form.param("key", confirmationKey);
        
        Response response = target.request().put(
                        Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        
        if(response.getStatus() != Response.Status.OK.getStatusCode()) {    
            String error = response.readEntity(String.class);
            throw new RuntimeException(error);
        }
        
    }
}
