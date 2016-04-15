/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.unsubscribe.webservice;

import javax.jws.HandlerChain;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author LeeKiatHaw
 */
//@WebService(
//        serviceName = "WSUnsubscribe",
//        endpointInterface = "segmail.program.subscribe.unsubscribe.webservice.WSUnsubscribe")
//@HandlerChain(file = "handlers-server.xml")
public class WSUnsubscribe {


    /**
     * Web service operation
     * @param key
     * @return 
     */
    public String unsubscribe(@WebParam(name = "key") String key) {
        //TODO write your implementation code here:
        return "WS unsubscribed called!";
    }
    
}
