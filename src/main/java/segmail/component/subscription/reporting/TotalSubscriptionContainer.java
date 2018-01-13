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
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import org.joda.time.DateTime;
import segmail.entity.subscription.SUBSCRIPTION_STATUS;

/**
 *
 * @author LeeKiatHaw
 */
public class TotalSubscriptionContainer {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    private Map<DateTime,Map<SUBSCRIPTION_STATUS,Long>> allSubscriptions;

    public TotalSubscriptionContainer() {
        this.allSubscriptions = new HashMap<>();
    }

    public void addCount(DateTime dt, SUBSCRIPTION_STATUS status, long count) {
        if(!allSubscriptions.containsKey(dt)) {
            allSubscriptions.put(dt, new HashMap<>());
        }
        if(!allSubscriptions.get(dt).containsKey(status)){
            allSubscriptions.get(dt).put(status, 0L);
        }
        long existingCount = allSubscriptions.get(dt).get(status);
        allSubscriptions.get(dt).put(status, existingCount + count);
    }
    
    /**
     * Structure:
 {
  allSubscriptions : [
      {   2017-12-01 : 100    },
      {   2017-12-01 : 99    },
      {   2017-12-01 : 101    },
      {   2017-12-01 : 105    }
  ],
  totalSignups : 1,954,
  totalUnsubscribes : 519,
  totalBounces : 122
 }
     * @return 
     */
    public String toJson() {
        JsonObjectBuilder container = Json.createObjectBuilder();
        
        if(allSubscriptions.isEmpty()) 
            return container.build().toString();
        
        // totalActive
        JsonArrayBuilder activeArray = Json.createArrayBuilder();
        List<DateTime> sortedDTs = allSubscriptions.keySet().stream().sorted().collect(Collectors.toList());
        for(DateTime dt : sortedDTs) {
            Map<SUBSCRIPTION_STATUS,Long> count = allSubscriptions.get(dt);
            long activeDay = count.get(SUBSCRIPTION_STATUS.CONFIRMED);
            
            JsonArrayBuilder dateCountPair = Json.createArrayBuilder();
            dateCountPair.add(dt.toString(DATE_FORMAT));
            dateCountPair.add(activeDay);
            
            activeArray.add(dateCountPair);
        }
        container.add("totalActive", activeArray);
        
        
        // Get the totalSignups, totalUnsubscribes and totalBounces from the latest
        // entry in allSubscriptions
        DateTime latest = sortedDTs.get(sortedDTs.size()-1);
        Map<SUBSCRIPTION_STATUS,Long> lastCount = allSubscriptions.get(latest);
        
        long totalSignups = lastCount.values().stream().mapToLong(i -> i.longValue()).sum();
        long totalUnsubscribes = lastCount.get(SUBSCRIPTION_STATUS.UNSUBSCRIBED);
        long totalBounces = lastCount.get(SUBSCRIPTION_STATUS.BOUNCED);
        
        container.add("totalSignups", totalSignups);
        container.add("totalUnsubscribes", totalUnsubscribes);
        container.add("totalBounces", totalBounces);
        
        return container.build().toString();
    }
    
}
