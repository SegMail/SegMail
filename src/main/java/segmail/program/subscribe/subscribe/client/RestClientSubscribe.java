/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.subscribe.client;

import eds.component.data.IncompleteDataException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import seca2.bootstrap.module.Webservice.REST.client.GenericRestClient;
import seca2.component.landing.LandingServerGenerationStrategy;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;
import seca2.entity.landing.ServerInstance;

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
    
    public String subscribe(Map<String,String[]> paramMap) {
        WebTarget target = getWebTarget("subscribe");
        Form form = new Form();
        for(String key : paramMap.keySet()) {
            String[] params = paramMap.get(key);
            if(params.length <= 0 || params[0] == null || params[0].isEmpty())
                continue;
            
            form.param(key, params[0]);
        }
        
        Response response = target.request(
                MediaType.APPLICATION_JSON_TYPE).post(
                        Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        String listname = response.readEntity(String.class);
        
        return listname;
    }
    
}
