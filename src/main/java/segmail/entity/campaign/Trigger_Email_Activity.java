/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign;

import eds.entity.mail.Email;
import eds.entity.transaction.EnterpriseTransactionTrigger;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 * A Trigger_Email_Activity is a relationship stating that "the CampaignActivity,
 * Target EnterpriseObject, has triggered the Email, Source EnterpriseTransaction."
 * 
 * A relationship signifies that an Email has been sent out for the CampaignActivity.
 * 
 * @author LeeKiatHaw
 */
@Entity
@Table(name="TRIGGER_EMAIL_ACTIVITY")
@EntityListeners({
    Trigger_Email_Activity_Listener.class
})
public class Trigger_Email_Activity extends EnterpriseTransactionTrigger<Email,CampaignActivity> {
    private String SUBCRIBER_EMAIL;

    public String getSUBCRIBER_EMAIL() {
        return SUBCRIBER_EMAIL;
    }

    public void setSUBCRIBER_EMAIL(String SUBCRIBER_EMAIL) {
        this.SUBCRIBER_EMAIL = SUBCRIBER_EMAIL;
    }
    
    
}
