/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.component.encryption.EncryptionType;
import eds.component.encryption.EncryptionUtility;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
public class SubscriptionListFieldListener {
    
    //Because injection doesn't work in Listeners now!
    EncryptionUtility encryptService = new EncryptionUtility();
    
    @PrePersist
    @PreUpdate
    public void prePersistUpdate(SubscriptionListField field){
        updateMailmergeTage(field);
        updateKeyName(field);
    }
    
    public void updateMailmergeTage(SubscriptionListField field){
        String fieldName = field.getFIELD_NAME();
        fieldName = fieldName.replace(" ", "-");
        fieldName = "{" + fieldName + "}";
        
        field.setMAILMERGE_TAG(fieldName);
    }
    
    public void updateKeyName(SubscriptionListField field) {
        if(field.getKEY_NAME() != null && !field.getKEY_NAME().isEmpty())
            return;
        
        String primarykey = Long.toString(field.getOWNER().getOBJECTID());
        primarykey += field.getSTART_DATE();
        primarykey += field.getEND_DATE();
        primarykey += field.getSNO();
        primarykey += field.getVersion();
        primarykey += DateTime.now().toString("YYYYMMddHHmmss");
        
        String hashkey = EncryptionUtility.getHash(primarykey, EncryptionType.MD5);
        
        field.setKEY_NAME(hashkey);
    }
}
