/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import seca2.program.Program;
import segmail.entity.subscription.SubscriberAccount;

/**
 *
 * @author LeeKiatHaw
 */
public class ProgramSubscribers extends Program {
    
    private Map<SubscriberAccount,Map<String,Object>> subscriberTable;
    
    private int currentPage;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    
    public Map<SubscriberAccount,Map<String,Object>> getSubscriberTable() {
        return subscriberTable;
    }

    public void setSubscriberTable(Map<SubscriberAccount,Map<String,Object>> subscriberTable) {
        this.subscriberTable = subscriberTable;
    }

    @Override
    public void clearVariables() {
        
    }

    @Override
    public void initRequestParams() {
        
    }

    @Override
    public void initProgram() {
        setSubscriberTable(new HashMap<SubscriberAccount,Map<String,Object>>());
        setCurrentPage(1);
    }
    
}
