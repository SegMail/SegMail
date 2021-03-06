/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.entity.data.EnterpriseRelationship;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="SUBSCRIPTION")
public class Subscription extends EnterpriseRelationship<SubscriberAccount,SubscriptionList> {

    private String STATUS;
    private String CONFIRMATION_KEY;
    private String UNSUBSCRIBE_KEY;
    
    private int NUM_CONFIRM_SENT;
    
    public Subscription() {
        STATUS = SUBSCRIPTION_STATUS.NEW.name; //default
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public void setSTATUS(SUBSCRIPTION_STATUS STATUS){
        this.STATUS = STATUS.name;
    }

    public String getCONFIRMATION_KEY() {
        return CONFIRMATION_KEY;
    }

    public void setCONFIRMATION_KEY(String CONFIRMATION_KEY) {
        this.CONFIRMATION_KEY = CONFIRMATION_KEY;
    }

    public String getUNSUBSCRIBE_KEY() {
        return UNSUBSCRIBE_KEY;
    }

    public void setUNSUBSCRIBE_KEY(String UNSUBSCRIBE_KEY) {
        this.UNSUBSCRIBE_KEY = UNSUBSCRIBE_KEY;
    }

    public int getNUM_CONFIRM_SENT() {
        return NUM_CONFIRM_SENT;
    }

    public void setNUM_CONFIRM_SENT(int NUM_CONFIRM_SENT) {
        this.NUM_CONFIRM_SENT = NUM_CONFIRM_SENT;
    }
    
    public void incrementConfirmEmail() {
        this.NUM_CONFIRM_SENT++;
    }
    
    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public SUBSCRIPTION_STATUS STATUS() {
        return SUBSCRIPTION_STATUS.valueOf(this.STATUS);
    }
}
