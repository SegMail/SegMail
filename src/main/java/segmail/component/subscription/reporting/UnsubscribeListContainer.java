/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription.reporting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import org.joda.time.DateTime;
import segmail.entity.subscription.Subscription;

/**
 *
 * @author LeeKiatHaw
 */
public class UnsubscribeListContainer {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    private Map<DateTime,List<Subscription>> unsubscribes;
    
    public UnsubscribeListContainer() {
        this.unsubscribes = new HashMap<>();
    }
    
    public void addUnsubscribe(Subscription unsub) {
        DateTime changed = new DateTime(unsub.getDATE_CHANGED());
        if(!unsubscribes.containsKey(changed)) {
            unsubscribes.put(changed, new ArrayList<>());
        }
        unsubscribes.get(changed).add(unsub);
    }
    
    public String toJson() {
        
        JsonObjectBuilder container = Json.createObjectBuilder();
        List<DateTime> sortedDTs = unsubscribes.keySet().stream().sorted().collect(Collectors.toList());
        for(DateTime dt : sortedDTs) {
            JsonArrayBuilder unsubArray = Json.createArrayBuilder();
            
            for(Subscription subsc : unsubscribes.get(dt)) {
                JsonObjectBuilder signupObj = Json.createObjectBuilder();
                // Subscriber object
                JsonObjectBuilder subscObj = Json.createObjectBuilder();
                subscObj.add("id", subsc.getSOURCE().getOBJECTID());
                subscObj.add("email", subsc.getSOURCE().getEMAIL());
                signupObj.add("subscriber", subscObj);
                
                // List object
                JsonObjectBuilder listObj = Json.createObjectBuilder();
                listObj.add("id", subsc.getTARGET().getOBJECTID());
                listObj.add("name", subsc.getTARGET().getLIST_NAME());
                signupObj.add("list", listObj);
                
                unsubArray.add(signupObj);
            }
            container.add(dt.toString(DATE_FORMAT), unsubArray);
        }
        
        
        return container.build().toString();
    }
}
