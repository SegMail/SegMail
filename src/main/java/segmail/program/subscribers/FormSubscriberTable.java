/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.SubscriptionListField;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormSubscriberTable")
public class FormSubscriberTable {
    
    @Inject ProgramSubscribers program;
    
    @EJB SubscribersDripService dripService;
    
    public List<Map<String, String>> getSubscriberTable() {
        return program.getSubscriberTable();
    }

    public void setSubscriberTable(List<Map<String, String>> subscriberTable) {
        program.setSubscriberTable(subscriberTable);
    }
    
    public void loadSubscribers(int page) {
        List<SubscriberFieldValue> values = dripService.drip(page);
        //Extract 
        List<SubscriberAccount> subscriberAccounts = new ArrayList<>();
        for(SubscriberFieldValue value : values) {
            
        }
        
    }
    
}
