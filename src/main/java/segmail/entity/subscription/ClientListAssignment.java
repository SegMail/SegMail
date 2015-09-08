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
    
    private boolean ABLE_TO_EDIT;
    private boolean ABLE_TO_ADD;
    private boolean ABLE_TO_REMOVE;

    public ClientListAssignment() {
    }

    public boolean isABLE_TO_EDIT() {
        return ABLE_TO_EDIT;
    }

    public void setABLE_TO_EDIT(boolean ABLE_TO_EDIT) {
        this.ABLE_TO_EDIT = ABLE_TO_EDIT;
    }

    public boolean isABLE_TO_ADD() {
        return ABLE_TO_ADD;
    }

    public void setABLE_TO_ADD(boolean ABLE_TO_ADD) {
        this.ABLE_TO_ADD = ABLE_TO_ADD;
    }

    public boolean isABLE_TO_REMOVE() {
        return ABLE_TO_REMOVE;
    }

    public void setABLE_TO_REMOVE(boolean ABLE_TO_REMOVE) {
        this.ABLE_TO_REMOVE = ABLE_TO_REMOVE;
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
