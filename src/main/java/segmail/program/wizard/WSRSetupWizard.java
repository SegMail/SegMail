/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.wizard;

import eds.component.client.ClientAWSService;
import eds.component.data.DataValidationException;
import eds.component.data.EntityExistsException;
import eds.entity.client.VerifiedSendingAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.bootstrap.module.Webservice.REST.RestSecured;
import segmail.program.mysettings.FormVerifyNewAddress;

/**
 *
 * @author LeeKiatHaw
 */
@Path("/wizard")
public class WSRSetupWizard {
    
    @Inject ProgramSetupWizard program;
    
    @Inject ClientContainer cltCont;
    
    @EJB ClientAWSService cltAWS;
    
    @Path("/address")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @RestSecured
    public Response setupAddress(
            @FormParam("email") String email) {
        JsonObjectBuilder objBuilder = Json.createObjectBuilder();
        
        // Register new sending address if the address is new
        try {
            VerifiedSendingAddress newAdd = cltAWS.verifyNewSendingAddress(cltCont.getClient(), email.trim(), true);

            program.getExistingAddresses().add(newAdd); //Ensure whatever that was created here is added to the existing list
            program.updateMapValue(program.getTAB_ADDRESS(), program.getKEY_COMPLETED(), true); //Update page progress

            objBuilder.add("result", "success");
            objBuilder.add("message", "Sending address submitted, please check your email!");

        } catch (DataValidationException ex) {
            Logger.getLogger(WSRSetupWizard.class.getName()).log(Level.SEVERE, null, ex);
            throw new InternalServerErrorException(ex.getMessage());
        } catch (EntityExistsException ex) {
            //If the address already exist then don't do anything
            objBuilder.add("result", "info");
            objBuilder.add("message", "Sending address already submitted.");
        } finally {

        }
        program.setAddress(email); //Just in case we need to do a partial request back to the page.
        
        return Response.ok("ok").entity(objBuilder.build().toString()).build();
    }
}
