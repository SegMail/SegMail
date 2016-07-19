/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import java.util.Comparator;

/**
 *
 * @author LeeKiatHaw
 */
public class SubscriberFieldValueComparator implements Comparator<SubscriberFieldValue> {

    @Override
    public int compare(SubscriberFieldValue o1, SubscriberFieldValue o2) {
        if(o1.getOWNER() == null) return -1;
        if(o2.getOWNER() == null) return 1;
        
        if(!o1.getOWNER().equals(o2.getOWNER()))
            return o1.getOWNER().compareTo(o2.getOWNER());
        
        return o1.getSNO() - o2.getSNO();
    }
    
}
