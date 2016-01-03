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
public class SubscriptionListFieldComparator implements Comparator<SubscriptionListField> {

    @Override
    public int compare(SubscriptionListField o1, SubscriptionListField o2) {
        return o1.getSNO() - o2.getSNO();
    }
    
}
