/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Webservice.REST.server;

import eds.component.user.UserAccountLockedException;
import eds.component.user.UserLoginException;
import eds.component.webservice.WebserviceService;
import eds.entity.user.User;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author LeeKiatHaw
 */
@Path("authentication")
public class RestServerAuthEndpoint {
    
    @EJB WebserviceService wsEJBService;
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@FormParam("username") String username, //must be the same as WebserviceSOAPKeys
                                     @FormParam("password") String password,
                                     @FormParam("servername") String servername) {

        try {

            // Authenticate the user using the credentials provided
            User user = authenticate(username, password, servername);

            // Issue a token for the user
            String token = issueToken(user);

            // Return the token on the response
            return Response.ok(token).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e).build();
        }      
    }

    private User authenticate(String username, String password, String servername) 
            throws UserAccountLockedException, UserLoginException {
        // Authenticate against a database, LDAP, file or whatever
        // Throw an Exception if the credentials are invalid
        User user = wsEJBService.authenticateApplication(username, password, servername);
        return user;
    }

    private String issueToken(User user) {
        // Issue a token (can be a random String persisted to a database or a JWT token)
        // The issued token must be associated to a user
        // Return the issued token
        String token = wsEJBService.issueNewToken(user);
        
        return token;
    }
}
