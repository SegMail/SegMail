/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.email;

import eds.entity.data.EnterpriseRelationship;
import segmail.entity.subscription.SubscriptionList;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 * @param <E>
 * @param <SubscriptionList>
 */
@Entity
@Table(name="TEMPLATE_LIST_ASSIGNMENT")
public abstract class TemplateListAssignment<E extends EmailTemplate> extends EnterpriseRelationship<E,SubscriptionList> {

    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
