/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription.reporting;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import org.joda.time.DateTime;
import segmail.component.subscription.ListService;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;

/**
 *
 * @author LeeKiatHaw
 */
public class LatestSubscribersContainer {
    
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    private Map<SubscriberAccount,Map<String,SubscriberFieldValue>> subscribers;
    
    private Map<String,SubscriptionListField> fields;
    
    public LatestSubscribersContainer() {
        subscribers = new HashMap<>();
        fields = new HashMap<>();
    }
    
    public void addSubscriberValue(SubscriberFieldValue value) {
        if(!subscribers.containsKey(value.getOWNER())) {
            subscribers.put(value.getOWNER(), new HashMap<>());
        }
        subscribers.get(value.getOWNER()).put(value.getFIELD_KEY(), value);
        
    }
    
    public void addListField(SubscriptionListField field) {
        fields.put((String) field.generateKey(), field);
    }
    
    public String toJson() {
        // Build subscribers
        JsonObjectBuilder container = Json.createObjectBuilder();
        JsonArrayBuilder array = Json.createArrayBuilder();
        
        List<SubscriberAccount> sortedAcc = 
                subscribers.keySet().stream().sorted(new Comparator<SubscriberAccount>(){

            @Override
            public int compare(SubscriberAccount o1, SubscriberAccount o2) {
                return -o1.getDATE_CREATED().compareTo(o2.getDATE_CREATED());
            }
        
        }).collect(Collectors.toList());
        for(SubscriberAccount subscriber : sortedAcc) {
            Map<String,SubscriberFieldValue> subscriberMap = subscribers.get(subscriber);
            JsonObjectBuilder subscObj = Json.createObjectBuilder();
            
            SubscriptionList list = null; // to be populated while looping through fields
            DateTime dateSubscribed = null;
            for(String fieldKey : subscriberMap.keySet()) {
                SubscriberFieldValue value = subscriberMap.get(fieldKey);
                // Only add if the field key is not null and a corresponding field can be found
                if(value.getFIELD_KEY() != null && fields.containsKey(value.getFIELD_KEY())) {
                    // Extra processing only for default fields
                    String fieldName = fields.get(value.getFIELD_KEY()).getFIELD_NAME();
                    for(int i=0; i < ListService.DEFAULT_FIELD_PATTERNS.length ; i++) {
                        String defaultPattern = ListService.DEFAULT_FIELD_PATTERNS[i];
                        String defaultField = ListService.DEFAULT_FIELD_NAMES[i];
                        if(fieldName.matches(defaultPattern)) {
                            subscObj.add(
                                defaultField, 
                                (value.getVALUE() == null) ? "" : value.getVALUE());
                        }
                    }
                    // Normal processing for all fields (if fieldName is exactly the
                    // same as a default field, it will replace the entry
                    subscObj.add(
                            fieldName, 
                            (value.getVALUE() == null) ? "" : value.getVALUE());
                    
                    // Will only execute the 1st iteration
                    if(list == null) {
                        list = fields.get(value.getFIELD_KEY()).getOWNER();
                        subscObj.add("List", list.getLIST_NAME());
                    }
                    if(dateSubscribed == null) {
                        dateSubscribed = new DateTime(value.getOWNER().getDATE_CREATED());
                        subscObj.add("Date Subscribed", dateSubscribed.toString(DATE_FORMAT));
                    }
                }
            }
            
            array.add(subscObj);
        }
        container.add("subscribers", array); // must add after the whole array has been populated
        // Build columns
        JsonArrayBuilder columns = Json.createArrayBuilder();
        columns.add(SubscriptionService.DEFAULT_EMAIL_FIELD_NAME);
        columns.add(ListService.DEFAULT_FNAME_FIELD_NAME);
        columns.add(ListService.DEFAULT_LNAME_FIELD_NAME);
        columns.add("Date Subscribed");
        columns.add("List");
        container.add("columns", columns);
        
        return container.build().toString();
    }
}
