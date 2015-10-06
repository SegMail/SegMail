/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.email;

import eds.entity.data.EnterpriseObject_;
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
}
