/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.entity.landing;

import eds.entity.data.EnterpriseData_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(ServerResource.class)
public class ServerResource_ extends EnterpriseData_ {
    public static volatile SingularAttribute<ServerResource,String> RESOURCE_TYPE;
    public static volatile SingularAttribute<ServerResource,String> RESOURCE_NAME;
    public static volatile SingularAttribute<ServerResource,String> JNDI_NAME;
}
