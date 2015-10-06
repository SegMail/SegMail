/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.email;

import javax.persistence.Entity;
import javax.persistence.Table;
import static segmail.entity.subscription.email.EmailTemplateFactory.TYPE.CONFIRMATION;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="CONFRIMATION_EMAIL_TEMPLATE")
//@DiscriminatorValue("CONFIRMATION")
public class ConfirmationEmailTemplate extends EmailTemplate{

    @Override
    public EmailTemplateFactory.TYPE type() {
        return CONFIRMATION;
    }

    
    
}
