/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.user;

import eds.entity.EnterpriseData_;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(UserType.class)
public class UserPreferenceSet_ extends EnterpriseData_{
    public static volatile MapAttribute<UserPreferenceSet,String,String> PREFERENCES;
}
