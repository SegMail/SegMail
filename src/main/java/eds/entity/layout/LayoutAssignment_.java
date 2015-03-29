/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.layout;

import eds.entity.navigation.*;
import eds.entity.EnterpriseRelationship_;
import eds.entity.user.UserType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author KH
 */
@StaticMetamodel(LayoutAssignment.class)
public class LayoutAssignment_ extends EnterpriseRelationship_ {
    public static volatile SingularAttribute<UserType,Integer> PRIORITY;
}
