/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.datasource.synchronize;

import java.util.List;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberFieldValue;

/**
 * May not be needed!
 * @author LeeKiatHaw
 */
public class SubscriberAccountWrapper extends SyncListObject {
    
    private SubscriberAccount account;
    private List<SubscriberFieldValue> values;

    @Override
    public String getId() {
        return account.getEMAIL();
    }
    
}
