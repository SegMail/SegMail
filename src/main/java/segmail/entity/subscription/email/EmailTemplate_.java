/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.email;

import eds.entity.data.EnterpriseData_;
import eds.entity.data.EnterpriseObject_;
import segmail.entity.subscription.email.EmailTemplate.EMAIL_TYPE;
import eds.entity.user.UserAccount;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(EmailTemplate.class)
public class EmailTemplate_ extends EnterpriseObject_ {

    public static volatile SingularAttribute<EmailTemplate,String> SUBJECT;
    public static volatile SingularAttribute<EmailTemplate,String> BODY;
    public static volatile SingularAttribute<EmailTemplate,EMAIL_TYPE> TYPE;
}
