/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.email;

import eds.entity.data.EnterpriseObject;
import eds.entity.mail.Email;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * A template is actually different from the actual email sent. It has to exist
 * permanently in the system while the actual email is a transactional data that
 * can be archived and deleted. That is why we model the email template as an
 * EnterpriseData.
 * 
 * [20150531] As of now, there is no way of changing inheritance strategy along 
 * the branch of superclasses and subclasses. What we will do is to have a enumeration
 * field type here to denote the type of email template.
 * 
 * @author LeeKiatHaw
 */
@Entity
@Table(name="EMAIL_TEMPLATE")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="EMAIL_TYPE")
public class EmailTemplate extends EnterpriseObject {
//extends Document<Client> {
    
    public enum EMAIL_TYPE {
        CONFIRMATION,
        NEWSLETTER,
        AUTORESPONDER
    }
    
    protected EMAIL_TYPE TYPE;
    
    protected String SUBJECT;
    
    protected String BODY;

    public String getSUBJECT() {
        return SUBJECT;
    }

    public void setSUBJECT(String SUBJECT) {
        this.SUBJECT = SUBJECT;
    }

    //@Lob //Causing a java.lang.AbstractMethodError
    @Column(columnDefinition="MEDIUMTEXT")
    public String getBODY() {
        return BODY;
    }

    public void setBODY(String BODY) {
        this.BODY = BODY;
    }

    @Enumerated(EnumType.STRING)
    public EMAIL_TYPE getTYPE() {
        return TYPE;
    }

    public void setTYPE(EMAIL_TYPE TYPE) {
        this.TYPE = TYPE;
    }
    
    
    
    /**
     * Factory method for generating the concrete Email type
     * 
     * @return 
     */
    public Email generateEmail(){
        Email newMail = null;
        switch(this.TYPE){
            case CONFIRMATION   :   this.setTYPE(EMAIL_TYPE.CONFIRMATION);
                                    newMail = new ConfirmationEmail();
                                    break;
            default             :   throw new RuntimeException("Email type "+this.getTYPE()+" is not implemented yet.");
                                    
                
        }
        
        newMail.setSUBJECT(this.getSUBJECT());
        newMail.setBODY(this.getBODY());
        
        return newMail;
    }
    
    /*
    public EmailTemplate getTemplate(EMAIL_TYPE type){
        switch(type){
            case CONFIRMATION   :   this.setTYPE(EMAIL_TYPE.CONFIRMATION);
                                    return this;
            default             :   break;
                
        }
        return null;
    }*/
    
    
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
        return this.SUBJECT;
    }
}
