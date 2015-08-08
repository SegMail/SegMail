/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.entity.client.ClientResource;
import eds.entity.data.EnterpriseObject;
import eds.entity.document.Document;
import eds.entity.mail.MailSender;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="SUBSCRIPTION_LIST")
@ClientResource
//@DiscriminatorColumn(name="SUBSCRIPTION_LIST")
public class SubscriptionList extends EnterpriseObject implements MailSender {

    public static enum LOCATION{
        REMOTE,
        LOCAL
    }
    
    private String LIST_NAME;
    private String SEND_AS_EMAIL;
    private String SEND_AS_NAME;
    private LOCATION LOCATION;
    private boolean DOUBLE_OPTIN;
    
    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String alias() {
        return this.LIST_NAME;
    }

    public String getLIST_NAME() {
        return LIST_NAME;
    }

    public void setLIST_NAME(String LIST_NAME) {
        this.LIST_NAME = LIST_NAME;
    }

    public String getSEND_AS_EMAIL() {
        return SEND_AS_EMAIL;
    }

    public void setSEND_AS_EMAIL(String SEND_AS_EMAIL) {
        this.SEND_AS_EMAIL = SEND_AS_EMAIL;
    }

    public String getSEND_AS_NAME() {
        return SEND_AS_NAME;
    }

    public void setSEND_AS_NAME(String SEND_AS_NAME) {
        this.SEND_AS_NAME = SEND_AS_NAME;
    }

    @Enumerated(EnumType.STRING)
    public LOCATION getLOCATION() {
        return LOCATION;
    }

    public void setLOCATION(LOCATION LOCATION) {
        this.LOCATION = LOCATION;
    }

    public boolean isDOUBLE_OPTIN() {
        return DOUBLE_OPTIN;
    }

    public void setDOUBLE_OPTIN(boolean DOUBLE_OPTIN) {
        this.DOUBLE_OPTIN = DOUBLE_OPTIN;
    }
    
    @Override
    public String getAddress() {
        return this.SEND_AS_EMAIL;
    }

    @Override
    public Document createDocument(Map<String, Object> ideas) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSignature() {
        return this.SEND_AS_EMAIL;
    }

    @Override
    public String getName() {
        return this.SEND_AS_NAME;
    }
    
    @Override
    public long getAuthorId() {
        return this.OBJECTID;
    }
    
    @Override
    public void setAddress(String address) {
        this.SEND_AS_EMAIL = address;
    }

    @Override
    public void setAuthorId(long id) {
        this.OBJECTID = id;
    }

    @Override
    public void setSignature(String signature) {
        this.SEND_AS_EMAIL = signature;
    }

    @Override
    public void setName(String name) {
        this.SEND_AS_NAME = name;
    }

}
