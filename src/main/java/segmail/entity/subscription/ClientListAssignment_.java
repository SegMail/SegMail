/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.entity.data.EnterpriseRelationship_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(ClientListAssignment.class)
public class ClientListAssignment_ extends EnterpriseRelationship_{//EnterpriseRelationship<SubscriptionList,Client> {
    public static volatile SingularAttribute<ClientListAssignment,Boolean> ABLE_TO_EDIT;
    public static volatile SingularAttribute<ClientListAssignment,Boolean> ABLE_TO_ADD;
    public static volatile SingularAttribute<ClientListAssignment,Boolean> ABLE_TO_REMOVE;
    
}
