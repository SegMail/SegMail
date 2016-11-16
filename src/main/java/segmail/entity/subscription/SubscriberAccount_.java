/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.entity.data.EnterpriseObject_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(SubscriberAccount.class)
public class SubscriberAccount_ extends EnterpriseObject_{
    
    public static volatile SingularAttribute<SubscriberAccount,String> EMAIL;
    public static volatile SingularAttribute<SubscriberAccount,String> SUBSCRIBER_STATUS;
}
