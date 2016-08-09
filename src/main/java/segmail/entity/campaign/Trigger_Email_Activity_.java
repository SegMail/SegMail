/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign;

import eds.entity.transaction.EnterpriseTransactionTrigger_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(Trigger_Email_Activity.class)
public class Trigger_Email_Activity_ extends EnterpriseTransactionTrigger_ {
    public static volatile SingularAttribute<Trigger_Email_Activity,String> SUBCRIBER_EMAIL;
}
