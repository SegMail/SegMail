/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import java.util.List;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import seca2.bootstrap.module.Webservice.REST.RestSecured;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.SubscriberFieldValue;

/**
 * We wrote this because JSF cannot handle multi-nested maps in partial requests.
 * Somehow the value is always not set or it throw NullPointerExceptions somewhere.
 * 
 * @author LeeKiatHaw
 */
@Path("/subscriber")
public class WSRSubscriber {
    
    @EJB SubscriptionService subService;
    @EJB GenericObjectService objService;
    @EJB UpdateObjectService updService;
    
    @Path("/field/update/{id}/{key}/{value}")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @RestSecured
    public Response updateField(
            @PathParam("id") String id, 
            @PathParam("key") String fieldKey, 
            @PathParam("value") String fieldValue) {
        /*if(form.get("subscriberId") == null || form.get("subscriberId").isEmpty())
            throw new BadRequestException("subscriberId is required.");
        long subscriberId = Long.parseLong(form.get("subscriberId").get(0));
        */
        long subscriberId = Long.parseLong(id);
        if(subscriberId <= 0)
            throw new BadRequestException("subscriberId is required.");
        
        /*if(form.get("fieldKey") == null || form.get("fieldKey").isEmpty())
            throw new BadRequestException("fieldKey is required.");
        String fieldKey = form.get("fieldKey").get(0);
        */
        if(fieldKey == null)
            throw new BadRequestException("fieldKey is required.");
        
        /*if(form.get("fieldValue") == null || form.get("fieldValue").isEmpty())
            throw new BadRequestException("fieldValue is required.");
        String fieldValue = form.get("fieldValue").get(0);
        */
        int count = subService.updateFieldValue(subscriberId, fieldKey, fieldValue);
        JsonObjectBuilder objBuilder = Json.createObjectBuilder();
        objBuilder.add("result", "success");
        objBuilder.add("count", count);
        
        return Response.ok("ok").entity(objBuilder.build().toString()).build();
    }
}
