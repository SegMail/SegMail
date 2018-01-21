/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.user;

import eds.entity.data.EnterpriseObject;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Representation of the User object. Preferably stores only system preferences.
 * 
 * @author LeeKiatHaw
 */
@Entity
@Table(name="USER")
public class User extends EnterpriseObject {

    private UserType USERTYPE;

    @ManyToOne
    public UserType getUSERTYPE() {
        return USERTYPE;
    }

    public void setUSERTYPE(UserType USERTYPE) {
        this.USERTYPE = USERTYPE;
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
        //User is a special object, because it uses the UserAccount name as its object_name.
        return (OBJECT_NAME == null || OBJECT_NAME.isEmpty()) ? "USER "+this.OBJECTID : OBJECT_NAME;
    }
    
}
