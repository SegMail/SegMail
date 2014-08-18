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
@StaticMetamodel(EnterpriseObject.class)
public class EnterpriseObject_ extends AuditedObject_{
    public static volatile SingularAttribute<EnterpriseObject,Long> OBJECTID;
    public static volatile SingularAttribute<EnterpriseObject,String> OBJECT_NAME;
    public static volatile SingularAttribute<EnterpriseObject,java.sql.Date> START_DATE;
    public static volatile SingularAttribute<EnterpriseObject,java.sql.Date> END_DATE;
    public static volatile SingularAttribute<EnterpriseObject,String> SEARCH_TERM;
    
}
