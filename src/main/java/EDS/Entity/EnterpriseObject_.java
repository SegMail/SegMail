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
@StaticMetamodel(EnterpriseObject.class)
public class EnterpriseObject_{
    public static volatile SingularAttribute<EnterpriseObject,Long> OBJECTID;
    public static volatile SingularAttribute<EnterpriseObject,String> OBJECT_NAME;
    public static volatile SingularAttribute<EnterpriseObject,java.sql.Date> START_DATE;
    public static volatile SingularAttribute<EnterpriseObject,java.sql.Date> END_DATE;
    public static volatile SingularAttribute<EnterpriseObject,String> SEARCH_TERM;
    public static volatile SingularAttribute<EnterpriseObject,java.sql.Date> DATE_CHANGED;
    public static volatile SingularAttribute<AuditedObject, String> CHANGED_BY;
    public static volatile SingularAttribute<AuditedObject, Date> DATE_CREATED;
    public static volatile SingularAttribute<AuditedObject, String> CREATED_BY;
    
}
