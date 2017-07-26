/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.user;

import eds.entity.transaction.EnterpriseTransactionTrigger;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="TRIGGER_PASSWORD_USER")
public class Trigger_Password_User extends EnterpriseTransactionTrigger<PasswordResetRequest,User>{
    private String TRIGGERED_EMAIL;

    public String getTRIGGERED_EMAIL() {
        return TRIGGERED_EMAIL;
    }

    public void setTRIGGERED_EMAIL(String TRIGGERED_EMAIL) {
        this.TRIGGERED_EMAIL = TRIGGERED_EMAIL;
    }
    
}
