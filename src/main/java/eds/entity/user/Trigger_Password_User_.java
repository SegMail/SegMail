/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.user;

import eds.entity.transaction.EnterpriseTransactionTrigger_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(Trigger_Password_User.class)
public class Trigger_Password_User_ extends EnterpriseTransactionTrigger_{
    public static volatile SingularAttribute<Trigger_Password_User,String> TRIGGERED_EMAIL;
}
