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
public class SignupCountContainer {
    
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    private Map<DateTime,Long> signups;
    
    public SignupCountContainer() {
        this.signups = new HashMap<>();
    }
    
    public void addSignup(String dateString, long count) {
        DateTime dt = DateTime.parse(dateString);
        signups.put(dt, count);
    }
    
    public String toJson() {
        JsonObjectBuilder container = Json.createObjectBuilder();
        List<DateTime> sortedDTs = signups.keySet().stream().sorted().collect(Collectors.toList());
        for(DateTime dt : sortedDTs) {
            container.add(dt.toString(DATE_FORMAT), signups.get(dt));
        }
        
        return container.build().toString();
    }
}
