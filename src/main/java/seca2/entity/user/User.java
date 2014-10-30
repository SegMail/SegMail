/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.entity.user;

import EDS.Entity.EnterpriseObject;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * Representation of the User object. Preferably stores only system preferences.
 * 
 * @author LeeKiatHaw
 */
@Entity
@Table(name="USER")
@TableGenerator(name="USER_SEQ",initialValue=1,allocationSize=1,table="SEQUENCE")
public class User extends EnterpriseObject {

    
    
    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
