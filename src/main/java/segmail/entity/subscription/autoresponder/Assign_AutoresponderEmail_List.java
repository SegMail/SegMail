/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.autoresponder;

import eds.entity.data.EnterpriseRelationship;
import javax.persistence.DiscriminatorColumn;
import segmail.entity.subscription.SubscriptionList;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 * @param <E>
 */
@Entity
@Table(name="ASSIGN_AUTORESPONDEREMAIL_LIST")
//@DiscriminatorColumn(name="Assign_AutoresponderEmail_List")
public class Assign_AutoresponderEmail_List extends EnterpriseRelationship<AutoresponderEmail,SubscriptionList> {

    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
