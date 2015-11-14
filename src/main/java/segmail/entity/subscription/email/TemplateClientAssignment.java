/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.email;

import eds.entity.client.Client;
import eds.entity.data.EnterpriseRelationship;
import eds.entity.user.UserType;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * This class was supposed to be extended by subclasses but then we realize it 
 * was really a hassle to create new relationship classes so we just stick with this.
 * Also, when you create templates, you create all types of templates in one
 * place. As a user, you need to pull all templates from the database ignoring
 * its type.
 * 
 * @author LeeKiatHaw
 * @param <E>
 */
@Entity
@Table(name="TEMPLATE_CLIENT_ASSIGNMENT")
public class TemplateClientAssignment<E extends EmailTemplate> extends EnterpriseRelationship<E,Client> {

    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
