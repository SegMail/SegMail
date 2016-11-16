/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.entity.audit.AuditedObjectListener;
import eds.entity.data.EnterpriseData;
import eds.entity.data.EnterpriseDataListener;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="SUBSCRIPTION_LIST_FIELD")
@EntityListeners({
    SubscriptionListFieldListener.class
})
public class SubscriptionListField extends EnterpriseData<SubscriptionList>{
    
    private boolean MANDATORY;
    private String FIELD_NAME;
    private String KEY_NAME; //Immutable name to retrieve for a SubscriberFieldValue
    private String TYPE;
    private String DESCRIPTION;
    
    private String MAILMERGE_TAG;

    //A must for all JPA entities
    public SubscriptionListField() {
    }

    public SubscriptionListField(SubscriptionList OWNER, int order, boolean MANDATORY, String FIELD_NAME, FIELD_TYPE TYPE, String DESCRIPTION) {
        this.OWNER = OWNER;
        this.FIELD_NAME = FIELD_NAME;
        this.MANDATORY = MANDATORY;
        this.TYPE = TYPE.name;
        this.DESCRIPTION = DESCRIPTION;
        this.SNO = order;
    }
    
    public String getFIELD_NAME() {
        return FIELD_NAME;
    }

    public void setFIELD_NAME(String FIELD_NAME) {
        this.FIELD_NAME = FIELD_NAME;
    }

    public String getTYPE() {
        return TYPE;
    }
    
    public void setTYPE(String type) {
        this.TYPE = type;
    }

    public void setTYPE(FIELD_TYPE TYPE) {
        this.TYPE = TYPE.name;
    }


    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public boolean isMANDATORY() {
        return MANDATORY;
    }

    public void setMANDATORY(boolean MANDATORY) {
        this.MANDATORY = MANDATORY;
    }

    public String getKEY_NAME() {
        return KEY_NAME;
    }

    public void setKEY_NAME(String KEY_NAME) {
        this.KEY_NAME = KEY_NAME;
    }

    public String getMAILMERGE_TAG() {
        return MAILMERGE_TAG;
    }

    public void setMAILMERGE_TAG(String MAILMERGE_TAG) {
        this.MAILMERGE_TAG = MAILMERGE_TAG;
    }
    
    /**
     * If the field name is used as an identifier in the view layer (html), this 
     * should be used instead of FIELD_NAME.
     * 
     * @return 
     */
    @Override
    public String HTMLName(){
        String htmlName = this.FIELD_NAME.trim().replaceAll(" ", "");
        return htmlName;
    }

    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        return //getOWNER().getOBJECT_NAME().replace(" ", "")
                "listfield"
                .concat(
                        String.format("%010d", getOWNER().getOBJECTID())
                                .concat(String.format("%05d", getSNO()))
                );
    }

}
