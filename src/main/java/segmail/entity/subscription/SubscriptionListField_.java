/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.entity.data.EnterpriseData;
import eds.entity.data.EnterpriseData_;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(SubscriptionListField.class)
public class SubscriptionListField_ extends EnterpriseData_{
    
    public static volatile SingularAttribute<SubscriptionListField,Boolean> MANDATORY;
    public static volatile SingularAttribute<SubscriptionListField,String> FIELD_NAME;
    public static volatile SingularAttribute<SubscriptionListField,String> KEY_NAME; //Immutable name to retrieve for a SubscriberFieldValue
    public static volatile SingularAttribute<SubscriptionListField,String> TYPE;
    public static volatile SingularAttribute<SubscriptionListField,String> DESCRIPTION;
    public static volatile SingularAttribute<SubscriptionListField,String> MAILMERGE_TAG;

}
