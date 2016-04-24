/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.entity.landing;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(ServerJNDIResource.class)
public class ServerJNDIResource_ {
    public static volatile SingularAttribute<ServerJNDIResource,String> RESOURCE_TYPE;
    public static volatile SingularAttribute<ServerJNDIResource,String> RESOURCE_KEY;
    public static volatile SingularAttribute<ServerJNDIResource,String> RESOURCE_VALUE;
}
