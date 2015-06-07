/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.subscription.email;

import eds.entity.client.Client;
import eds.entity.data.EnterpriseRelationship;
import eds.entity.user.UserType;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="TEMPLATE_CLIENT_ASSIGNMENT")
public class TemplateClientAssignment extends EnterpriseRelationship<EmailTemplate,Client> {

    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
