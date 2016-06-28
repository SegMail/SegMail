/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.unsubscribe.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import seca2.bootstrap.module.Webservice.client.GenericWSInterface;

/**
 *
 * @author LeeKiatHaw
 */
@WebService(
        name = "WSUnsubscribe", 
        targetNamespace = "http://webservice.unsubscribe.subscribe.program.segmail/")
public interface WSUnsubscribeInterface extends GenericWSInterface {
    
    @WebMethod
    public String unsubscribe(@WebParam(name = "key") String key);
}
