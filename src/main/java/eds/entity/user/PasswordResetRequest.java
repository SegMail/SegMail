/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.user;

import eds.entity.mail.Email;
import eds.entity.transaction.EnterpriseTransaction;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="PASSWORD_RESET_REQUEST")
public class PasswordResetRequest extends EnterpriseTransaction {
    
}
