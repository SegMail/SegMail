/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.unsubscribe.client;

import eds.component.data.RelationshipNotFoundException;
import eds.component.webservice.TransactionProcessedException;
import eds.component.webservice.UnwantedAccessException;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import seca2.bootstrap.module.Webservice.SOAP.client.GenericWSInterface;

/**
 *
 * @author LeeKiatHaw
 */
@WebService(
        name = "WSUnsubscribe", 
        targetNamespace = "http://webservice.unsubscribe.subscribe.program.segmail/")
public interface WSUnsubscribeInterface extends GenericWSInterface {
    
    /**
     * 
     * @param key
     * @return
     * @throws UnwantedAccessException if the key is not provided or doesn't match with any Subscription
     * records at the server.
     */
    @WebMethod
    public String unsubscribe(@WebParam(name = "key") String key) 
            throws UnwantedAccessException;
}
