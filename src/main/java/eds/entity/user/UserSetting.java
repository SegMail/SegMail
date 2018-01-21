/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.user;

import eds.entity.data.EnterpriseData;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Quick and dirty way to store user settings
 * 
 * @author LeeKiatHaw
 */
@Entity
@Table(name="USER_SETTING")
public class UserSetting extends EnterpriseData<User> {
    
    private String NAME;
    private String VALUE;

    public UserSetting() {
    }
    
    public UserSetting(String NAME, String VALUE) {
        this.NAME = NAME;
        this.VALUE = VALUE;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getVALUE() {
        return VALUE;
    }

    public void setVALUE(String VALUE) {
        this.VALUE = VALUE;
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
