/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.entity.people;

import eds.entity.data.EnterpriseData;
import java.sql.Date;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="PERSONAL_PARTICULARS")
public class PersonalParticulars extends EnterpriseData<Employee>{

    public enum GENDER_KEY{
        MALE,
        FEMALE
    }
    
    private String FIRSTNAME;
    private String LASTNAME;
    private java.sql.Date DOB;
    private GENDER_KEY GENDER;

    public String getFIRSTNAME() {
        return FIRSTNAME;
    }

    public void setFIRSTNAME(String FIRSTNAME) {
        this.FIRSTNAME = FIRSTNAME;
    }

    public String getLASTNAME() {
        return LASTNAME;
    }

    public void setLASTNAME(String LASTNAME) {
        this.LASTNAME = LASTNAME;
    }

    public Date getDOB() {
        return DOB;
    }

    public void setDOB(Date DOB) {
        this.DOB = DOB;
    }

    public GENDER_KEY getGENDER() {
        return GENDER;
    }

    public void setGENDER(GENDER_KEY GENDER) {
        this.GENDER = GENDER;
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
