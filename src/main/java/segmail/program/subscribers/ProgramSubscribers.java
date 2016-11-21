/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import java.util.List;
import java.util.Map;
import seca2.program.Program;
import segmail.entity.subscription.SubscriptionListField;

/**
 *
 * @author LeeKiatHaw
 */
public class ProgramSubscribers extends Program {
    
    private List<Map<String,String>> subscriberTable;

    public List<Map<String, String>> getSubscriberTable() {
        return subscriberTable;
    }

    public void setSubscriberTable(List<Map<String, String>> subscriberTable) {
        this.subscriberTable = subscriberTable;
    }

    @Override
    public void clearVariables() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void initRequestParams() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void initProgram() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
