/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.subscription;

import eds.entity.data.EnterpriseObject;
import eds.entity.data.EnterpriseRelationship;
import eds.entity.data.EnterpriseRelationship_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(Subscription.class)
public class Subscription_ extends EnterpriseRelationship_ {
    //public static volatile SingularAttribute<EnterpriseRelationship,? extends EnterpriseObject> SOURCE;
    //public static volatile SingularAttribute<EnterpriseRelationship,? extends EnterpriseObject> TARGET;
    public static volatile SingularAttribute<Subscription,Subscription.STATUS> STATUS;
}
