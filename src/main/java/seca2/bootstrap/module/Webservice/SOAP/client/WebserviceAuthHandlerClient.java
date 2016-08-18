/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Webservice.SOAP.client;

import eds.component.webservice.WebserviceSOAPKeys;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import seca2.component.landing.LandingService;

/**
 *
 * @author LeeKiatHaw
 */
public class WebserviceAuthHandlerClient implements SOAPHandler<SOAPMessageContext>{

    /**
     * You cannot store these here!
     */
    //private String username = "sws";
    //private String password = "sws";
    
    public static final String USERNAME_KEY = "WS_CLIENT_USERNAME";
    public static final String PASSWORD_KEY = "WS_PASSWORD_KEY";
    
    @EJB LandingService landingService = new LandingService(); //No EJB context here!!! WTF!!!
    
    @Override
    public Set<QName> getHeaders() {
        return new TreeSet();
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        try {
            System.out.println("Client handler called");
            
            insertCredentials(context);
            
            return true;
            
        } catch (SOAPException ex) {
            Logger.getLogger(WebserviceAuthHandlerClient.class.getName()).log(Level.SEVERE, null, ex);
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
    
    /**
     * Inserts credentials into the HTTP headers for the server to authenticate
     * 
     * @param context 
     */
    private void insertCredentials(SOAPMessageContext context) throws SOAPException {
        HttpServletRequest req = (HttpServletRequest) context.get(MessageContext.SERVLET_REQUEST);
        
        SOAPMessage message = context.getMessage();
        SOAPHeader header = message.getSOAPHeader();
        
        QName headerUsername = new QName(WebserviceSOAPKeys.NAMESPACE,WebserviceSOAPKeys.USERNAME);
        String username = System.getProperty(USERNAME_KEY);
        header.addAttribute(headerUsername, username);
        QName headerPassword = new QName(WebserviceSOAPKeys.NAMESPACE,WebserviceSOAPKeys.PASSWORD);
        String password = System.getProperty(PASSWORD_KEY);
        header.addAttribute(headerPassword, password);
        QName serverName = new QName(WebserviceSOAPKeys.NAMESPACE,WebserviceSOAPKeys.SERVER_NAME);
        String server = landingService.getOwnServerName();
        header.addAttribute(serverName, server);
        
        message.saveChanges();
    }
}
