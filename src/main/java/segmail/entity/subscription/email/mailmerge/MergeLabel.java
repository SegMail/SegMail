/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.email.mailmerge;

import eds.entity.data.EnterpriseObject;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Shortforms for URLs, HTML markups, texts (copyright statements, address, etc)
 * can also link up fields 
 * 
 * @author LeeKiatHaw
 */
@Entity
@Table(name="MERGE_LABEL")
public abstract class MergeLabel extends EnterpriseObject {

    private String MESSAGE_KEY;
    
    /**
     * Given the parameters, the child label can generate its own values. For 
     * example, an UNSUBSCRIBE label can take in the list ID, subscriber ID/Email,
     * 
     * @param param
     * @return 
     */
    protected abstract String generateValue(Map<String,Object> param);

    public String getMESSAGE_KEY() {
        return MESSAGE_KEY;
    }

    public void setMESSAGE_KEY(String MESSAGE_KEY) {
        this.MESSAGE_KEY = MESSAGE_KEY;
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
        return this.getMESSAGE_KEY();
    }
    
}
