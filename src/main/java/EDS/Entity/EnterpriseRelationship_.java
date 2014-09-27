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
@StaticMetamodel(EnterpriseRelationship.class)
public class EnterpriseRelationship_ extends AuditedObject_{
    
    public static volatile SingularAttribute<EnterpriseRelationship,EnterpriseObject> SOURCE;
    public static volatile SingularAttribute<EnterpriseRelationship,EnterpriseObject> TARGET;
    public static volatile SingularAttribute<EnterpriseRelationship,String> REL_TYPE;
    public static volatile SingularAttribute<AuditedObject, String> CHANGED_BY;
    public static volatile SingularAttribute<AuditedObject, Date> DATE_CREATED;
    public static volatile SingularAttribute<AuditedObject, String> CREATED_BY;
}
