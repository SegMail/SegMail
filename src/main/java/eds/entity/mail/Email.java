/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.mail;

import eds.entity.transaction.EnterpriseTransaction;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
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
    
    private String SENDER;
    
    private Set<String> RECIPIENTS = new HashSet();

    public String getSUBJECT() {
        return SUBJECT;
    }

    public void setSUBJECT(String SUBJECT) {
        this.SUBJECT = SUBJECT;
    }

    public String getSENDER() {
        return SENDER;
    }

    public void setSENDER(String SENDER) {
        this.SENDER = SENDER;
    }

    @ElementCollection
    public Set<String> getRECIPIENTS() {
        return RECIPIENTS;
    }

    public void setRECIPIENTS(Set<String> RECIPIENTS) {
        this.RECIPIENTS = RECIPIENTS;
    }

    public String getBODY() {
        return BODY;
    }

    public void setBODY(String BODY) {
        this.BODY = BODY;
    }
    
    public void addRecipient(String TO) {
        this.RECIPIENTS.add(TO);
    }
}
