/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.subscribe.webservice;

import java.util.List;
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
import segmail.component.subscription.ListService;
import segmail.component.subscription.SubscriptionService;

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
    
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response subscribe(MultivaluedMap<String,String> subscriberMap) {
        
        //try {
            List<String> listIds = subscriberMap.get("list");
            if(listIds == null || listIds.isEmpty())
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            
            long listId = Long.parseLong(listIds.get(0));
            
            List<String> clientIds = subscriberMap.get("client");
            if(clientIds == null || clientIds.isEmpty())
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            
            long clientId = Long.parseLong(clientIds.get(0));
            //Get the list from listKey
            //subService.subscribe(clientId, listId, subscriberMap, true);
            
            return Response.ok("ok").build();
            
        /*} catch (EntityNotFoundException ex) {
            Logger.getLogger(WSHttpSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (IncompleteDataException ex) {
            Logger.getLogger(WSHttpSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            return Response.ok(ex.getMessage()).build();
        } catch (SubscriptionException ex) {
            Logger.getLogger(WSHttpSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            return Response.ok(ex.getMessage()).build();
        } catch (BatchProcessingException ex) {
            Logger.getLogger(WSHttpSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            return Response.ok(ex.getMessage()).build();
        } catch (RelationshipExistsException ex) {
            Logger.getLogger(WSHttpSubscribe.class.getName()).log(Level.SEVERE, null, ex);
            return Response.ok(ex.getMessage()).build();
        }*/
    }
}
