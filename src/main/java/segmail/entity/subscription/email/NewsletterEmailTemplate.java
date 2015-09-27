/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.email;

import javax.persistence.Entity;
import segmail.entity.subscription.email.EmailTemplateFactory.TYPE;
import static segmail.entity.subscription.email.EmailTemplateFactory.TYPE.NEWSLETTER;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
public class NewsletterEmailTemplate extends EmailTemplate{

    @Override
    public TYPE type() {
        return NEWSLETTER;
    }
    
}
