/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.entity.data.EnterpriseData;
import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;

/**
 *
 * @author LeeKiatHaw
 */
public class SubscriberFieldValue extends EnterpriseData<SubscriberAccount> {

    private SubscriptionListField FIELD;
    private String VALUE;

    @ManyToOne(cascade=CascadeType.ALL)
    public SubscriptionListField getFIELD() {
        return FIELD;
    }

    public void setFIELD(SubscriptionListField FIELD) {
        this.FIELD = FIELD;
    }

    public String getVALUE() {
        return VALUE;
    }

    public void setVALUE(String VALUE) {
        this.VALUE = VALUE;
    }
    
    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
