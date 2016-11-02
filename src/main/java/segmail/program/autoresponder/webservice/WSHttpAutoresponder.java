/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.autoresponder.webservice;

import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.joda.time.DateTime;
import segmail.component.subscription.autoresponder.AutoresponderService;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;
import segmail.program.autoresponder.ProgramAutoresponder;

/**
 * REST Web Service
 * Nah...REST web services are meant for external apps, not within the ERP app.
 *
 * @author LeeKiatHaw
 */
@Path("autoresponder")
public class WSHttpAutoresponder {

    @Context
    private UriInfo context;
    
    @EJB AutoresponderService autoemailService;
    
    @Inject ProgramAutoresponder program;

    /**
     * Creates a new instance of WSHttpAutoresponder
     */
    public WSHttpAutoresponder() {
    }

    @Path("save")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveAutoemail(AutoresponderEmail email) {
        try {
            AutoresponderEmail autoemail = program.getEditingTemplate();
            
            autoemail.setBODY(email.getBODY());
            autoemail.setBODY_PROCESSED(email.getBODY_PROCESSED());
            
            autoemailService.saveAutoEmail(autoemail);
            
            //Return the time saved
            DateTime now = DateTime.now();
            
            return Response.status(Response.Status.OK).entity(now.toString()).build();
        } catch (IncompleteDataException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (EntityExistsException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (EntityNotFoundException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } catch (Throwable ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("mailmerge")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response parseMailmergeTag(String mailmergeTag) {
        return null;
    }
}
