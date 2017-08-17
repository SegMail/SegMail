/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.account;

import eds.component.GenericObjectService;
import eds.component.client.ClientAWSService;
import eds.component.client.ClientService;
import eds.component.data.DataValidationException;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import eds.component.transaction.TransactionNotFoundException;
import eds.component.transaction.TransactionService;
import eds.component.user.PWD_PROCESSING_STATUS;
import eds.component.user.UserService;
import eds.entity.client.ClientAWSAccount;
import eds.entity.client.ClientType;
import eds.entity.client.ClientUserAssignment;
import eds.entity.client.ContactInfo;
import eds.entity.user.PasswordResetRequest;
import eds.entity.user.Trigger_Password_User;
import eds.entity.user.User;
import eds.entity.user.UserAccount;
import eds.entity.user.UserType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import seca2.bootstrap.module.Webservice.REST.RestSecured;
import seca2.component.landing.LandingServerGenerationStrategy;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;
import seca2.entity.landing.ServerInstance;
import segmail.component.subscription.ListService;
import segmail.component.subscription.SubscriptionException;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.SUBSCRIPTION_STATUS;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;

/**
 * This is a RESTful service endpoints for all signup services.
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
    @EJB ClientAWSService clientAWSService;
    @EJB LandingService landingService;
    @EJB GenericObjectService objService;
    @EJB TransactionService txService;
    
    /**
     * 1) Create the UserAccount (username, password, email)
     * 2) Create the ClientAccount and ClientAWSAccount
     * 3) Subscribe to the Segmail list (email, listid, clientid)
     * 
     * If any one of the parameters are not available, skip the step.
     * 
     * @param subscriptionMap
     * @return
     * @throws IncompleteDataException 
     */
    @Path("segmail/create")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @RestSecured
    public String createSegmailUserAccount(
            MultivaluedMap<String,String> subscriptionMap) throws IncompleteDataException {
        
        //For account creation
        final String usernameKey = "username";
        final String passwordKey = "password";
        final String emailkey = "email";
        String username = subscriptionMap.getFirst(usernameKey);
        String password = subscriptionMap.getFirst(passwordKey);
        String email = subscriptionMap.getFirst(emailkey);
        
        //For subscription to list
        final String listKey = "list";
        final String clientKey = "client";
        long listId = Long.parseLong(subscriptionMap.getFirst(listKey));
        long clientId = Long.parseLong(subscriptionMap.getFirst(clientKey));
        
        //Register user account and client account
        if(username != null && !username.isEmpty()) {
            UserAccount userAccount = null;
            try {
                long userTypeId = userService.getUserTypeByName(SEGMAIL_USER_ACCOUNT_NAME).get(0).getOBJECTID();
                userAccount = userService.registerUserByUserTypeId(userTypeId, username, password, email);
            } catch (EntityNotFoundException | DataValidationException ex) {
                Logger.getLogger(ClientAccountService.class.getName()).log(Level.SEVERE, null, ex);
                return "Error : "+ex.getMessage();
            } catch (EntityExistsException ex) {
                Logger.getLogger(ClientAccountService.class.getName()).log(Level.SEVERE, null, ex);
                userAccount = userService.getUserAccountByUsername(username);
            }
            
            ClientUserAssignment clientUserAssign = null;
            try {
                long clientTypeId = clientService.getClientTypeByName(SEGMAIL_CLIENT_TYPE).getOBJECTID();
                clientUserAssign = clientService.registerClientForUser(userAccount.getOWNER(), clientTypeId);
            } catch (EntityNotFoundException ex) {
                Logger.getLogger(ClientAccountService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (EntityExistsException ex) {
                Logger.getLogger(ClientAccountService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RelationshipExistsException ex) {
                Logger.getLogger(ClientAccountService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Subscribe
        if(listId > 0) {
            Map<String,Object> subscriberMap = new HashMap<>();
            for(String key : subscriptionMap.keySet()) {
                if(subscriptionMap.get(key) == null)
                    continue;
                
                List<String> values = subscriptionMap.get(key);
                if(values.isEmpty())
                    continue;
                
                subscriberMap.put(key, values.get(0));
            }
            try {
                Subscription newSubsc = subService.subscribe(clientId, listId, subscriberMap, true);
                
                //If there is a redirect link, return it
                SubscriptionList list = newSubsc.getTARGET();
                if(list.getREDIRECT_CONFIRM()!= null && !list.getREDIRECT_CONFIRM().isEmpty()) {
                    String redirectUrl = list.getREDIRECT_CONFIRM();
                    if(!redirectUrl.startsWith("http://") && !redirectUrl.startsWith("https://"))
                        redirectUrl = "http://"+redirectUrl;

                    return "Redirect : "+redirectUrl;
                }
                
            } catch (EntityNotFoundException ex) {
                Logger.getLogger(ClientAccountService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SubscriptionException ex) {
                Logger.getLogger(ClientAccountService.class.getName()).log(Level.SEVERE, null, ex);
                return "Error : "+ex.getMessage();
            } catch (RelationshipExistsException ex) {
                Logger.getLogger(ClientAccountService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return "Success";
    }
         
    @Path("segmail/check")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @RestSecured
    public String checkAccount(MultivaluedMap<String,String> accountMap) {
        final String emailKey = "email";
        final String usernameKey = "username";
        final String clientKey = "client";
        
        String email = accountMap.getFirst(emailKey);
        String username = accountMap.getFirst(usernameKey);
        String client = accountMap.getFirst(clientKey);
        long listId = Long.parseLong(accountMap.getFirst("list"));
        
        JsonObjectBuilder resultObjectBuilder = Json.createObjectBuilder();
        
        if(email != null && !email.isEmpty()) {
            List<Subscription> subcs = subService.getSubscriptions(email, listId, null);
            if(subcs == null || subcs.isEmpty()) {
                //resultObjectBuilder.addNull(emailKey); //Don't put anything
            } else { // Let the client decide the message to return to the user
                resultObjectBuilder.add(emailKey, subcs.get(0).getSTATUS());
            }
        }
        
        if(username != null && !username.isEmpty()) {
            Boolean userExists = userService.checkUsernameExist(username);
            resultObjectBuilder.add(usernameKey, userExists.toString());
        }
        
        if(client != null && !client.isEmpty()) {
            Boolean clientExists = !(clientService.getClientByClientname(client) == null);
            resultObjectBuilder.add(clientKey, clientExists.toString());
        }
        
        return resultObjectBuilder.build().toString();
    }
    
    /**
     * Initialize the password reseting process
     * 
     * @param email
     * @return 
     */
    @Path("segmail/reset/init")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @RestSecured
    public String resetLoginAccountInit(@QueryParam("email") String email) {
        
        try {
            String token = userService.generatePasswordResetToken(email);
            
            return token;
            
        } catch (IncompleteDataException ex) {
            Logger.getLogger(ClientAccountService.class.getName()).log(Level.SEVERE, null, ex);
            return "Error:No servers setup yet, please contact your administrators.";
        } catch (EntityNotFoundException ex) {
            Logger.getLogger(ClientAccountService.class.getName()).log(Level.SEVERE, null, ex);
            return "Error:"+ex.getMessage();
        }
    }
    
    /**
     * 
     * @param token
     * @return 
     */
    @Path("segmail/reset/retrieve")
    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @RestSecured
    public String retrieveRequest(@QueryParam("token") String token) {
        PasswordResetRequest req = txService.getTransactionByKey(token, PasswordResetRequest.class);
        if(PWD_PROCESSING_STATUS.PROCESSED.label.equals(req.getPROCESSING_STATUS()))
            throw new ForbiddenException("This request has been processed. Please create another one.");
        
        return "Valid";
    }
    
    
    /**
     * 
     * @param token
     * @param password
     * @return 
     */
    @Path("segmail/reset/password")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @RestSecured
    public String resetPassword(
            @QueryParam("token") String token,
            @QueryParam("password") String password) {
        
        try {
            userService.resetPassword(token, password);
            
            return "Success";
        } catch (TransactionNotFoundException ex) {
            Logger.getLogger(ClientAccountService.class.getName()).log(Level.SEVERE, null, ex);
            throw new ForbiddenException();
        }
        
    }
    
}
