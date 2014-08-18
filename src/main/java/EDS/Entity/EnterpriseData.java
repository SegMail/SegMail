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
@Table(name="ENTERPRISEDATA")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class EnterpriseData extends AuditedObject{
    
    protected EnterpriseObject OWNER;
    protected java.sql.Date START_DATE;
    protected java.sql.Date END_DATE;
    protected int SNO;

    @Id @ManyToOne
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
    
    
}
