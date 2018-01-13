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
public class SignupListContainer {
    
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    private DateTime startDate;
    private DateTime endDate;
    private Map<DateTime,List<Subscription>> signups;

    public SignupListContainer(DateTime startDate, DateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.signups = new HashMap<>();
    }
    
    public void addSignup(Subscription signup) {
        DateTime created = new DateTime(signup.getDATE_CREATED());
        if(!signups.containsKey(created)) {
            signups.put(created, new ArrayList<>());
        }
        signups.get(created).add(signup);
    }

    /**
     * eg. { '2017-10-01' : {
     *          {
     *              subscriber : {
     *                  id : 101,
     *                  email : 'abc@abc.com'
     *              },
     *              list    :   {
     *                  id : 123,
     *                  name : 'List 1'
     *              }
     *          }, ...
     *      },
     *      '2017-10-02' : {
     *          { ...
     *              
     * @return 
     */
    public String toJson() {
        
        JsonObjectBuilder container = Json.createObjectBuilder();
        List<DateTime> sortedDTs = signups.keySet().stream().sorted().collect(Collectors.toList());
        for(DateTime dt : sortedDTs) {
            JsonArrayBuilder signupArray = Json.createArrayBuilder();
            
            for(Subscription subsc : signups.get(dt)) {
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
                
                signupArray.add(signupObj);
            }
            container.add(dt.toString(DATE_FORMAT), signupArray);
        }
        
        
        return container.build().toString();
    }
    
    
}
