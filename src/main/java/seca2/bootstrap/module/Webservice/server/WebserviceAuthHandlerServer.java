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
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
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

/**
 *
 * @author LeeKiatHaw
 */
public class WebserviceAuthHandlerServer implements SOAPHandler<SOAPMessageContext> {

    @EJB
    WebserviceService wsService;

    @Override
    public Set<QName> getHeaders() {
        return new TreeSet();
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        try {
            System.out.println("Server handler called");

            boolean success = authenticateWS(context);

            return success;

        } catch (SOAPException ex) {
            Logger.getLogger(WebserviceAuthHandlerServer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {

    }

    private boolean authenticateWS(SOAPMessageContext context) throws SOAPException {
        try {
            HttpServletRequest req = (HttpServletRequest) context.get(MessageContext.SERVLET_REQUEST);

            //String username = req.getHeader(WebserviceSOAPKeys.USERNAME);
            //String password = req.getHeader(WebserviceSOAPKeys.PASSWORD);
            
            SOAPMessage message = context.getMessage();
            SOAPHeader header = message.getSOAPHeader();
            
            QName headerUsername = new QName(WebserviceSOAPKeys.NAMESPACE,WebserviceSOAPKeys.USERNAME);
            String username = header.getAttributeValue(headerUsername);
            QName headerPassword = new QName(WebserviceSOAPKeys.NAMESPACE,WebserviceSOAPKeys.PASSWORD);
            String password = header.getAttributeValue(headerPassword);

            //Get the host of the application
            //More reliable to get from HTTP header than manually set it in SOAP header
            String ip = req.getRemoteAddr();

            wsService.authenticateApplication(username, password, ip);

            return true;

        } catch (UserAccountLockedException ex) {
            setSOAPFault(ex, context);
            return false;

        } catch (UserLoginException ex) {
            setSOAPFault(ex, context);
            return false;
        } 
    }

    private SOAPFault setSOAPFault(Exception ex, SOAPMessageContext context) throws SOAPException {
        Logger.getLogger(WebserviceAuthHandlerServer.class.getName()).log(Level.SEVERE, null, ex);

        SOAPBody body = context.getMessage().getSOAPBody();
        SOAPFault fault = body.addFault();

        QName faultName = new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, "Server");
        fault.setFaultCode(faultName);
        fault.setFaultActor(ex.getClass().getSimpleName());
        fault.setFaultString(ex.getMessage());

        return fault;
    }
}