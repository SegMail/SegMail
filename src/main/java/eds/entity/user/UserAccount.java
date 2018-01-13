/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.user;

import eds.entity.data.EnterpriseData;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author KH
 */
@Entity
@Table(name="USERACCOUNT")
public class UserAccount extends EnterpriseData<User> {
    
    private String USERNAME;
    private String PASSWORD;
    private boolean USER_LOCKED;
    private int UNSUCCESSFUL_ATTEMPTS;
    private java.util.Date LAST_UNSUCCESS_ATTEMPT; //Timestamp
    private String CONTACT_EMAIL; //Important!
    
    //[20150314] Put this here temporarily until there is a need to put it somewhere else
    private String PROFILE_PIC_URL;
    
    private String API_KEY;
    
    private java.sql.Timestamp LAST_LOGIN;
    private boolean FIRST_LOGIN;

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
    
    public String getPROFILE_PIC_URL(){
        return this.PROFILE_PIC_URL;
    }
    
    public void setPROFILE_PIC_URL(String PROFILE_PIC_URL){
        this.PROFILE_PIC_URL = PROFILE_PIC_URL;
    }

    public String getAPI_KEY() {
        return API_KEY;
    }

    public void setAPI_KEY(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    public String getCONTACT_EMAIL() {
        return CONTACT_EMAIL;
    }

    public void setCONTACT_EMAIL(String CONTACT_EMAIL) {
        this.CONTACT_EMAIL = CONTACT_EMAIL;
    }

    public Timestamp getLAST_LOGIN() {
        return LAST_LOGIN;
    }

    public void setLAST_LOGIN(Timestamp LAST_LOGIN) {
        this.LAST_LOGIN = LAST_LOGIN;
    }

    public boolean isFIRST_LOGIN() {
        return FIRST_LOGIN;
    }

    public void setFIRST_LOGIN(boolean FIRST_LOGIN) {
        this.FIRST_LOGIN = FIRST_LOGIN;
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
    public String HTMLName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
