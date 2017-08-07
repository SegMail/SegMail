/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import java.io.Serializable;
import static javax.persistence.ConstraintMode.NO_CONSTRAINT;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="SUBSCRIBER_FIELD_VALUE_KEY")
public class SubscriberFieldValueKey implements Serializable {
    
    private SubscriberFieldValue VALUE;
    private String FIELD_KEY;
    private int KEY_ORDER;

    @Id
    @ManyToOne
    public SubscriberFieldValue getVALUE() {
        return VALUE;
    }

    public void setVALUE(SubscriberFieldValue VALUE) {
        this.VALUE = VALUE;
    }

    public String getFIELD_KEY() {
        return FIELD_KEY;
    }

    public void setFIELD_KEY(String FIELD_KEY) {
        this.FIELD_KEY = FIELD_KEY;
    }

    @Id
    public int getKEY_ORDER() {
        return KEY_ORDER;
    }

    public void setKEY_ORDER(int KEY_ORDER) {
        this.KEY_ORDER = KEY_ORDER;
    }
    
    
}
