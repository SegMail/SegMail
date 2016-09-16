/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.webservice;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.data.IncompleteDataException;
import eds.component.user.UserAccountLockedException;
import eds.component.user.UserLoginException;
import eds.component.user.UserService;
import eds.entity.data.EnterpriseData_;
import eds.entity.user.APIAccount;
import eds.entity.user.APIAccount_;
import eds.entity.user.User;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.xml.namespace.QName;
import org.joda.time.DateTime;
import seca2.bootstrap.module.Webservice.SOAP.client.GenericWSInterface;
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
    
    public static final String REST_PATH = "REST_PATH";
    @EJB
    UserService userService;
    @EJB
    LandingService landingService;
    @EJB
    GenericObjectService objService;
    @EJB
    UpdateObjectService updService;

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
  
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String issueNewToken(User user) {
        DateTime now = DateTime.now();
        List<APIAccount> accounts =  getAPIAccounts(user.getOBJECTID(),now,now);
        
        //If there is an existing API key, delimit it and create a new one
        if(accounts != null && !accounts.isEmpty()) {
            return accounts.get(0).getAPIKey();
        }
        
        //Create new API key
        APIAccount newAccount = new APIAccount();
        newAccount.setOWNER(user);
        objService.getEm().persist(newAccount);
        newAccount.generateKey();
        
        return newAccount.getAPIKey();
    }
    
    public List<APIAccount> getAPIAccounts(long userId, DateTime start, DateTime end) {
        
        java.sql.Date startDate = new java.sql.Date(start.getMillis());
        java.sql.Date endDate = new java.sql.Date(end.getMillis());
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<APIAccount> query = builder.createQuery(APIAccount.class);
        Root<APIAccount> fromAPI = query.from(APIAccount.class);
        
        query.select(fromAPI);
        query.where(
                builder.and(
                        builder.lessThanOrEqualTo(fromAPI.get(APIAccount_.START_DATE), endDate),
                        builder.greaterThanOrEqualTo(fromAPI.get(APIAccount_.END_DATE), startDate)
                )
        );
        
        query.orderBy(
                builder.desc(fromAPI.get(APIAccount_.START_DATE)),
                builder.desc(fromAPI.get(APIAccount_.END_DATE)),
                builder.desc(fromAPI.get(APIAccount_.SNO)));
        
        List<APIAccount> results = objService.getEm().createQuery(query)
                .getResultList();
        
        return results;
    }
    
    public String getRestPath() {
        String restPath = System.getProperty(REST_PATH);
        return restPath;
    }
    
    public List<APIAccount> getAPIAccounts(String token) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<APIAccount> query = builder.createQuery(APIAccount.class);
        Root<APIAccount> fromAPI = query.from(APIAccount.class);
        
        query.select(fromAPI);
        query.where(builder.equal(fromAPI.get(APIAccount_.APIKey), token));
        
        List<APIAccount> results = objService.getEm().createQuery(query)
                .getResultList();
        
        return results;
    }
    
    /**
     * Checks if the token is valid and if the user application that was issued 
     * the token comes from the same IP address that was assigned in LandingService
     * module.
     * 
     * @param token
     * @param ipAddress
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User authenticateApplicationToken(String token, String ipAddress) throws UserLoginException {
        
        List<APIAccount> accounts = this.getAPIAccounts(token);
        if(accounts == null || accounts.isEmpty())
            throw new UserLoginException("Invalid token.");
        
        APIAccount account = accounts.get(0);
        User user = account.getOWNER();
        
        //Get Server assigned to the account
        ServerInstance server = landingService.getServerFromUser(user.getOBJECTID());
        if(server == null)
            throw new UserLoginException("No servers assigned to user account.");
        
        if(ipAddress == null ||
                server.getIP_ADDRESS() == null ||
                !ipAddress.equals(server.getIP_ADDRESS()))
            throw new UserLoginException("Unauthorized IP address.");
        
        return user;
            
    }
}
