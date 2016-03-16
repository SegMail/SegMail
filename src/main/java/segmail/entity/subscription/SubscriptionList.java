/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.entity.data.EnterpriseObject;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="SUBSCRIPTION_LIST")
//@ClientResource
//@DiscriminatorColumn(name="SUBSCRIPTION_LIST")
public class SubscriptionList extends EnterpriseObject {// implements MailSender {
    
    /**
     * Private name viewable only by the list owners
     */
    private String LIST_NAME;
    
    /**
     * The email displayed in the From field when viewed by the recipient.
     */
    private String SEND_AS_EMAIL;
    
    /**
     * The name displayed in the From field when viewed by the recipient.
     */
    private String SEND_AS_NAME;
    
    /**
     * Indicates if the list requires double opt-in method of confirming subscribers.
     * It is by default TRUE.
     */
    private boolean DOUBLE_OPTIN;
    
    /**
     * Indicates if the list is stored in a remote location instead of in the 
     * local server.
     */
    private boolean REMOTE;
    
    /**
     * Current number of subscribers by subscription.
     */
    private int SUBSCRIBER_COUNT;
    
    /**
     * The address the recipient is sending to when they click "Reply" in the 
     * email.
     */
    private String REPLY_TO_ADDRESS;
    
    /**
     * The email address that bounces are returned to. 
     * 
     * We will not use this at the moment, but SES own bounce handling instead:
     * https://sesblog.amazon.com/post/TxJE1JNZ6T9JXK/Handling-Bounces-and-Complaints
     */
    //private String RETURN_PATH;

    public SubscriptionList() {
        this.REMOTE = true; //Default value
        this.DOUBLE_OPTIN = true;
    }
    
    
    
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

    
    public boolean isDOUBLE_OPTIN() {
        return DOUBLE_OPTIN;
    }

    public void setDOUBLE_OPTIN(boolean DOUBLE_OPTIN) {
        this.DOUBLE_OPTIN = DOUBLE_OPTIN;
    }

    public boolean isREMOTE() {
        return REMOTE;
    }

    public void setREMOTE(boolean REMOTE) {
        this.REMOTE = REMOTE;
    }

    public int getSUBSCRIBER_COUNT() {
        return SUBSCRIBER_COUNT;
    }

    public void setSUBSCRIBER_COUNT(int SUBSCRIBER_COUNT) {
        this.SUBSCRIBER_COUNT = SUBSCRIBER_COUNT;
    }

    public String getREPLY_TO_ADDRESS() {
        return REPLY_TO_ADDRESS;
    }

    public void setREPLY_TO_ADDRESS(String REPLY_TO_ADDRESS) {
        this.REPLY_TO_ADDRESS = REPLY_TO_ADDRESS;
    }
    
}
