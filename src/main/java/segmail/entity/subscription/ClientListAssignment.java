/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.entity.data.EnterpriseRelationship;
import eds.entity.client.Client;
import eds.entity.client.ClientResourceAssignment;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="CLIENT_LIST_ASSIGNMENT")
public class ClientListAssignment extends EnterpriseRelationship<Client,SubscriptionList> {//ClientResourceAssignment<Client,SubscriptionList>{
    
    //private boolean ABLE_TO_EDIT;
    //private boolean ABLE_TO_ADD;
    //private boolean ABLE_TO_REMOVE;
    
    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
