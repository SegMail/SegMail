/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.subscribe.webservice;

import eds.component.data.DataValidationException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import eds.component.data.RelationshipNotFoundException;
import eds.component.mail.InvalidEmailException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import seca2.bootstrap.module.Webservice.REST.RestSecured;
import segmail.component.subscription.ListService;
import segmail.component.subscription.SubscriptionException;
import segmail.component.subscription.SubscriptionService;
import segmail.component.subscription.mailmerge.MailMergeService;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;

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
    @EJB MailMergeService mmService;
    
    /**
     * Currently only allows subscription to 1 list only.
     * 
     * @param subscriptionMap
     * @return 
     * <ul>
     * <li>Response code 201: If the 
     */
    @Path("/subscribe")
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
            SubscriberAccount newSub = subscription.getSOURCE();
            SubscriptionList list = subscription.getTARGET();
            
            String redirect = list.generateConfirmUrl();
            if(redirect != null && !redirect.isEmpty()) {
                /*String redirectUrl = list.getREDIRECT_CONFIRM();
                if(!redirectUrl.startsWith("http://") && !redirectUrl.startsWith("https://"))
                    redirectUrl = "http://"+redirectUrl;
                */
                List<SubscriptionListField> fields = listService.getFieldsForSubscriptionList(listId);
                List<SubscriberFieldValue> fieldValues = subService.getSubscriberValuesBySubscriberObject(newSub);
                Map<Long,Map<String,String>> mmValues = mmService.createMMValueMap(newSub.getOBJECTID(), fields, fieldValues);
                redirect = mmService.parseSubscriberTags(redirect, mmValues.get(newSub.getOBJECTID()));
                
                return Response.status(Response.Status.TEMPORARY_REDIRECT).entity(redirect).build();
            }
            
            return Response.ok("ok").entity(subscription.getCONFIRMATION_KEY()).build();
            
        } catch (EntityNotFoundException ex) {
            Logger.getLogger(WSHttpSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (IncompleteDataException ex) {
            Logger.getLogger(WSHttpSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (SubscriptionException ex) {
            Logger.getLogger(WSHttpSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (RelationshipExistsException ex) {
            String confirmKey = ex.getMessage();
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(confirmKey).build();
            
        } catch (URISyntaxException ex) {
            Logger.getLogger(WSHttpSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }  
    }
    
    @Path("/retriggerConfirmation")
    @POST
    @RestSecured
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Response retriggerConfirmation(@FormParam("key")String key) {
        try {
            subService.retriggerConfirmation(key);
            
            return Response.ok(Response.Status.OK).entity(key).build();
            
        } catch (IncompleteDataException ex) {
            Logger.getLogger(WSHttpSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            return Response.ok(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (DataValidationException ex) {
            Logger.getLogger(WSHttpSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            return Response.ok(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        } catch (InvalidEmailException ex) {
            Logger.getLogger(WSHttpSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            return Response.ok(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }
    
    /**
     * 
     * @param listId
     * @param email
     * @return String that represent the results:
     * <ul>
     * <li>Success</li>
     * <li>Not_found: if Subscriber doesn't exist yet</li>
     * <li>System_error: if No confirmation email assigned, or Send As not set for list, or Invalid sender email</li>
     * <li>Error: if the subscriber's email is invalid</li>
     */
    @Path("segmail/retriggerConfirmation")
    @POST
    @RestSecured
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Response retriggerConfirmation(MultivaluedMap<String,String> subscriptionMap) {
        try {
            List<String> listIds = subscriptionMap.get("list");
            if(listIds == null || listIds.isEmpty())
                return Response.ok(Response.Status.OK).entity("Error : No list IDs provided.").build();
            
            long listId = Long.parseLong(listIds.get(0));
            
            List<String> emails = subscriptionMap.get("email");
            if(emails == null || emails.isEmpty() || emails.get(0) == null || emails.get(0).isEmpty())
                return Response.ok(Response.Status.OK).entity("Error : Please provide your email address that was registered with us.").build();
            
            subService.retriggerConfirmation(listId,emails.get(0));
            return Response.ok(Response.Status.OK).entity("Success").build();
        } catch (RelationshipNotFoundException ex) { //Subscriber doesn't exist yet
            Logger.getLogger(WSHttpSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            return Response.ok(Response.Status.OK).entity("Not_found : "+ex.getMessage()).build();
        } catch (IncompleteDataException ex) { //No confirmation email assigned or Send As not set for list
            Logger.getLogger(WSHttpSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            return Response.ok(Response.Status.OK).entity("System_error : "+ex.getMessage()).build();
        } catch (DataValidationException ex) { //Invalid sender email
            Logger.getLogger(WSHttpSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            return Response.ok(Response.Status.OK).entity("System_error : "+ex.getMessage()).build();
        } catch (InvalidEmailException ex) { //Invalid recipient email
            Logger.getLogger(WSHttpSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            return Response.ok(Response.Status.OK).entity("System_error : "+ex.getMessage()).build();
        }
        
    }
}
