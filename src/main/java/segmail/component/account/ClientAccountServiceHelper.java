/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.account;

import eds.component.GenericObjectService;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.MultivaluedMap;
import segmail.component.subscription.ListService;
import segmail.component.subscription.SubscriptionException;
import segmail.component.subscription.SubscriptionService;
import segmail.component.subscription.mailmerge.MailMergeService;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class ClientAccountServiceHelper {
    
    @EJB SubscriptionService subService;
    @EJB MailMergeService mmService;
    @EJB ListService listService;
    
    public String subscribe(long listId, long clientId, MultivaluedMap<String,String> subscriptionMap) {
        
        JsonObjectBuilder resultObjectBuilder = Json.createObjectBuilder();
        
        if(listId > 0) {
            Map<String,Object> subscriberMap = new HashMap<>();
            for(String key : subscriptionMap.keySet()) {
                if(subscriptionMap.get(key) == null)
                    continue;
                
                List<String> values = subscriptionMap.get(key);
                if(values.isEmpty())
                    continue;
                
                subscriberMap.put(key, values.get(0));
            }
            try {
                Subscription newSubsc = subService.subscribe(clientId, listId, subscriberMap, true);
                SubscriberAccount newSub = newSubsc.getSOURCE();
                SubscriptionList list = newSubsc.getTARGET();
                
                //If there is a redirect link, return it
                String redirect = list.generateConfirmUrl();
                if(redirect != null && !redirect.isEmpty()) {
                    /*String redirectUrl = list.getREDIRECT_CONFIRM();
                    if(!redirectUrl.startsWith("http://") && !redirectUrl.startsWith("https://"))
                        redirectUrl = "http://"+redirectUrl;
                    */
                    // Parse MM tags (currently only subscriber tags are available
                    List<SubscriberFieldValue> fieldValues = subService.getSubscriberValuesBySubscriberObject(newSub);
                    List<SubscriptionListField> fields = listService.getFieldsForSubscriptionList(listId);
                    Map<Long,Map<String,String>> mmValues = mmService.createMMValueMap(newSub.getOBJECTID(), fields, fieldValues);
                    
                    redirect = mmService.parseSubscriberTags(redirect, mmValues.get(newSub.getOBJECTID()));
                    
                    resultObjectBuilder.add("redirect", redirect);
                }
                
            } catch (EntityNotFoundException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                resultObjectBuilder.add("error", ex.getMessage());
            } catch (SubscriptionException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                resultObjectBuilder.add("error", ex.getMessage());
            } catch (RelationshipExistsException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                resultObjectBuilder.add("exists", ex.getMessage());
            } catch (IncompleteDataException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                resultObjectBuilder.add("error", ex.getMessage());
            } catch (URISyntaxException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                resultObjectBuilder.add("error", ex.getMessage());
            }
        }
        return resultObjectBuilder.build().toString();
    }
}
