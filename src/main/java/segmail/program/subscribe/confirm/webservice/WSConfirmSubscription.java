/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.confirm.webservice;

import eds.component.data.RelationshipNotFoundException;
import eds.component.transaction.TransactionService;
import eds.entity.transaction.EnterpriseTransactionParam;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.Subscription;

/**
 *
 * @author LeeKiatHaw
 */
@WebService(serviceName = "WSConfirmSubscription")
public class WSConfirmSubscription {
    
    @EJB SubscriptionService subService;
    @EJB TransactionService transService;

    /**
     * This is a sample web service operation
     * @param key
     * @return 
     * @throws eds.component.data.RelationshipNotFoundException
     */
    @WebMethod(operationName = "confirm")
    public String confirm(@WebParam(name = "key") String key) 
            throws RelationshipNotFoundException {
        
        List<EnterpriseTransactionParam> params = transService.getTransactionParamsByKey(key, EnterpriseTransactionParam.class);
        
        String email = "";
        long listId = -1;
        
        for(EnterpriseTransactionParam p : params) {
            if(p.getPARAM_KEY().equals(SubscriptionService.DEFAULT_EMAIL_FIELD_NAME))
                email = p.getPARAM_VALUE();
            if(p.getPARAM_KEY().equals(SubscriptionService.DEFAULT_KEY_FOR_LIST))
                listId = Long.parseLong(p.getPARAM_VALUE());
        }
        
        Subscription confirmedSubsc = subService.confirmSubscriber(key, listId);
        return confirmedSubsc.getTARGET().getLIST_NAME();
    }
}
