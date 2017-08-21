/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.mail;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.client.ClientAWSService;
import eds.entity.mail.EMAIL_PROCESSING_STATUS;
import eds.entity.mail.Email;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import segmail.component.subscription.SubscriptionService;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class MailServiceInboundHelper {
    
    @EJB UpdateObjectService updService;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateBounceStatus(Email email) {
        email.PROCESSING_STATUS(EMAIL_PROCESSING_STATUS.BOUNCED);
        email = (Email) updService.merge(email);
    }
    
    
}