/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign;

import javax.persistence.PrePersist;

/**
 *
 * @author LeeKiatHaw
 */
public class Trigger_Email_Activity_Listener {
    
    @PrePersist
    public void prePersist(Trigger_Email_Activity trigger) {
        setEmail(trigger);
    }
    
    public void setEmail(Trigger_Email_Activity trigger) {
        if(trigger.getTRIGGERED_TRANSACTION() != null) {
            for(String email : trigger.getTRIGGERED_TRANSACTION().getRECIPIENTS()) {
                trigger.setSUBSCRIBER_EMAIL(email);
            }
        }
            
    }
}
