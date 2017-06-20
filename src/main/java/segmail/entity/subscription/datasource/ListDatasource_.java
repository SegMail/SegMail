/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.datasource;

import eds.entity.data.EnterpriseData_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(ListDatasource.class)
public class ListDatasource_ extends EnterpriseData_ {
    public static volatile SingularAttribute<ListDatasource,String> NAME;
    public static volatile SingularAttribute<ListDatasource,String> ENDPOINT_TYPE;
    public static volatile SingularAttribute<ListDatasource,String> SERVER_NAME;
    public static volatile SingularAttribute<ListDatasource,String> DB_NAME;
    public static volatile SingularAttribute<ListDatasource,String> DESCRIPTION;
    public static volatile SingularAttribute<ListDatasource,Integer> PORT;
    public static volatile SingularAttribute<ListDatasource,String> USERNAME;
    public static volatile SingularAttribute<ListDatasource,String> PASSWORD;
    public static volatile SingularAttribute<ListDatasource,Boolean> ACTIVE;
    public static volatile SingularAttribute<ListDatasource,String> STATUS_FIELD;
    public static volatile SingularAttribute<ListDatasource,String> TABLE_NAME;
    public static volatile SingularAttribute<ListDatasource,String> KEY_FIELD;
    public static volatile SingularAttribute<ListDatasource,java.sql.Timestamp> LAST_SYNC;
    public static volatile SingularAttribute<ListDatasource,Integer> LAST_SYNC_RESULT;
}
