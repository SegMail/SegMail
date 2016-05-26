/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.webservice;

import eds.component.data.IncompleteDataException;
import eds.component.user.UserAccountLockedException;
import eds.component.user.UserLoginException;
import eds.component.user.UserService;
import eds.entity.user.User;
import java.net.MalformedURLException;
import java.net.URL;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.namespace.QName;
import seca2.bootstrap.module.Webservice.client.GenericWSInterface;
import seca2.component.landing.LandingServerGenerationStrategy;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;
import seca2.entity.landing.ServerInstance;

/**
 * Also a SOAP handler
 *
 * @author LeeKiatHaw
 */
@Stateless
public class WebserviceService {

    public static final String PORT_APPEND = "Port";
    public static final String WSDL_APPEND = "?wsdl";
    @EJB
    UserService userService;
    @EJB
    LandingService landingService;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User authenticateApplication(String username, String password, String serverName)
            throws UserAccountLockedException, UserLoginException {

        //try {
            //try to authenticate first
            User authenticatedUser = userService.login(username, password);

            //retrieve the user account linked to the ipAddress address
            //User wsLinkedAccount = landingService.getUserFromServerAddress(ipAddress);
            User wsLinkedAccount = landingService.getUserFromServerName(serverName);

            //If either one is null or both are not the same, throw UserLoginException
            if (authenticatedUser == null
                    || wsLinkedAccount == null
                    || !authenticatedUser.equals(wsLinkedAccount)) {
                throw new UserLoginException("Unmatched webservice and user accounts.");
            }
            
            if (!wsLinkedAccount.getUSERTYPE().isWS_ACCESS())
                throw new UserLoginException("User account has no webservice access.");

            return authenticatedUser;

        /*} catch (UserLoginException ex) {
            //Catch it and change it to our message
            throw new UserLoginException("Webservice authentication failed.");
        }*/

    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getNextWSEndpointURL(String endpointName) 
            throws IncompleteDataException{
        
        if(endpointName == null || endpointName.isEmpty())
            throw new IncompleteDataException("Endpoint cannot be empty.");
        
        ServerInstance endpointServer = landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN, ServerNodeType.ERP);
        String endpointURL = endpointServer.getURI();
        //Append endpoint name to url
        endpointURL = endpointURL.concat("/").concat(endpointName).concat(WSDL_APPEND);
        
        return endpointURL;
    }
    
    public <WS extends GenericWSInterface> WS getWSProvider(String endpointName, String namespace, Class<WS> ws) 
            throws IncompleteDataException, MalformedURLException {
        
        String endpointURL = getNextWSEndpointURL(endpointName);
        URL wsdlLocaton = new URL(endpointURL);
        QName serviceName = new QName(namespace, endpointName);
        QName portName = new QName(namespace, endpointName.concat(PORT_APPEND));
        
        
        GenericWSProvider wsProvider = new GenericWSProvider(wsdlLocaton,serviceName,portName);
        return wsProvider.getPort(ws);
    }
}
