/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.entity.data.EnterpriseData_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(SubscriberFieldValue.class)
public class SubscriberFieldValue_ extends EnterpriseData_{
    
    public static volatile SingularAttribute<SubscriberFieldValue,String> FIELD_KEY;
    public static volatile SingularAttribute<SubscriberFieldValue,String> VALUE;
}
