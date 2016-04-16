/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.mail;

import eds.entity.transaction.EnterpriseTransaction;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="EMAIL")
public class Email extends EnterpriseTransaction {
    
    private String SUBJECT;
    
    private String BODY;
    
    private String SENDER_ADDRESS;
    
    private String SENDER_NAME;
    
    private Set<String> RECIPIENTS = new HashSet();
    
    private Set<String> REPLY_TO_ADDRESSES = new HashSet();

    public String getSUBJECT() {
        return SUBJECT;
    }

    public void setSUBJECT(String SUBJECT) {
        this.SUBJECT = SUBJECT;
    }

    public String getSENDER_ADDRESS() {
        return SENDER_ADDRESS;
    }

    public void setSENDER_ADDRESS(String SENDER_ADDRESS) {
        this.SENDER_ADDRESS = SENDER_ADDRESS;
    }

    @ElementCollection
    public Set<String> getRECIPIENTS() {
        return RECIPIENTS;
    }

    public void setRECIPIENTS(Set<String> RECIPIENTS) {
        this.RECIPIENTS = RECIPIENTS;
    }

    @Column(columnDefinition="MEDIUMTEXT")
    public String getBODY() {
        return BODY;
    }

    public void setBODY(String BODY) {
        this.BODY = BODY;
    }
    
    public void addRecipient(String TO) {
        this.RECIPIENTS.add(TO);
    }

    public String getSENDER_NAME() {
        return SENDER_NAME;
    }

    public void setSENDER_NAME(String SENDER_NAME) {
        this.SENDER_NAME = SENDER_NAME;
    }

    @ElementCollection
    public Set<String> getREPLY_TO_ADDRESSES() {
        return REPLY_TO_ADDRESSES;
    }

    public void setREPLY_TO_ADDRESSES(Set<String> REPLY_TO_ADDRESSES) {
        this.REPLY_TO_ADDRESSES = REPLY_TO_ADDRESSES;
    }

    
}
