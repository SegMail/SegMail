/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.mail;

import eds.entity.transaction.EnterpriseTransaction;
import eds.entity.transaction.TransactionStatus;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.Table;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="EMAIL"
        ,indexes={
            @Index(name="MailServiceOutbound",
                    columnList="TRANSACTION_ID,PROCESSING_STATUS,SCHEDULED_DATETIME,DATETIME_CHANGED,AWS_SES_MESSAGE_ID"),
            @Index(name="MailServiceInbound",
                    columnList="AWS_SES_MESSAGE_ID"),
        })
public abstract class Email extends EnterpriseTransaction {
    
    public static final String CREATED_FROM = "CREATED_FROM";
    
    protected String SUBJECT;
    
    protected String BODY;
    
    protected String SENDER_ADDRESS;
    
    protected String SENDER_NAME;
    
    protected Set<String> RECIPIENTS = new HashSet();
    
    protected Set<String> REPLY_TO_ADDRESSES = new HashSet();
    
    protected int RETRIES;
    
    protected String AWS_SES_MESSAGE_ID;
    
    public Email() {
        
    }

    public Email(Email anotherEmail) {
        super(anotherEmail);
        this.SUBJECT = anotherEmail.SUBJECT;
        this.BODY = anotherEmail.BODY;
        this.SENDER_ADDRESS = anotherEmail.SENDER_ADDRESS;
        this.SENDER_NAME = anotherEmail.SENDER_NAME;
        this.RETRIES = anotherEmail.RETRIES;
        this.AWS_SES_MESSAGE_ID = anotherEmail.AWS_SES_MESSAGE_ID;
        this.RECIPIENTS = new HashSet<>(anotherEmail.RECIPIENTS);
        this.REPLY_TO_ADDRESSES = new HashSet<>(anotherEmail.REPLY_TO_ADDRESSES);
    }
    
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

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "RECIPIENT")
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
        getRECIPIENTS().add(TO);
    }
    
    public void addSingleRecipient(String TO) {
        setRECIPIENTS(new HashSet());
        this.addRecipient(TO);
    }
    
    public void addReplyTo(String replyTo) {
        getREPLY_TO_ADDRESSES().add(replyTo);
    }

    public String getSENDER_NAME() {
        return SENDER_NAME;
    }

    public void setSENDER_NAME(String SENDER_NAME) {
        this.SENDER_NAME = SENDER_NAME;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    public Set<String> getREPLY_TO_ADDRESSES() {
        return REPLY_TO_ADDRESSES;
    }

    public void setREPLY_TO_ADDRESSES(Set<String> REPLY_TO_ADDRESSES) {
        this.REPLY_TO_ADDRESSES = REPLY_TO_ADDRESSES;
    }

    public void PROCESSING_STATUS(EMAIL_PROCESSING_STATUS status){
        this.setPROCESSING_STATUS(status.label);
    }

    public int getRETRIES() {
        return RETRIES;
    }

    public void setRETRIES(int RETRIES) {
        this.RETRIES = RETRIES;
    }

    public String getAWS_SES_MESSAGE_ID() {
        return AWS_SES_MESSAGE_ID;
    }

    public void setAWS_SES_MESSAGE_ID(String AWS_SES_MESSAGE_ID) {
        this.AWS_SES_MESSAGE_ID = AWS_SES_MESSAGE_ID;
    }

    @Override
    public EMAIL_PROCESSING_STATUS PROCESSING_STATUS() {
        if(PROCESSING_STATUS == null || PROCESSING_STATUS.isEmpty())
            return null;
        
        // May throw IllegalArgumentException, but it may as well be a RuntimeException
        // so let it throw
        return EMAIL_PROCESSING_STATUS.valueOf(PROCESSING_STATUS); 
    }

    @Override
    public Email transit(TransactionStatus newStatus, DateTime dt) {
        // If either old or new statuses are null
        if(newStatus.getStatus() == null || this.PROCESSING_STATUS() == null)
            return this;
        
        // If no change in status
        if(newStatus.getStatus().equals(this.PROCESSING_STATUS))
            return this;
        
        if(newStatus.getStatus().equals(EMAIL_PROCESSING_STATUS.QUEUED.getStatus()))
            return new QueuedEmail(this,dt);
        
        if(newStatus.getStatus().equals(EMAIL_PROCESSING_STATUS.SENT.getStatus()))
            return new SentEmail(this,dt);
        
        if(newStatus.getStatus().equals(EMAIL_PROCESSING_STATUS.BOUNCED.getStatus()))
            return new BouncedEmail(this,dt);
        
        if(newStatus.getStatus().equals(EMAIL_PROCESSING_STATUS.ERROR.getStatus()))
            return new ErrorEmail(this,dt);
        
        // If there is no dedicated class for this status, we return the same 
        // instance.
        this.setPROCESSING_STATUS(newStatus.getStatus());
        
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.TRANSACTION_KEY);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Email other = (Email) obj;
        if (!Objects.equals(this.TRANSACTION_KEY, other.TRANSACTION_KEY)) {
            return false;
        }
        return true;
    }
    
}
