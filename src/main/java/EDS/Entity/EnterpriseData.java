/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package EDS.Entity;

import java.sql.Date;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name="ENTERPRISEDATA")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class EnterpriseData extends AuditedObject{
    
    protected EnterpriseObject OWNER;
    protected java.sql.Date START_DATE;
    protected java.sql.Date END_DATE;
    protected int SNO;
    protected String CHANGED_BY;
    protected Date DATE_CREATED;
    protected String CREATED_BY;

    
    /**
     * 
     * @return 
     */
    @Id @ManyToOne(fetch=FetchType.LAZY) //For performance's sake
    public EnterpriseObject getOWNER() {
        return OWNER;
    }

    public void setOWNER(EnterpriseObject OWNER) {
        this.OWNER = OWNER;
    }

    @Id 
    public Date getSTART_DATE() {
        return START_DATE;
    }

    public void setSTART_DATE(Date START_DATE) {
        this.START_DATE = START_DATE;
    }

    @Id 
    public Date getEND_DATE() {
        return END_DATE;
    }

    public void setEND_DATE(Date END_DATE) {
        this.END_DATE = END_DATE;
    }

    @Id 
    public int getSNO() {
        return SNO;
    }

    public void setSNO(int SNO) {
        this.SNO = SNO;
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
