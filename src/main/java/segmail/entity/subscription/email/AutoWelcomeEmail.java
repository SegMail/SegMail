/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.email;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import segmail.entity.subscription.email.AutoEmailTypeFactory.TYPE;
import static segmail.entity.subscription.email.AutoEmailTypeFactory.TYPE.WELCOME;

/**
 *
 * @author LeeKiatHaw
 */
//@Entity
//@Table(name="AUTO_WELCOME_EMAIL")
//@DiscriminatorValue("AutoWelcomeEmail")
public class AutoWelcomeEmail extends AutoresponderEmail{

    //@Override
    public TYPE type() {
        return WELCOME;
    }

    
}
