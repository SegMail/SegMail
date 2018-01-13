/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription.reporting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
public class UnsubscribeCountContainer {
    
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    private Map<DateTime,Long> unsubscribes;
    
    public UnsubscribeCountContainer() {
        this.unsubscribes = new HashMap<>();
    }
    
    public void addUnsubscribe(String dateString, long count) {
        DateTime dt = DateTime.parse(dateString);
        unsubscribes.put(dt, count);
    }
    
    public String toJson() {
        JsonObjectBuilder container = Json.createObjectBuilder();
        List<DateTime> sortedDTs = unsubscribes.keySet().stream().sorted().collect(Collectors.toList());
        for(DateTime dt : sortedDTs) {
            container.add(dt.toString(DATE_FORMAT), unsubscribes.get(dt));
        }
        
        return container.build().toString();
    }
}
