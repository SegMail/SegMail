/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.landing;

import eds.entity.data.EnterpriseRelationship;
import eds.entity.user.User;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="ASSIGN_SERVER_USER")
public class Assign_Server_User extends EnterpriseRelationship<ServerInstance,User> {

    public Assign_Server_User(ServerInstance newInstance, User user) {
        this.setSOURCE(newInstance);
        this.setTARGET(user);
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
