/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package EDS.Entity;

import java.sql.Date;
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
    protected int REL_SEQUENCE;
    protected String CHANGED_BY;
    protected Date DATE_CREATED;
    protected String CREATED_BY;

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
    
    @Id
    public String getREL_TYPE() {
        return REL_TYPE;
    }

    public void setREL_TYPE(String REL_TYPE) {
        this.REL_TYPE = REL_TYPE;
    }
    
    @Id
    public int getREL_SEQUENCE() {
        return REL_SEQUENCE;
    }

    public void setREL_SEQUENCE(int REL_SEQUENCE) {
        this.REL_SEQUENCE = REL_SEQUENCE;
    }

    /**
     * Testing method for initializing random values for a table row
     */
    public abstract void randInit();

    /**
     * Generates an object that represents the identifier of the row object
     *
     *
     * @return Object - Key of this table row
     */
    public abstract Object generateKey();
    
    
}
