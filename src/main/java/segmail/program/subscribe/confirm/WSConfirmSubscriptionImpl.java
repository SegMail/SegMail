/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.confirm;

import eds.component.data.RelationshipNotFoundException;
import eds.component.transaction.TransactionService;
import eds.entity.transaction.EnterpriseTransactionParam;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.Subscription;
import segmail.program.subscribe.confirm.client.WSConfirmSubscription;

/**
 *
 * @author LeeKiatHaw
 */
@WebService(name = "WSConfirmSubscription", targetNamespace = "http://confirm.subscribe.program.segmail/")
@HandlerChain(file="/handlers-server.xml")
public class WSConfirmSubscriptionImpl implements WSConfirmSubscription  {

    @EJB TransactionService transService;
    @EJB SubscriptionService subscriptionService;
    /**
     * Process email confirmation.
     * 
     * @param key
     * @return 
     * @throws eds.component.data.RelationshipNotFoundException 
     */
    @Override
    public String confirm(String key) throws RelationshipNotFoundException {
        
        List<EnterpriseTransactionParam> params = transService.getTransactionParamsByKey(key, EnterpriseTransactionParam.class);
        
        String email = "";
        String list = "";
        for(EnterpriseTransactionParam p : params){
            if(p.getPARAM_KEY().equals(SubscriptionService.DEFAULT_EMAIL_FIELD_NAME))
                email = p.getPARAM_VALUE();
            if(p.getPARAM_KEY().equals(SubscriptionService.DEFAULT_KEY_FOR_LIST))
                list = p.getPARAM_VALUE();
        }
        
        Subscription subsc = subscriptionService.confirmSubscriber(email, Long.getLong(list));
        
        return subsc.getTARGET().getLIST_NAME();
    }
}
