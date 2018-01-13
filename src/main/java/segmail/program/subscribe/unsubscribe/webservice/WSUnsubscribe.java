/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.unsubscribe.webservice;

import eds.component.data.RelationshipNotFoundException;
import eds.component.webservice.UnwantedAccessException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.HandlerChain;
import javax.jws.WebParam;
import javax.jws.WebService;
import segmail.component.subscription.ListService;
import segmail.component.subscription.SubscriptionService;
import segmail.component.subscription.mailmerge.MailMergeService;
import segmail.entity.subscription.SUBSCRIPTION_STATUS;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;
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
    @EJB
    ListService listService;
    @EJB
    MailMergeService mmService;

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
            
            List<Subscription> subs = subService.updateSubscription(key,SUBSCRIPTION_STATUS.UNSUBSCRIBED);
            //Assume that there is only 1 unique key for Subscriptions
            Subscription sub = subs.get(0);
            SubscriberAccount subscriber = sub.getSOURCE();
            SubscriptionList list = sub.getTARGET();
            
            String redirect = list.generateUnsubscribeUrl();
            if(redirect != null && !redirect.isEmpty()) {
                List<SubscriptionListField> fields = listService.getFieldsForSubscriptionList(list.getOBJECTID());
                List<SubscriberFieldValue> fieldValues = subService.getSubscriberValuesBySubscriberObject(subscriber);
                Map<Long,Map<String,String>> mmValues = mmService.createMMValueMap(subscriber.getOBJECTID(), fields, fieldValues);
                
                redirect = mmService.parseSubscriberTags(redirect, mmValues.get(subscriber.getOBJECTID()));
                
                return "redirect: "+redirect; //Ugly hack, could have used JAX-RS and return a redirect response
            }
            
            return key;
        } catch (RelationshipNotFoundException ex) {
            throw new UnwantedAccessException("Key doesn't match any Subscription records.");
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

}
