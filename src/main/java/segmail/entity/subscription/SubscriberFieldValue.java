/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.entity.data.EnterpriseData;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="SUBSCRIBER_FIELD_VALUE")
public class SubscriberFieldValue extends EnterpriseData<SubscriberAccount> {

    //private SubscriptionListField FIELD;
    private String FIELD_KEY;
    private String VALUE;
    /*private String MAILMERGE_TAG;
    
    private List<SubscriberFieldValueKey> FIELD_KEYS;
    */

    public String getFIELD_KEY() {
        return FIELD_KEY;
    }

    public void setFIELD_KEY(String FIELD_KEY) {
        this.FIELD_KEY = FIELD_KEY;
    }

    public String getVALUE() {
        return VALUE;
    }

    public void setVALUE(String VALUE) {
        this.VALUE = VALUE;
    }

    /*public String getMAILMERGE_TAG() {
        return MAILMERGE_TAG;
    }

    public void setMAILMERGE_TAG(String MAILMERGE_TAG) {
        this.MAILMERGE_TAG = MAILMERGE_TAG;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy="VALUE")
    @OrderColumn(name = "KEY_ORDER")
    public List<SubscriberFieldValueKey> getFIELD_KEYS() {
        return FIELD_KEYS;
    }

    public void setFIELD_KEYS(List<SubscriberFieldValueKey> FIELD_KEYS) {
        this.FIELD_KEYS = FIELD_KEYS;
    }
    */
    
    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /*@Override
    public int compareTo(EnterpriseData o) {
        
        //o is also a SubscriberFieldValue
        if(this.getClass() == o.getClass() && this.getOWNER().equals(o.getOWNER()))
            return this.getFIELD().compareTo(((SubscriberFieldValue)o).getFIELD());
        
        return super.compareTo(o);
    }*/

    @Override
    public String HTMLName() {
        String htmlName = this.FIELD_KEY.trim().replaceAll(" ", "-");
        return htmlName;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.FIELD_KEY);
        return hash;
    }

    /**
     * The FIELD_KEY is used for comparing Field Value objects because this is 
     * used to identify what values a SubscriberAccount has with a List. 
     * 
     * @param obj
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SubscriberFieldValue other = (SubscriberFieldValue) obj;
        if (!Objects.equals(this.OWNER, other.OWNER)) {
            return false;
        }
        
        if (!Objects.equals(this.FIELD_KEY, other.FIELD_KEY)) {
            return false;
        }
        return true;
    }

    
    
    
    
    
}
