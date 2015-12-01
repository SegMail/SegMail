/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.email;

import eds.entity.data.EnterpriseObject;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import segmail.entity.subscription.email.AutoEmailTypeFactory.TYPE;

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
@Table(name="AUTORESPONDER_EMAIL")
@DiscriminatorValue("AutoresponderEmail")
public abstract class AutoresponderEmail extends EnterpriseObject {
    
    /**
    public enum EMAIL_TYPE {
        CONFIRMATION,
        NEWSLETTER,
        AUTORESPONDER
    }
    
    protected EMAIL_TYPE TYPE;*/
    
    protected String SUBJECT;
    
    protected String BODY;

    public String getSUBJECT() {
        return SUBJECT;
    }

    public void setSUBJECT(String SUBJECT) {
        this.SUBJECT = SUBJECT;
    }
    
    /**
     * Each subclass should implement it and return an enumeration that is defined
 in AutoEmailTypeFactory.
     * 
     * @return TYPE
     */
    //@Transient
    public abstract TYPE type();

    //@Lob //Causing a java.lang.AbstractMethodError
    @Column(columnDefinition="MEDIUMTEXT")
    public String getBODY() {
        return BODY;
    }

    public void setBODY(String BODY) {
        this.BODY = BODY;
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
        return this.SUBJECT;
    }
    
}
