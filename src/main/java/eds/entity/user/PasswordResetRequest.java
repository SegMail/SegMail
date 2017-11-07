/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.user;

import eds.entity.transaction.EnterpriseTransaction;
import eds.entity.transaction.TransactionStatus;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="PASSWORD_RESET_REQUEST")
public class PasswordResetRequest extends EnterpriseTransaction {

    @Override
    public <Ts extends TransactionStatus> Ts PROCESSING_STATUS() {
        return null;
    }

    @Override
    public PasswordResetRequest transit(TransactionStatus newStatus, DateTime dt) {
        return this;
    }
    
}
