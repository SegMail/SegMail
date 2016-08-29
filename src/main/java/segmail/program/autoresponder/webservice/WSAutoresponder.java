/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.autoresponder.webservice;

import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.ws.Response;
import org.joda.time.DateTime;
import segmail.component.subscription.autoresponder.AutoresponderService;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;
import segmail.program.autoresponder.ProgramAutoresponder;

/**
 *
 * @author LeeKiatHaw
 */
@WebService(serviceName = "WSAutoresponder")
@HandlerChain(file = "handlers-server.xml")
public class WSAutoresponder {

    @EJB AutoresponderService autoemailService;
    
    @Inject ProgramAutoresponder program;
    /**
     * This is a sample web service operation
     * @param body
     * @param bodyProcessed
     * @throws eds.component.data.EntityNotFoundException
     * @throws eds.component.data.IncompleteDataException
     * @throws eds.component.data.EntityExistsException
     */
    @WebMethod(operationName = "saveAutoemail")
    public String saveAutoemail(
            @WebParam(name = "body") String body,
            @WebParam(name = "bodyProcessed") String bodyProcessed) 
            throws EntityNotFoundException, IncompleteDataException, EntityExistsException {
        
        AutoresponderEmail autoemail = program.getEditingTemplate();
        if(autoemail == null)
            throw new EntityNotFoundException("No AutoresponderEmail found.");
        
        autoemail.setBODY(body);
        autoemail.setBODY_PROCESSED(bodyProcessed);
        
        autoemail = autoemailService.saveAutoEmail(autoemail);
        
        program.setEditingTemplate(autoemail);
        
        DateTime now = DateTime.now();
        
        return "Updated "+now.toString("dd-MM-yyyy hh:mm:ss");
    }
}
