/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.confirm.client;

import javax.xml.ws.WebFault;
import segmail.program.subscribe.confirm.webservice.UnwantedAccessException;

/**
 *
 * @author LeeKiatHaw
 */
//@WebFault(name="UnwantedAccessException",targetNamespace="http://client.confirm.subscribe.program.segmail/")
public class UnwantedAccessException_Exception extends Exception {

    private final UnwantedAccessException faultInfo;

    public UnwantedAccessException_Exception(UnwantedAccessException faultInfo, String message) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public UnwantedAccessException_Exception(UnwantedAccessException faultInfo, String message, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    public UnwantedAccessException getFaultInfo() {
        return faultInfo;
    }
    
    
}
