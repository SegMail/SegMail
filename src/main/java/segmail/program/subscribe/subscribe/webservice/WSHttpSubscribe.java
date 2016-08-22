/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.subscribe.webservice;

import eds.component.batch.BatchProcessingException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import seca2.bootstrap.module.Webservice.REST.RestSecured;
import segmail.component.subscription.ListService;
import segmail.component.subscription.SubscriptionException;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;

/**
 * REST Web Service - accepts a request for a new subscription
 *
 * @author LeeKiatHaw
 */
@Path("/subscribe")
public class WSHttpSubscribe {

    @Context
    private UriInfo context;
    
    @EJB ListService listService;
    @EJB SubscriptionService subService;
    
    /**
     * 
     * @param subscriptionMap
     * @return 
     * <ul>
     * <li>Response code 201: If the 
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @RestSecured
    public Response subscribe(MultivaluedMap<String,String> subscriptionMap) {
        long listId = 0;
        long clientId = 0;
        try {
            List<String> listIds = subscriptionMap.get("list");
            if(listIds == null || listIds.isEmpty())
                throw new IncompleteDataException("No list IDs provided.");
            
            listId = Long.parseLong(listIds.get(0));
            
            List<String> clientIds = subscriptionMap.get("client");
            if(clientIds == null || clientIds.isEmpty())
                throw new IncompleteDataException("No client IDs provided.");
            
            clientId = Long.parseLong(clientIds.get(0));
            
            Map<String,Object> subscriberMap = new HashMap<>();
            for(String key : subscriptionMap.keySet()) {
                if(subscriptionMap.get(key) == null)
                    continue;
                
                List<String> values = subscriptionMap.get(key);
                if(values.isEmpty())
                    continue;
                
                subscriberMap.put(key, values.get(0));
            }
            
            
            //Get the list from listKey
            Subscription subscription = subService.subscribe(clientId, listId, subscriberMap, true);
            
            return Response.ok("ok").entity(subscription.getCONFIRMATION_KEY()).build();
            
        } catch (EntityNotFoundException ex) {
            Logger.getLogger(WSHttpSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
        } catch (IncompleteDataException ex) {
            Logger.getLogger(WSHttpSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (SubscriptionException ex) {
            Logger.getLogger(WSHttpSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (RelationshipExistsException ex) {
            List<String> hackForEmail = new ArrayList<>();
            for(List<String> values : subscriptionMap.values()) {
                String value = (values == null || values.isEmpty()) ? "" : values.get(0);
                hackForEmail.add(value);
            }
            List<Subscription> subscriptions = subService.getSubscriptionsByEmails(hackForEmail, listId);
            if(subscriptions == null || subscriptions.isEmpty())
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            return Response.status(Response.Status.OK).entity(subscriptions.get(0).getCONFIRMATION_KEY()).build();
            
        } 
    }
    
}
