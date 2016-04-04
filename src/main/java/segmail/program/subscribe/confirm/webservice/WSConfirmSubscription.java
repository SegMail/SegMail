/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.confirm.webservice;

import segmail.program.subscribe.confirm.client.WSConfirmSubscriptionInterface;
import eds.component.data.RelationshipNotFoundException;
import eds.component.transaction.TransactionService;
import eds.entity.transaction.EnterpriseTransactionParam;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.WebParam;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.Subscription;

/**
 *
 * @author LeeKiatHaw
 */
@WebService(
        serviceName = "WSConfirmSubscription",
        endpointInterface = "segmail.program.subscribe.confirm.webservice.WSConfirmSubscription")
@HandlerChain(file="handlers-server.xml")
public class WSConfirmSubscription implements WSConfirmSubscriptionInterface  {
    
    @EJB SubscriptionService subService;
    @EJB TransactionService transService;

    /**
     * This is a sample web service operation
     * @param key
     * @return 
     * @throws eds.component.data.RelationshipNotFoundException
     */
    @Override
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
