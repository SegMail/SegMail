/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Webservice.server;

import eds.component.user.UserAccountLockedException;
import eds.component.user.UserLoginException;
import eds.component.webservice.WebserviceSOAPKeys;
import eds.component.webservice.WebserviceService;
import eds.entity.user.User;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import seca2.bootstrap.UserSessionContainer;

/**
 *
 * @author LeeKiatHaw
 */
public class WebserviceAuthHandlerServer implements SOAPHandler<SOAPMessageContext> {

    @EJB
    WebserviceService wsService;
    
    @Inject UserSessionContainer sessionContainer;

    @Override
    public Set<QName> getHeaders() {
        return new TreeSet();
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        try {
            System.out.println("Server handler called");
            

            boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            boolean success = (outbound) ? true : authenticateWS(context);

            return success;

        } catch (SOAPException ex) {
            Logger.getLogger(WebserviceAuthHandlerServer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (UserAccountLockedException ex) {
            Logger.getLogger(WebserviceAuthHandlerServer.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (UserLoginException ex) {
            Logger.getLogger(WebserviceAuthHandlerServer.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        /*try {
            SOAPMessage message = context.getMessage();
            SOAPBody body = message.getSOAPBody();
            SOAPFault fault = body.getFault();
            String code = fault.getFaultCode();
            String faultString = fault.getFaultString();
            
        } catch (SOAPException ex) {
            Logger.getLogger(WebserviceAuthHandlerServer.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        return true;
    }

    @Override
    public void close(MessageContext context) {

    }

    private boolean authenticateWS(SOAPMessageContext context)
            throws SOAPException, UserAccountLockedException, UserLoginException {

        //If the request comes from the an authenticated session, no need to authenticate
        if(sessionContainer.isLoggedIn())
            return true;
        
        HttpServletRequest req = (HttpServletRequest) context.get(MessageContext.SERVLET_REQUEST);

            //String username = req.getHeader(WebserviceSOAPKeys.USERNAME);
        //String password = req.getHeader(WebserviceSOAPKeys.PASSWORD);
        SOAPMessage message = context.getMessage();
        SOAPHeader header = message.getSOAPHeader();

        /**
         * Raised a bug: https://github.com/SegMail/SegMail/issues/53 We will
         * fix this namespace issue after the launch, for the future SegERP.
         */
            //QName headerUsername = new QName(WebserviceSOAPKeys.NAMESPACE,WebserviceSOAPKeys.USERNAME);
        //String username = header.getAttributeValue(headerUsername);
        String username = header.getAttribute(WebserviceSOAPKeys.USERNAME);
            //QName headerPassword = new QName(WebserviceSOAPKeys.NAMESPACE,WebserviceSOAPKeys.PASSWORD);
        //String password = header.getAttributeValue(headerPassword);
        String password = header.getAttribute(WebserviceSOAPKeys.PASSWORD);
        
        String server = header.getAttribute(WebserviceSOAPKeys.SERVER_NAME);
        
        if(username == null || username.isEmpty()
                || password == null || password.isEmpty())
            throw new UserLoginException("No username or password provided. Please log in again.");

            //Get the host of the application
        //More reliable to get from HTTP header than manually set it in SOAP header
        String ip = req.getRemoteAddr();

        User authenticatedUser = wsService.authenticateApplication(username, password, server);
        sessionContainer.setSessionId(req.getRequestedSessionId());
        sessionContainer.setUser(authenticatedUser);
        sessionContainer.setLoggedIn(true);
        sessionContainer.setUserType(authenticatedUser.getUSERTYPE());

        return true;

    }
    

    private SOAPFault setSOAPFault(Exception ex, SOAPMessageContext context) throws SOAPException {
        Logger.getLogger(WebserviceAuthHandlerServer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);

        SOAPBody body = context.getMessage().getSOAPBody();
        SOAPFault fault = body.addFault();

        //QName faultName = new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, "Server");
        QName faultName = new QName(WebserviceSOAPKeys.NAMESPACE, ex.getClass().getSimpleName());
        fault.setFaultCode(faultName);
        fault.setFaultActor(ex.getClass().getSimpleName());
        fault.setFaultString(ex.getMessage());

        return fault;
    }
}
