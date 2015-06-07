/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.resource;

import eds.entity.data.EnterpriseObject;
import eds.entity.data.EnterpriseRelationship;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 * @param <S>
 * @param <T>
 */
@Entity
@Table(name="SYSTEM_RESOURCE_ASSIGNMENT")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class SystemResourceAssignment<S extends SystemResource, T extends EnterpriseObject>
        extends EnterpriseRelationship<SystemResource,EnterpriseObject> {
    
}
