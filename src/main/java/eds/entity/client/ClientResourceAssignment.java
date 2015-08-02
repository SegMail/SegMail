/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.client;

import eds.entity.data.EnterpriseObject;
import eds.entity.data.EnterpriseRelationship;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

/**
 * After some experimentation, there is still no use for this assignment class.
 * Each domain currently defines their own assignment relationship to client 
 * and it doesn't seem beneficial to have a common assignment superclass at the 
 * moment.
 * 
 * @author LeeKiatHaw
 */
@Entity
@Table(name="CLIENT_RESOURCE_ASSIGNMENT")
public class ClientResourceAssignment extends EnterpriseRelationship<Client,EnterpriseObject>{

    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
