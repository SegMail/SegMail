/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 *
 * @author LeeKiatHaw
 */
public class SubscriptionListFieldListener {
    
    @PrePersist
    @PreUpdate
    public void prePersistUpdate(SubscriptionListField field){
        updateMailmergeTage(field);
    }
    
    public void updateMailmergeTage(SubscriptionListField field){
        String fieldName = field.getFIELD_NAME();
        fieldName = fieldName.replace(" ", "-");
        fieldName = "!" + fieldName;
        
        field.setMAILMERGE_TAG(fieldName);
    }
}
