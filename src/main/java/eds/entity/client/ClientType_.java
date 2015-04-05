/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.client;

import eds.entity.layout.*;
import eds.entity.EnterpriseObject_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author KH
 */
@StaticMetamodel(ClientType.class)
public class ClientType_ extends EnterpriseObject_{
    public static volatile SingularAttribute<ClientType,String> CLIENT_TYPE_NAME;
    public static volatile SingularAttribute<ClientType,String> CLIENT_TYPE_DESCRIPTION;
}
