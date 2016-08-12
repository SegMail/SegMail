/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.unsubscribe.webservice;

import eds.component.data.RelationshipNotFoundException;
import eds.component.webservice.UnwantedAccessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.HandlerChain;
import javax.jws.WebParam;
import javax.jws.WebService;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.email.mailmerge.MAILMERGE_REQUEST;
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
     * @throws eds.component.data.RelationshipNotFoundException
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
            MAILMERGE_REQUEST label = MAILMERGE_REQUEST.getByLabel(key);
            if (label != null && label.equals(MAILMERGE_REQUEST.UNSUBSCRIBE)) {
                return "This is a testing list";
            }
            
            Subscription sub = subService.unsubscribeSubscriber(key);
            SubscriptionList list = sub.getTARGET();
            
            /*JsonObjectBuilder resultObjectBuilder = Json.createObjectBuilder();
            resultObjectBuilder.add("name", list.getLIST_NAME());
            resultObjectBuilder.add("redirect", "");
            
            String result = resultObjectBuilder.build().toString();*/
            String result = list.getLIST_NAME();
            
            return result;
        } catch (RelationshipNotFoundException ex) {
            throw new UnwantedAccessException("Key doesn't match any Subscription records.");
        }
    }

}
