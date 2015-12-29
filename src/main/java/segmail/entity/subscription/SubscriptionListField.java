/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="SUBSCRIPTION_LIST_FIELD")
public class SubscriptionListField implements Serializable{
    
    private long ID;
    private String FIELD_NAME;
    private String TYPE;
    private String DESCRIPTION;
    private SubscriptionListFieldList LIST;

    //A must for all JPA entities
    public SubscriptionListField() {
    }

    public SubscriptionListField(long ID, String FIELD_NAME, String TYPE, String DESCRIPTION) {
        this.ID = ID;
        this.FIELD_NAME = FIELD_NAME;
        this.TYPE = TYPE;
        this.DESCRIPTION = DESCRIPTION;
    }

    @Id
    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
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

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    @Id @ManyToOne(cascade=CascadeType.ALL)
    public SubscriptionListFieldList getLIST() {
        return LIST;
    }

    public void setLIST(SubscriptionListFieldList LIST) {
        this.LIST = LIST;
    }
    
    
}
