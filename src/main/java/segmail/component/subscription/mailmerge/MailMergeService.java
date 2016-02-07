/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription.mailmerge;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import segmail.entity.subscription.SubscriberAccount;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class MailMergeService {
    
    @EJB private GenericObjectService objectService;
    @EJB private UpdateObjectService updateService;
    
    /**
     * 
     * @param content
     * @param listId
     * @param subscribers
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String parseMultipleContent(String content, long listId, List<SubscriberAccount> subscribers){
        
        return "";
    }
}
