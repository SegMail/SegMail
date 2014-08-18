/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package EDS.Entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author KH
 */
@Entity
@Table(name="ENTERPRISE_RELATIONSHIP")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class EnterpriseRelationship extends AuditedObject {
    
    protected EnterpriseObject SOURCE;
    protected EnterpriseObject TARGET;
    
    protected String REL_TYPE;

    @Id @ManyToOne
    public EnterpriseObject getSOURCE() {
        return SOURCE;
    }

    public void setSOURCE(EnterpriseObject SOURCE) {
        this.SOURCE = SOURCE;
    }

    @Id @ManyToOne
    public EnterpriseObject getTARGET() {
        return TARGET;
    }

    public void setTARGET(EnterpriseObject TARGET) {
        this.TARGET = TARGET;
    }

    public String getREL_TYPE() {
        return REL_TYPE;
    }

    public void setREL_TYPE(String REL_TYPE) {
        this.REL_TYPE = REL_TYPE;
    }
    
    
}
