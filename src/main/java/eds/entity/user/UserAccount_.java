/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.user;

import eds.entity.EnterpriseData_;
import eds.entity.EnterpriseObject_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author KH
 */
@StaticMetamodel(UserAccount.class)
public class UserAccount_ extends EnterpriseData_ {
    public static volatile SingularAttribute<UserAccount,String> USERNAME;
    public static volatile SingularAttribute<UserAccount,String> PASSWORD;
    public static volatile SingularAttribute<UserAccount,Boolean> USER_LOCKED;
    public static volatile SingularAttribute<UserAccount,Integer> UNSUCCESSFUL_ATTEMPTS;
    public static volatile SingularAttribute<UserAccount,java.sql.Date> LAST_UNSUCCESS_ATTEMPT;
    public static volatile SingularAttribute<UserAccount,String> PROFILE_PIC_URL;
}
