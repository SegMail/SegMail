/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.client;

import eds.entity.data.EnterpriseObject;
import eds.entity.document.Document;
import eds.entity.document.DocumentAuthor;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A Client represents a customer that signed up for a service. Eg. for an autoresponder,
 * the client is the one who signs up to use the autoresponder.
 * 
 * Implementing a DocumentAuthor allows this Entity to author a business document
 * such as an invoice or email.
 * 
 * @author LeeKiatHaw
 */
@Entity
@Table(name="CLIENT")
public class Client extends EnterpriseObject implements DocumentAuthor {

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
    public String alias() {
        return this.CLIENT_NAME;
    }

    @Override
    public long getAuthorId() {
        return this.getOBJECTID();
    }

    @Override
    public void setAuthorId(long id) {
        this.setOBJECTID(id);
    }

    @Override
    public Document createDocument(Map<String, Object> ideas) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSignature() {
        return this.getCLIENT_NAME();
    }

    @Override
    public void setSignature(String signature) {
        this.setCLIENT_NAME(signature);
    }

    @Override
    public String getName() {
        return this.getCLIENT_NAME();
    }

    @Override
    public void setName(String name) {
        this.setCLIENT_NAME(name);
    }
    
}
