/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.entity.data.EnterpriseObject;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="SUBSCRIBER_ACCOUNT")
public class SubscriberAccount extends EnterpriseObject {
    
    /**
     * This is actually more like the date of creation, but for end users, they 
     * would only understand date of subscription better.
     */
    public static final String MM_DATE_OF_SUBSCRIPTION = "{!date_subscribe}";
    
    public static final String MM_LENGTH_OF_SUBSCRIPTION = "{!length_subscribe}";

    private String EMAIL;
    
    private String SUBSCRIBER_STATUS;
    
    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getSUBSCRIBER_STATUS() {
        return SUBSCRIBER_STATUS;
    }

    public void setSUBSCRIBER_STATUS(String SUBSCRIBER_STATUS) {
        this.SUBSCRIBER_STATUS = SUBSCRIBER_STATUS;
    }
    
    public SUBSCRIBER_STATUS SUBSCRIBER_STATUS() {
        return segmail.entity.subscription.SUBSCRIBER_STATUS.valueOf(this.SUBSCRIBER_STATUS);
    }
    
    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String alias() {
        return getEMAIL();
    }
    
}
