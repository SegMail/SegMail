/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.user;

import eds.entity.EnterpriseObject_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author KH
 */
@StaticMetamodel(UserType.class)
public class UserType_ extends EnterpriseObject_{
    public static volatile SingularAttribute<UserType,String> USERTYPENAME;
    public static volatile SingularAttribute<UserType,String> DESCRIPTION;
    public static volatile SingularAttribute<UserType,Boolean> PORTAL_ACCESS;
    public static volatile SingularAttribute<UserType,Boolean> WS_ACCESS;
    
}
