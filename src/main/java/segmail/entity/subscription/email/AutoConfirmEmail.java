/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.email;

import java.util.Map;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import static segmail.entity.subscription.email.AutoEmailTypeFactory.TYPE.CONFIRMATION;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="AUTO_CONFIRM_EMAIL")
@DiscriminatorValue("AutoConfirmEmail")
public class AutoConfirmEmail extends AutoresponderEmail{

    @Override
    public AutoEmailTypeFactory.TYPE type() {
        return CONFIRMATION;
    }
}
