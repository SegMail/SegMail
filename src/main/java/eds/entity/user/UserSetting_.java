/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.user;

import eds.entity.data.EnterpriseData_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(UserSetting.class)
public class UserSetting_ extends EnterpriseData_ {
    
    public static volatile SingularAttribute<UserSetting,String> NAME;
    public static volatile SingularAttribute<UserSetting,String> VALUE;
}
