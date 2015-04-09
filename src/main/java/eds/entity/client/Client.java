/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.client;

import eds.entity.EnterpriseObject;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="CLIENT")
public class Client extends EnterpriseObject {

    private String CLIENT_NAME;
    
    private ClientType CLIENTTYPE;

    public String getCLIENT_NAME() {
        return CLIENT_NAME;
    }

    public void setCLIENT_NAME(String CLIENT_NAME) {
        this.CLIENT_NAME = CLIENT_NAME;
    }

    @ManyToOne
    public ClientType getCLIENTTYPE() {
        return CLIENTTYPE;
    }

    public void setCLIENTTYPE(ClientType CLIENTTYPE) {
        this.CLIENTTYPE = CLIENTTYPE;
    }
    
    
    
    @Override
    public void randInit() {
        int randInt = (int) (Math.random()*10000);
        this.CLIENT_NAME = "Client "+randInt;
        
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getAlias() {
        return this.CLIENT_NAME;
    }
    
}
