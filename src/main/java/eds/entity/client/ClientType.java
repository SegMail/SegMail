/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.client;

import eds.entity.EnterpriseObject;
import eds.entity.program.ProgramListener;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="CLIENTTYPE")
@EntityListeners(ClientTypeListener.class)
public class ClientType extends EnterpriseObject {
    
    private String CLIENT_TYPE_NAME;

    public String getCLIENT_TYPE_NAME() {
        return CLIENT_TYPE_NAME;
    }

    public void setCLIENT_TYPE_NAME(String CLIENT_TYPE_NAME) {
        this.CLIENT_TYPE_NAME = CLIENT_TYPE_NAME;
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
