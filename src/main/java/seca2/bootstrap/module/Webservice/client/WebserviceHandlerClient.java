/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Webservice.client;

import eds.component.webservice.WebserviceSOAPKeys;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 *
 * @author LeeKiatHaw
 */
public class WebserviceHandlerClient implements SOAPHandler<SOAPMessageContext>{

    private String username = "sws";
    private String password = "sws";
    
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
            Logger.getLogger(WebserviceHandlerClient.class.getName()).log(Level.SEVERE, null, ex);
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
        header.addAttribute(headerUsername, username);
        QName headerPassword = new QName(WebserviceSOAPKeys.NAMESPACE,WebserviceSOAPKeys.PASSWORD);
        header.addAttribute(headerPassword, password);
        
        message.saveChanges();
    }
}
