/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.entity.organization;

import eds.entity.client.ClientResourceAssignment;

/**
 *
 * @author LeeKiatHaw
 */
//@Entity
//@Table(name="BUSINESSUNIT_CLIENT")
public class BusinessUnitClientAssignment extends ClientResourceAssignment {

    private boolean OWNER_FLAG;

    public boolean isOWNER_FLAG() {
        return OWNER_FLAG;
    }

    public void setOWNER_FLAG(boolean OWNER_FLAG) {
        this.OWNER_FLAG = OWNER_FLAG;
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
