/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.subscription.email;

import eds.entity.data.EnterpriseRelationship;
import eds.entity.subscription.SubscriptionList;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="TEMPLATE_LIST_ASSIGNMENT")
public class TemplateListAssignment extends EnterpriseRelationship<EmailTemplate,SubscriptionList> {

    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
