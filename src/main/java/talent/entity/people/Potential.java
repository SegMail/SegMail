/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.entity.people;

import eds.entity.data.EnterpriseData;
import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import talent.entity.people.potential.PotentialLevel;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="POTENTIAL")
public class Potential extends EnterpriseData<Employee> {

    private java.sql.Date DATE_OF_ASSESSMENT;
    
    private PotentialLevel LEVEL;
    
    private String COMMENTS;

    public Date getDATE_OF_ASSESSMENT() {
        return DATE_OF_ASSESSMENT;
    }

    public void setDATE_OF_ASSESSMENT(Date DATE_OF_ASSESSMENT) {
        this.DATE_OF_ASSESSMENT = DATE_OF_ASSESSMENT;
    }

    @ManyToOne
    public PotentialLevel getLEVEL() {
        return LEVEL;
    }

    public void setLEVEL(PotentialLevel LEVEL) {
        this.LEVEL = LEVEL;
    }

    @Column(columnDefinition="MEDIUMTEXT")
    public String getCOMMENTS() {
        return COMMENTS;
    }

    public void setCOMMENTS(String COMMENTS) {
        this.COMMENTS = COMMENTS;
    }
    
    
    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
