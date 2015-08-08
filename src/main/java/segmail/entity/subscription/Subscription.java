/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.entity.data.EnterpriseRelationship;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="SUBSCRIPTION")
public class Subscription extends EnterpriseRelationship<SubscriberAccount,SubscriptionList> {

    public Subscription() {
    }

    public static enum STATUS{
        NEW,
        CONFIRMED,
        BOUNCED
    }
    
    private STATUS STATUS;

    @Enumerated(EnumType.STRING)
    public STATUS getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(STATUS STATUS) {
        this.STATUS = STATUS;
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
