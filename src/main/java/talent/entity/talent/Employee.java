/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.entity.talent;

import eds.entity.data.EnterpriseObject;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="EMPLOYEE")
public class Employee extends EnterpriseObject {

    private String PROFILE_PIC_LOCATION;

    public String getPROFILE_PIC_LOCATION() {
        return PROFILE_PIC_LOCATION;
    }

    public void setPROFILE_PIC_LOCATION(String PROFILE_PIC_LOCATION) {
        this.PROFILE_PIC_LOCATION = PROFILE_PIC_LOCATION;
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
    public String alias() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
