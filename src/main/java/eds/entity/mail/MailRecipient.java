/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.mail;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
//@Entity
//@Table(name="MailRecipient")
//@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public interface MailRecipient {
    
    @Id
    public long getId();
    
    public void setId(long id);
    /**
     * Returns the address of the MailRecipient.
     * 
     * @return 
     */
    public String getAddress();
    
    public void setAddress(String address);
}
