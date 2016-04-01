/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.webservice;

import eds.component.user.UserAccountLockedException;
import eds.component.user.UserLoginException;
import eds.component.user.UserService;
import eds.entity.user.User;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import seca2.component.landing.LandingService;

/**
 * Also a SOAP handler
 *
 * @author LeeKiatHaw
 */
@Stateless
public class WebserviceService {

    @EJB
    UserService userService;
    @EJB
    LandingService landingService;

    @TransactionAttribute()
    public User authenticateApplication(String username, String password, String ipAddress)
            throws UserAccountLockedException, UserLoginException {

        try {
            //try to authenticate first
            User authenticatedUser = userService.login(username, password);

            //retrieve the user account linked to the ipAddress address
            User wsLinkedAccount = landingService.getUserFromServerAddress(ipAddress);

            //If either one is null or both are not the same, throw UserLoginException
            if (authenticatedUser == null
                    || wsLinkedAccount == null
                    || !authenticatedUser.equals(wsLinkedAccount)) {
                throw new UserLoginException("");
            }

            return authenticatedUser;

        } catch (UserLoginException ex) {
            //Catch it and change it to our message
            throw new UserLoginException("Webservice authentication failed.");
        }

    }
}
