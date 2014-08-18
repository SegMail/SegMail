/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.entity.user;

import EDS.Entity.EnterpriseObject;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author KH
 */
@Entity
@Table(name="USERENTITY")
@TableGenerator(name="USERENTITY_SEQ",initialValue=1,allocationSize=1,table="SEQUENCE")
public class UserAccount extends EnterpriseObject {
    
    private String USERNAME;
    private String PASSWORD;
    private boolean USER_LOCKED;
    private int UNSUCCESSFUL_ATTEMPTS;
    private java.util.Date LAST_UNSUCCESS_ATTEMPT; //Timestamp
    
    private UserType USERTYPE;

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public boolean isUSER_LOCKED() {
        return USER_LOCKED;
    }

    public void setUSER_LOCKED(boolean USER_LOCKED) {
        this.USER_LOCKED = USER_LOCKED;
    }

    public int getUNSUCCESSFUL_ATTEMPTS() {
        return UNSUCCESSFUL_ATTEMPTS;
    }

    public void setUNSUCCESSFUL_ATTEMPTS(int UNSUCCESSFUL_ATTEMPTS) {
        this.UNSUCCESSFUL_ATTEMPTS = UNSUCCESSFUL_ATTEMPTS;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public java.util.Date getLAST_UNSUCCESS_ATTEMPT() {
        return LAST_UNSUCCESS_ATTEMPT;
    }

    public void setLAST_UNSUCCESS_ATTEMPT(java.util.Date LAST_UNSUCCESS_ATTEMPT) {
        this.LAST_UNSUCCESS_ATTEMPT = LAST_UNSUCCESS_ATTEMPT;
    }

    @ManyToOne
    public UserType getUSERTYPE() {
        return USERTYPE;
    }

    public void setUSERTYPE(UserType USERTYPE) {
        this.USERTYPE = USERTYPE;
    }

    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
