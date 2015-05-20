/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.user;

import eds.entity.data.EnterpriseData_;
import eds.entity.data.EnterpriseObject_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author KH
 */
@StaticMetamodel(APIAccount.class)
public class APIAccount_ extends EnterpriseData_ {
    public static volatile SingularAttribute<APIAccount,String> APIKey;
}
