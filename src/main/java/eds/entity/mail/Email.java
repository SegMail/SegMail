/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.mail;

import eds.entity.transaction.EnterpriseTransaction;
import java.util.List;

/**
 *
 * @author LeeKiatHaw
 */
public class Email extends EnterpriseTransaction {
    
    private String SUBJECT;
    
    private String BODY;
    
    private String FROM;
    
    private List<String> TO;

    public String getSUBJECT() {
        return SUBJECT;
    }

    public void setSUBJECT(String SUBJECT) {
        this.SUBJECT = SUBJECT;
    }

    public String getFROM() {
        return FROM;
    }

    public void setFROM(String FROM) {
        this.FROM = FROM;
    }

    public List<String> getTO() {
        return TO;
    }

    public void setTO(List<String> TO) {
        this.TO = TO;
    }

    public String getBODY() {
        return BODY;
    }

    public void setBODY(String BODY) {
        this.BODY = BODY;
    }
    
    
}
