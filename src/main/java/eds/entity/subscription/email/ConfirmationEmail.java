/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.subscription.email;

import eds.entity.mail.Email;
import eds.entity.mail.MailRecipient;
import eds.entity.subscription.SubscriberAccount;

/**
 *
 * @author LeeKiatHaw
 */
public class ConfirmationEmail extends Email<SubscriberAccount> {
    
    /**
     * A Confirmation email will only have 1 recipient
     * 
     * @return 
     */
    public MailRecipient getRECIPIENT(){
        return this.RECIPIENTS.get(0);
    }
    
    public void setRECIPIENT(SubscriberAccount s){
        if(!RECIPIENTS.isEmpty()) RECIPIENTS.clear();
        RECIPIENTS.add(s);
    }

    /**
     * Adding a recipient will replace the existing one, if any.
     * 
     * @param recipient 
     */
    @Override
    public void addRecipient(SubscriberAccount recipient) {
        this.setRECIPIENT(recipient);
    }
}
