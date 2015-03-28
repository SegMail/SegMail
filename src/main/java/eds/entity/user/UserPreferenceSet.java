/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.user;

import eds.entity.EnterpriseData;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This is to store all non-system user setting preferences that can be set by 
 * external components/modules. The settings here should be transparent to UserService.
 * UserService should not touch any of the preferences, but merely retrieves and
 * stores it. If there is a setting that is required to be accessed directly by 
 * UserService, then it should be a system setting in User class.
 * 
 * @author LeeKiatHaw
 */
@Entity
@Table(name="USER_PREFERENCE_SET")
public class UserPreferenceSet extends EnterpriseData implements Serializable {
    
    private Map<String,String> PREFERENCES = new HashMap<String,String>();

    //@MapKey(name="name")
    @ElementCollection
    public Map<String,String> getPREFERENCES() {
        return PREFERENCES;
    }

    public void setPREFERENCES(Map<String,String> PREFERENCES) {
        this.PREFERENCES = PREFERENCES;
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
