/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.subscription;

import eds.entity.data.EnterpriseObject_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(SubscriptionList.class)
public class SubscriptionList_ extends EnterpriseObject_ {
    public static volatile SingularAttribute<SubscriptionList,String> LIST_NAME;
    public static volatile SingularAttribute<SubscriptionList,String> SEND_AS_EMAIL;
    public static volatile SingularAttribute<SubscriptionList,String> SEND_AS_NAME;
    public static volatile SingularAttribute<SubscriptionList,SubscriptionList.LOCATION> LOCATION;   
    public static volatile SingularAttribute<SubscriptionList,Boolean> DOUBLE_OPTIN;
    
}
