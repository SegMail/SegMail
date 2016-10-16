/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.user;

import eds.component.encryption.EncryptionType;
import eds.component.encryption.EncryptionUtility;
import eds.entity.data.EnterpriseData;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 * This should be under the Landing component instead of User component.
 * 
 * @author LeeKiatHaw
 */
@Entity
@Table(name="API_ACCOUNT")
public class APIAccount extends EnterpriseData<User> {

    private String APIKEY;

    public String getAPIKEY() {
        return APIKEY;
    }

    public void setAPIKEY(String APIKEY) {
        this.APIKEY = APIKEY;
    }

    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        User owner = getOWNER();
        String keyToBeEncrypted = owner.getOBJECTID() + owner.getOBJECT_NAME() + getSTART_DATE() + getEND_DATE() + getDATE_CHANGED() + getSNO() + getVersion() + this;
        String key = EncryptionUtility.getHash(keyToBeEncrypted, EncryptionType.SHA256);
        setAPIKEY(key);
        
        return key;
    }

    @Override
    public String HTMLName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
