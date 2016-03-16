/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.email.mailmerge;

import eds.entity.transaction.EnterpriseTransaction;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="MAILMERGE_REQUEST")
@EntityListeners({
    MailMergeRequestListener.class})
public class MailMergeRequest extends EnterpriseTransaction {
    
}
