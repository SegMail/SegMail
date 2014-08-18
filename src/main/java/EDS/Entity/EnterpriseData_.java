/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package EDS.Entity;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author KH
 */
@StaticMetamodel(EnterpriseData.class)
public class EnterpriseData_ extends AuditedObject_ {
    
    public static volatile SingularAttribute<EnterpriseData,EnterpriseObject> OWNER;
    public static volatile SingularAttribute<EnterpriseData,java.sql.Date> START_DATE;
    public static volatile SingularAttribute<EnterpriseData,java.sql.Date> END_DATE;
    public static volatile SingularAttribute<EnterpriseData,Integer> SNO;
}
