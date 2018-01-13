/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.data.EntityNotFoundException;
import eds.component.data.RelationshipNotFoundException;
import eds.component.transaction.TransactionService;
import eds.entity.mail.SentEmail;
import java.util.List;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.joda.time.DateTime;
import seca2.bootstrap.module.Webservice.REST.RestSecured;
import segmail.component.subscription.ListService;
import segmail.component.subscription.SubscriberService;
import segmail.component.subscription.SubscriptionService;
import segmail.component.subscription.event.SubscriberEvent;
import segmail.component.subscription.event.SubscriberEventAction;
import segmail.component.subscription.event.SubscriberTimelineService;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.subscription.SubscriberFieldValidationException;

/**
 * We wrote this because JSF cannot handle multi-nested maps in partial requests.
 * Somehow the value is always not set or it throw NullPointerExceptions somewhere.
 * And we realized the power and ease of REST so we will continue to use this 
 * more frequently in newer programs.
 * @author LeeKiatHaw
 */
@Path("/subscriber")
public class WSRSubscriber {
    
    @EJB SubscriptionService subService;
    @EJB GenericObjectService objService;
    @EJB UpdateObjectService updService;
    @EJB ListService listService;
    @EJB SubscriberService accService;
    @EJB SubscriberTimelineService tlService;
    @EJB TransactionService txService;
    
    /**
     * 
     * @param id SubscriberAccount.OBJECTID
     * @param fieldKey SubscriptionListField.generateKey()
     * @param fieldValue SubscriberFieldValue.VALUE
     * @return 
     */
    @Path("/field/update/{id}/{key}")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @RestSecured
    public Response updateField(
            @PathParam("id") String id, 
            @PathParam("key") String fieldKey, 
            @QueryParam("value") String fieldValue) {
        
        long subscriberId = Long.parseLong(id);
        if(subscriberId <= 0)
            throw new BadRequestException("subscriberId is required.");
        
        if(fieldKey == null || fieldKey.isEmpty())
            throw new BadRequestException("fieldKey is required.");
        
        String result = "";
        int count = 0;
        DateTime now = DateTime.now();
        try {
            count = subService.updateFieldValue(subscriberId, fieldKey, fieldValue, now);
            result = "success";
        } catch (EntityNotFoundException ex) {
            result = ex.getMessage();
        } catch (SubscriberFieldValidationException ex) {
            result = ex.getMessage();
        } catch (Exception ex) {
            result = ex.getMessage();
        }
        JsonObjectBuilder objBuilder = Json.createObjectBuilder();
        objBuilder.add("result", result);
        objBuilder.add("value", fieldValue);
        objBuilder.add("count", count);
        objBuilder.add("date_changed", now.toString("YYYY-MM-dd"));
        
        return Response.ok("ok").entity(objBuilder.build().toString()).build();
    }
    
    @Path("/timeline/build/{id}")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @RestSecured
    public Response buildTimeline(@PathParam("id") String idString) {
        try {
            long id = Long.parseLong(idString);
            
            List<SubscriberEvent> events = tlService.buildSubscriberTimeline(id);
            
            // Build the json object manually
            JsonObjectBuilder objBuilder = Json.createObjectBuilder();
            JsonArrayBuilder eventList = Json.createArrayBuilder();
            
            for(SubscriberEvent event : events) {
                JsonObjectBuilder eventObj = Json.createObjectBuilder();
                eventObj.add("title", event.title());
                eventObj.add("body", event.body());
                eventObj.add("datetime", event.datetime());
                eventObj.add("isoDatetime", event.isoDatetime());
                eventObj.add("icon", event.eventIcon());
                
                JsonArrayBuilder actions = Json.createArrayBuilder();
                for(SubscriberEventAction action : event.actions()) {
                    JsonObjectBuilder actionObj = Json.createObjectBuilder();
                    actionObj.add("href", action.getHref());
                    actionObj.add("text", action.getText());
                    actionObj.add("class", action.getHtmlClass());
                    
                    if(action.getDatamap() != null && !action.getDatamap().isEmpty()) {
                        JsonObjectBuilder dataMap = Json.createObjectBuilder();
                        for(String datakey : action.getDatamap().keySet()) {
                            //JsonObjectBuilder dataObj = Json.createObjectBuilder();
                            //dataObj.add(datakey, action.getDatamap().get(datakey));

                            dataMap.add(datakey, action.getDatamap().get(datakey));
                        }
                        actionObj.add("datamap", dataMap);
                    }
                    actions.add(actionObj);
                }
                eventObj.add("action", actions);
                
                eventList.add(eventObj);
            }
            
            objBuilder.add("events", eventList);
            
            return Response.ok(objBuilder.build().toString()).build();
            
        } catch (RelationshipNotFoundException ex) {
            throw new InternalServerErrorException(ex.getMessage());
        } catch (EntityNotFoundException ex) {
            throw new InternalServerErrorException(ex.getMessage());
        }
    }
    
    @Path("/preview/campaign/{id}")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @RestSecured
    public Response previewCampaign(@PathParam("id") String idString) {
        long id = Long.parseLong(idString);
        CampaignActivity act = objService.getEnterpriseObjectById(id, CampaignActivity.class);
        
        JsonObjectBuilder objBuilder = Json.createObjectBuilder();
        objBuilder.add("subject", act.getACTIVITY_NAME());
        objBuilder.add("body", act.getACTIVITY_CONTENT_PREVIEW());
        
        return Response.ok(objBuilder.build().toString()).build();
    }
    
    @Path("/preview/email/{key}")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @RestSecured
    public Response previewEmail(@PathParam("key") String key) {
        
        SentEmail email = txService.getTransactionByKey(key, SentEmail.class);
        
        JsonObjectBuilder objBuilder = Json.createObjectBuilder();
        objBuilder.add("subject", email.getSUBJECT());
        objBuilder.add("body", email.getBODY());
        
        return Response.ok(objBuilder.build().toString()).build();
    }
}
