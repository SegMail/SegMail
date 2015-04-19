/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.resource;

import eds.entity.data.EnterpriseObject;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="SYSTEM_RESOURCE")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public abstract class SystemResource extends EnterpriseObject {
    
}
