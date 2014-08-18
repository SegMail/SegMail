/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package EDS.Entity;

import java.io.Serializable;
import java.sql.Date;

/**
 *
 * @author KH
 */
public abstract class AuditedObject implements Serializable {
    
    protected java.sql.Date DATE_CHANGED;
    protected String CHANGED_BY;
    
    protected java.sql.Date DATE_CREATED;
    protected String CREATED_BY;
    
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

    public Date getDATE_CHANGED() {
        return DATE_CHANGED;
    }

    public void setDATE_CHANGED(Date DATE_CHANGED) {
        this.DATE_CHANGED = DATE_CHANGED;
    }

    public String getCHANGED_BY() {
        return CHANGED_BY;
    }

    public void setCHANGED_BY(String CHANGED_BY) {
        this.CHANGED_BY = CHANGED_BY;
    }

    public Date getDATE_CREATED() {
        return DATE_CREATED;
    }

    public void setDATE_CREATED(Date DATE_CREATED) {
        this.DATE_CREATED = DATE_CREATED;
    }

    public String getCREATED_BY() {
        return CREATED_BY;
    }

    public void setCREATED_BY(String CREATED_BY) {
        this.CREATED_BY = CREATED_BY;
    }
    
    
}
