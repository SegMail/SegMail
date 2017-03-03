/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.unsubscribe.webservice;

import eds.component.data.RelationshipNotFoundException;
import eds.component.webservice.UnwantedAccessException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.HandlerChain;
import javax.jws.WebParam;
import javax.jws.WebService;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;
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

    @EJB
    SubscriptionService subService;

    /**
     * Web service operation
     *
     * @param key
     * @return a JSON string of the list name and a redirect address, if any.
     * Example: { "name" : "Segmail List", "redirect" : "http://segmail.io" }
     * @throws eds.component.webservice.UnwantedAccessException
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String unsubscribe(@WebParam(name = "key") String key)
            throws UnwantedAccessException {
        try {
            if (key == null || key.isEmpty()) {
                throw new UnwantedAccessException("Key is not provided.");
            }
            //Check if it is a testing link
            if (key.equalsIgnoreCase("test")) {
                return "This is a testing list";
            }
            
            List<Subscription> subs = subService.unsubscribeSubscriber(key);
            //Assume that there is only 1 unique key for Subscriptions
            Subscription sub = subs.get(0);
            SubscriptionList list = sub.getTARGET();
            if(list.getREDIRECT_UNSUBSCRIBE()!= null && !list.getREDIRECT_UNSUBSCRIBE().isEmpty()) {
                return "redirect: "+list.getREDIRECT_UNSUBSCRIBE(); //Ugly hack, could have used JAX-RS and return a redirect response
            }
            
            return key;
        } catch (RelationshipNotFoundException ex) {
            throw new UnwantedAccessException("Key doesn't match any Subscription records.");
        }
    }

}
