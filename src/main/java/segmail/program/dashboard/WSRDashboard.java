/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.dashboard;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.joda.time.DateTime;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.bootstrap.module.Webservice.REST.RestSecured;
import segmail.component.subscription.reporting.LatestSubscribersContainer;
import segmail.component.subscription.reporting.SignupCountContainer;
import segmail.component.subscription.reporting.SubscriptionReportService;
import segmail.component.subscription.reporting.TotalSubscriptionContainer;
import segmail.component.subscription.reporting.UnsubscribeCountContainer;

/**
 *
 * @author LeeKiatHaw
 */
@Path("/dashboard")
public class WSRDashboard {
    
    @EJB SubscriptionReportService subService;
    
    // So that you cannot call this from other places, or change the clientId in 
    // your browser to sneak on other clients
    @Inject ClientContainer clientCont;
    
    @Path("/signups")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @RestSecured
    public Response signups(
            //@PathParam("clientId") String clientId, // take from ClientContainer
            @QueryParam("start") String start, 
            @QueryParam("end") String end) {
        
        long id = clientCont.getClient().getOBJECTID();
        DateTime startDate = DateTime.parse(start);
        DateTime endDate = DateTime.parse(end);
        
        SignupCountContainer subscriptions = subService.getSignupsByDate(id, startDate, endDate);
        
        return Response.ok(subscriptions.toJson()).build();
    }
    
    @Path("/unsubscribes")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @RestSecured
    public Response unsubscribes(
            //@PathParam("clientId") String clientId, // take from ClientContainer
            @QueryParam("start") String start, 
            @QueryParam("end") String end) {
        long id = clientCont.getClient().getOBJECTID();
        DateTime startDate = DateTime.parse(start);
        DateTime endDate = DateTime.parse(end);
        
        UnsubscribeCountContainer subscriptions = subService.getUnsubsByDate(id, startDate, endDate);
        
        return Response.ok(subscriptions.toJson()).build();
    }
    
    @Path("/totalsubcribers")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @RestSecured
    public Response totalsubcribers(
            //@PathParam("clientId") String clientId, // take from ClientContainer
            @QueryParam("start") String start, 
            @QueryParam("end") String end) {
        long id = clientCont.getClient().getOBJECTID();
        DateTime startDate = DateTime.parse(start);
        DateTime endDate = DateTime.parse(end);
        
        TotalSubscriptionContainer subscriptions = subService.getActiveSubscriptions(id, startDate, endDate);
        
        return Response.ok(subscriptions.toJson()).build();
    }
    
    @Path("/latestsubcribers")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @RestSecured
    public Response latestsubcribers(
            //@PathParam("clientId") String clientId, // take from ClientContainer
            @QueryParam("start") String start, 
            @QueryParam("end") String end,
            @QueryParam("n") int n
    ) {
        long id = clientCont.getClient().getOBJECTID();
        DateTime startDate = DateTime.parse(start);
        DateTime endDate = DateTime.parse(end);
        
        LatestSubscribersContainer cont = subService.getLatestSubscribers(id, startDate, endDate, n);
        
        return Response.ok(cont.toJson()).build();
    }
}
