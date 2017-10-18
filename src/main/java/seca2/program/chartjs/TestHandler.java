/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.chartjs;

import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 *
 * @author LeeKiatHaw
 */
public class TestHandler implements SOAPHandler<SOAPMessageContext> {

    @Override
    public Set<QName> getHeaders() {
        //System.out.println("Server : getHeaders()......");
        return null;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        //System.out.println("Server : handleMessage()......");
        
        SOAPMessage soapMsg = context.getMessage();
        
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        //System.out.println("Server : handleFault()......");

        return true;
    }

    @Override
    public void close(MessageContext context) {
        //System.out.println("Server : close()......");
    }

}
