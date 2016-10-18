/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.client;

import eds.entity.data.EnterpriseData;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="VERIFIED_SENDING_ADDRESS")
public class VerifiedSendingAddress extends EnterpriseData<Client> {
    
    private String VERIFIED_ADDRESS;

    public String getVERIFIED_ADDRESS() {
        return VERIFIED_ADDRESS;
    }

    public void setVERIFIED_ADDRESS(String VERIFIED_ADDRESS) {
        this.VERIFIED_ADDRESS = VERIFIED_ADDRESS;
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
    public String HTMLName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
