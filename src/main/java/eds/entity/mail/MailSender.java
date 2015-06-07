/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.mail;

import eds.entity.document.DocumentAuthor;

/**
 *
 * @author LeeKiatHaw
 */
public interface MailSender extends DocumentAuthor {
    
    /**
     * Returns the address of the MailSender.
     * 
     * @return 
     */
    public String getAddress();
    
    public void setAddress(String address);
}
