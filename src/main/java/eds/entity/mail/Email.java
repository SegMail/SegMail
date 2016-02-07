/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.mail;

import eds.entity.document.Document;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import org.joda.time.DateTime;

/**
 * Email is an Document sent out by a Client
 * 
 * @author LeeKiatHaw
 * @param <R>
 */
//@Entity
//@Table(name="EMAIL")
public abstract class Email<R extends MailRecipient> extends Document<MailSender> {
    
    public enum STATUS{
        DRAFT,
        SCHEDULED,
        SENT
    }
    
    protected STATUS EMAIL_STATUS;
    
    /**
     * This is only important for an email in Scheduled status. It will be the 
     * planned time that the email was supposed to be sent. However, there is no
     * guarantee that it will be sent on time, it will depend on the scheduling 
     * mechanism such as cron jobs or server daemons that will push it out on time.
     */
    protected java.sql.Timestamp SCHEDULED_TIME;
    
    protected String SUBJECT;
    
    protected String BODY;
    
    protected List<R> RECIPIENTS = new ArrayList<R>();
    
    protected List<R> CC = new ArrayList<R>();
    
    protected List<R> BCC = new ArrayList<R>();

    public STATUS getEMAIL_STATUS() {
        return EMAIL_STATUS;
    }

    public void setEMAIL_STATUS(STATUS EMAIL_STATUS) {
        this.EMAIL_STATUS = EMAIL_STATUS;
    }

    public String getSUBJECT() {
        return SUBJECT;
    }

    public void setSUBJECT(String SUBJECT) {
        this.SUBJECT = SUBJECT;
    }

    public String getBODY() {
        return BODY;
    }

    public void setBODY(String BODY) {
        this.BODY = BODY;
    }

    @OneToMany(targetEntity=MailRecipient.class)
    public List<R> getRECIPIENTS() {
        return RECIPIENTS;
    }

    public void setRECIPIENTS(List<R> RECIPIENTS) {
        this.RECIPIENTS = RECIPIENTS;
    }

    @OneToMany(targetEntity=MailRecipient.class)
    public List<R> getCC() {
        return CC;
    }

    public void setCC(List<R> CC) {
        this.CC = CC;
    }

    @OneToMany(targetEntity=MailRecipient.class)
    public List<R> getBCC() {
        return BCC;
    }

    public void setBCC(List<R> BCC) {
        this.BCC = BCC;
    }

    public Timestamp getSCHEDULED_TIME() {
        return SCHEDULED_TIME;
    }

    public void setSCHEDULED_TIME(Timestamp SCHEDULED_TIME) {
        this.SCHEDULED_TIME = SCHEDULED_TIME;
    }

    
    /**
     * An email only has 1 sender/author
     * 
     * @return 
     */
    @Transient
    public MailSender getAUTHOR(){
        return (AUTHORS == null || AUTHORS.isEmpty()) ? null : AUTHORS.get(0);
    }
    
    public void setAUTHOR(MailSender sender){
        if(!AUTHORS.isEmpty()) AUTHORS.clear();
        AUTHORS.add(sender);
    }
    
    /**
     * Adds a new <? extends Recipient> to the list of Recipients of this email
     * 
     * @param recipient 
     */
    public abstract void addRecipient(R recipient);
    
    /**
     * Sets status to schedule. If null is passed in, set as to be sent immediately.
     * 
     * @param ts
     */
    public void schedule(DateTime ts){
        this.EMAIL_STATUS = STATUS.SCHEDULED;
        
        if(ts == null){
            //set it to immediately
            ts = new DateTime();
        }
        
        this.SCHEDULED_TIME = new java.sql.Timestamp(ts.getMillis());
    }
    
}
