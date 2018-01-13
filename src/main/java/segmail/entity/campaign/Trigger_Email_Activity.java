/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign;

import eds.entity.mail.Email;
import eds.entity.transaction.EnterpriseTransactionTrigger;
import eds.entity.transaction.TransactionStatus;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Index;
import javax.persistence.Table;
import org.joda.time.DateTime;

/**
 * A Trigger_Email_Activity is a relationship stating that "the CampaignActivity,
 * Target EnterpriseObject, has triggered the Email, Source EnterpriseTransaction."
 * 
 * This relationship signifies that an Email has been sent out for the CampaignActivity.
 * 
 * @author LeeKiatHaw
 */
@Entity
@Table(name="TRIGGER_EMAIL_ACTIVITY"
        ,indexes = {
            @Index(name="CampaignServiceGetSentEmails", columnList="TRANSACTION_ID,TRIGGERING_OBJECT,SUBSCRIBER_EMAIL")
        }
)
@EntityListeners({
    Trigger_Email_Activity_Listener.class
})
public class Trigger_Email_Activity extends EnterpriseTransactionTrigger<Email,CampaignActivity> {
    private String SUBSCRIBER_EMAIL;
    
    private String SUBSCRIBER_ID;

    public String getSUBSCRIBER_ID() {
        return SUBSCRIBER_ID;
    }

    public void setSUBSCRIBER_ID(String SUBSCRIBER_ID) {
        this.SUBSCRIBER_ID = SUBSCRIBER_ID;
    }

    public String getSUBSCRIBER_EMAIL() {
        return SUBSCRIBER_EMAIL;
    }

    public void setSUBSCRIBER_EMAIL(String SUBSCRIBER_EMAIL) {
        this.SUBSCRIBER_EMAIL = SUBSCRIBER_EMAIL;
    }

    @Override
    public <Ts extends TransactionStatus> Ts PROCESSING_STATUS() {
        return null;
    }

    @Override
    public Trigger_Email_Activity transit(TransactionStatus newStatus, DateTime dt) {
        return this;
    }
    
    
}
