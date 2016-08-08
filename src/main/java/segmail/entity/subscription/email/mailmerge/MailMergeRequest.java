/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.email.mailmerge;

import eds.entity.transaction.EnterpriseTransaction;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="MAILMERGE_REQUEST")
@EntityListeners({
    MailMergeRequestListener.class})
public class MailMergeRequest extends EnterpriseTransaction {
    
    /**
     * The corresponding MAILMERGE_LABEL name;
     */
    private String MAILMERGE_LABEL;
    
    /**
     * The processed value. Usually a link.
     */
    private String MAILMERGE_VALUE;

    public String getMAILMERGE_LABEL() {
        return MAILMERGE_LABEL;
    }

    public void setMAILMERGE_LABEL(String MAILMERGE_LABEL) {
        this.MAILMERGE_LABEL = MAILMERGE_LABEL;
    }
    
    public void overrideSTATUS(MAILMERGE_STATUS mailmerge_status) {
        this.PROCESSING_STATUS = mailmerge_status.name();
    }
    
}
