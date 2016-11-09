/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.mail;

import eds.component.GenericObjectService;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class MailServiceInbound {
    
    @EJB GenericObjectService objService;
    
    public void retrieveBounceMessage(String senderEmail) {
        
    }
}
