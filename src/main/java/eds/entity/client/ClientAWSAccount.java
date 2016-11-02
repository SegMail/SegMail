/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.client;

import eds.entity.data.EnterpriseData;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="CLIENT_AWS_ACCOUNT")
public class ClientAWSAccount extends EnterpriseData<Client> {
    
    private String USERNAME;
    private String USERID;
    private String ARN;
    private String AWS_ACCESS_KEY_ID;
    private String AWS_SECRET_ACCESS_KEY;

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getUSERID() {
        return USERID;
    }

    public void setUSERID(String USERID) {
        this.USERID = USERID;
    }

    public String getARN() {
        return ARN;
    }

    public void setARN(String ARN) {
        this.ARN = ARN;
    }

    public String getAWS_ACCESS_KEY_ID() {
        return AWS_ACCESS_KEY_ID;
    }

    public void setAWS_ACCESS_KEY_ID(String AWS_ACCESS_KEY_ID) {
        this.AWS_ACCESS_KEY_ID = AWS_ACCESS_KEY_ID;
    }

    public String getAWS_SECRET_ACCESS_KEY() {
        return AWS_SECRET_ACCESS_KEY;
    }

    public void setAWS_SECRET_ACCESS_KEY(String AWS_SECRET_ACCESS_KEY) {
        this.AWS_SECRET_ACCESS_KEY = AWS_SECRET_ACCESS_KEY;
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
