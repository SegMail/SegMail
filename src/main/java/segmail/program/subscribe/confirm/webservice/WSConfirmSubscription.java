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
import javax.xml.ws.soap.SOAPFaultException;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.Subscription;

/**
 *
 * @author LeeKiatHaw
 */
@WebService(
        serviceName = "WSConfirmSubscription",
        endpointInterface = "segmail.program.subscribe.confirm.webservice.WSConfirmSubscription")
@HandlerChain(file = "handlers-server.xml")
public class WSConfirmSubscription implements WSConfirmSubscriptionInterface {

    @EJB
    SubscriptionService subService;
    @EJB
    TransactionService transService;

    /**
     * Confirms a subscription.
     * 
     * 3 outcomes:
     * 1) Key is null or empty: throw RuntimeException.
     * This is a programming fault, the user cannot do anything to rectify it.
     * 
     * 2) Key cannot be found: throw SOAPFaultException
     *
     * @param key
     * @return
     */
    @Override
    public String confirm(@WebParam(name = "key") String key) {

        try {
            if(key == null || key.isEmpty())
                throw new RuntimeException("Key is not provided.");
            
            List<EnterpriseTransactionParam> params = transService.getTransactionParamsByKey(key, EnterpriseTransactionParam.class);
            
            //if(params == null || params.isEmpty())
                
            
            String email = "";
            long listId = -1;

            for (EnterpriseTransactionParam p : params) {
                if (p.getPARAM_KEY().equals(SubscriptionService.DEFAULT_EMAIL_FIELD_NAME)) {
                    email = p.getPARAM_VALUE();
                }
                if (p.getPARAM_KEY().equals(SubscriptionService.DEFAULT_KEY_FOR_LIST)) {
                    listId = Long.parseLong(p.getPARAM_VALUE());
                }
            }

            Subscription confirmedSubsc = subService.confirmSubscriber(key, listId);
            return confirmedSubsc.getTARGET().getLIST_NAME();
        } catch (RelationshipNotFoundException ex) {
            throw new RuntimeException("Confirmation failed.", ex);
        }
    }
}
