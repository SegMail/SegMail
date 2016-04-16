/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.client;

import eds.entity.data.EnterpriseRelationship;
import eds.entity.user.User;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This represents a Client->User assignment, which means the user belongs to the
 * client group. Eg. The first user assigned is the "root"/master administrator
 * of all client resource - "SegMail" is registered as the client, "admin1" is the 
 * first ever user assigned. admin1 can then go ahead to create user accounts 
 * "admin2", "admin3", etc, who will then also have access to SegMail's stuff.
 * 
 * @author LeeKiatHaw
 */
@Entity
@Table(name="CLIENT_USER_ASSIGNMENT")
public class ClientUserAssignment extends EnterpriseRelationship<Client,User> {

    public ClientUserAssignment() {
    }

    public ClientUserAssignment(Client s, User t) {
        super(s, t);
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
