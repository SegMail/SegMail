/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.unsubscribe.webservice;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.HandlerChain;
import javax.jws.WebParam;
import javax.jws.WebService;
import segmail.program.subscribe.unsubscribe.client.WSUnsubscribeInterface;

/**
 *
 * @author LeeKiatHaw
 */
@WebService(
        serviceName = "WSUnsubscribe",
        endpointInterface = "segmail.program.subscribe.unsubscribe.webservice.WSUnsubscribe")
@HandlerChain(file = "handlers-server.xml")
public class WSUnsubscribe implements WSUnsubscribeInterface {


    /**
     * Web service operation
     * @param key
     * @return 
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String unsubscribe(@WebParam(name = "key") String key) {
        //TODO write your implementation code here:
        return "WS unsubscribed called!";
    }
    
}
