/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.entity.talent;

import eds.entity.data.EnterpriseData;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="DEVELOPMENT_PLAN")
public class DevelopmentPlan extends EnterpriseData<Employee>{

    private String DEVELOPMENT_AREAS;
    private String DEVELOPMENT_ACTIONS;

    @Column(columnDefinition="MEDIUMTEXT")
    public String getDEVELOPMENT_AREAS() {
        return DEVELOPMENT_AREAS;
    }

    public void setDEVELOPMENT_AREAS(String DEVELOPMENT_AREAS) {
        this.DEVELOPMENT_AREAS = DEVELOPMENT_AREAS;
    }

    @Column(columnDefinition="MEDIUMTEXT")
    public String getDEVELOPMENT_ACTIONS() {
        return DEVELOPMENT_ACTIONS;
    }

    public void setDEVELOPMENT_ACTIONS(String DEVELOPMENT_ACTIONS) {
        this.DEVELOPMENT_ACTIONS = DEVELOPMENT_ACTIONS;
    }
    
    
    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getHTMLName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
