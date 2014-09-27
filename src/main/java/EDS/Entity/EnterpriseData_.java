/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package EDS.Entity;

import java.sql.Date;
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
    public static volatile SingularAttribute<AuditedObject, String> CHANGED_BY;
    public static volatile SingularAttribute<AuditedObject, Date> DATE_CREATED;
    public static volatile SingularAttribute<AuditedObject, String> CREATED_BY;
}
