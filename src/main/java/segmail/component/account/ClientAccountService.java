/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.account;

import eds.component.client.ClientService;
import eds.component.data.DataValidationException;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import eds.component.user.UserService;
import eds.entity.client.Client;
import eds.entity.client.ClientType;
import eds.entity.client.ClientUserAssignment;
import eds.entity.client.ContactInfo;
import eds.entity.user.UserAccount;
import eds.entity.user.UserType;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import seca2.component.landing.LandingServerGenerationStrategy;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;
import seca2.entity.landing.ServerInstance;
import segmail.component.subscription.ListService;
import segmail.component.subscription.SubscriptionService;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
@Path("account")
public class ClientAccountService {
    
    private final String SEGMAIL_USER_ACCOUNT_NAME = "Segmail User";
    private final String SEGMAIL_CLIENT_TYPE = "Segmail Client";
    
    @EJB UserService userService;
    @EJB ListService listService;
    @EJB SubscriptionService subService;
    @EJB ClientService clientService;
    @EJB LandingService landingService;
    
    @Path("segmail")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SegmailAccountContainer createSegmailAccount(SegmailAccountContainer newAccount) 
            throws EntityNotFoundException, IncompleteDataException, EntityExistsException, RelationshipExistsException {
        //Create user account
        List<UserType> userTypes = userService.getUserTypeByName(SEGMAIL_USER_ACCOUNT_NAME);
        if(userTypes == null || userTypes.isEmpty())
            throw new EntityNotFoundException(SEGMAIL_USER_ACCOUNT_NAME+" type not created yet.");
        long sUserTypeId = userTypes.get(0).getOBJECTID();
        
        UserAccount segmailAccount = userService.registerUserByUserTypeId(sUserTypeId, newAccount.getEmail(), newAccount.getPassword());
        
        //Create Client
        ClientType clientType = clientService.getClientTypeByName(SEGMAIL_CLIENT_TYPE);
        if(clientType == null)
            throw new EntityNotFoundException(SEGMAIL_CLIENT_TYPE+" type not created yet.");
        long clientId = clientType.getOBJECTID();
        
        ClientUserAssignment clientAssign = clientService.registerClientForUser(segmailAccount.getOWNER(), clientId);
        
        //If help is true, send admin an email 
        //or use SubscriberFieldValue to store it and MailTriggers(new module) to trigger
        
        
        newAccount.setClientAssignment(clientAssign);
        
        return newAccount;
    }
    
    @Path("segmail/create")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String createSegmailUserAccount(
            MultivaluedMap<String,String> subscriptionMap) throws IncompleteDataException {
        
        //To be used in case of EntityExistsException or RelationshipExistsException
        ServerInstance server = landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN, ServerNodeType.ERP);
        try {
            //Create user account
            List<UserType> userTypes = userService.getUserTypeByName(SEGMAIL_USER_ACCOUNT_NAME);
            if(userTypes == null || userTypes.isEmpty())
                throw new EntityNotFoundException(SEGMAIL_USER_ACCOUNT_NAME+" type not created yet.");
            long sUserTypeId = userTypes.get(0).getOBJECTID();
            
            UserAccount segmailAccount = userService.registerUserByUserTypeId(sUserTypeId, subscriptionMap.getFirst("username"), subscriptionMap.getFirst("password"));
            
            //Create Client
            ClientType clientType = clientService.getClientTypeByName(SEGMAIL_CLIENT_TYPE);
            if(clientType == null)
                throw new EntityNotFoundException(SEGMAIL_CLIENT_TYPE+" type not created yet.");
            long clientId = clientType.getOBJECTID();
            
            ClientUserAssignment clientAssign = clientService.registerClientForUser(segmailAccount.getOWNER(), clientId);
            
            //Update Client attribute
            ContactInfo newContactInfo = new ContactInfo();
            newContactInfo.setEMAIL(subscriptionMap.getFirst("email") == null ? "" : subscriptionMap.getFirst("email"));
            newContactInfo.setFIRSTNAME(subscriptionMap.getFirst("firstname") == null ? "" : subscriptionMap.getFirst("firstname"));
            newContactInfo.setLASTNAME(subscriptionMap.getFirst("lastname") == null ? "" : subscriptionMap.getFirst("lastname"));
            newContactInfo.setOWNER(clientAssign.getSOURCE());
            clientService.createClientContact(newContactInfo);
            
            return Long.toString(clientAssign.getSOURCE().getOBJECTID());
        } catch (EntityNotFoundException ex) {
            Logger.getLogger(ClientAccountService.class.getName()).log(Level.SEVERE, null, ex);
            return "Error : "+ex.getMessage();
        } catch (IncompleteDataException ex) {
            Logger.getLogger(ClientAccountService.class.getName()).log(Level.SEVERE, null, ex);
            return "Error : "+ex.getMessage();
        } catch (EntityExistsException | RelationshipExistsException ex) {
            Logger.getLogger(ClientAccountService.class.getName()).log(Level.SEVERE, null, ex);
            return "Exist : Your account was already created. Please check your email for our confirmation emails and visit <a target=\"_blank\" href=\""+server.getURI()+"\">Our login page</a>";
        } 
    }
            
}
