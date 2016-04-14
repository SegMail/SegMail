/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.confirm.webservice;

import eds.component.webservice.TransactionProcessedException;
import eds.component.webservice.UnwantedAccessException;
import segmail.program.subscribe.confirm.client.WSConfirmSubscriptionInterface;
import eds.component.data.RelationshipNotFoundException;
import eds.component.transaction.TransactionService;
import eds.entity.transaction.EnterpriseTransactionParam;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.WebParam;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.email.mailmerge.MAILMERGE_STATUS;
import segmail.entity.subscription.email.mailmerge.MailMergeLabel;
import segmail.entity.subscription.email.mailmerge.MailMergeRequest;

/**
 *
 * @author LeeKiatHaw
 */
//@Stateless
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
     * 3 outcomes: 1) Key is null or empty: Throw a UnwantedAccessException to
     * show users a Landing Page to sign up for our services.
     *
     * 2) Key/Params cannot be found: This is a programming fault, the user
     * cannot do anything to rectify it. Throw RuntimeException.
     *
     * 3) Transaction has already been processed Throw a
     * TransactionProcessedException exception to tell users that their
     * subscription has already been processed and they should be receiving
     * their welcome email soon.
     *
     * 4) Subscription cannot be found Throw a RuntimeException. If there is a
     * Transaction key and params but no Subscription that means something was
     * changed at the ERP server, which users can't do anything about.
     *
     * 5) Subscription is confirmed successfully return the list name
     *
     * @param key
     * @return List name
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public String confirm(@WebParam(name = "key") String key) 
            throws TransactionProcessedException, UnwantedAccessException {

        try {
            if (key == null || key.isEmpty()) {
                throw new UnwantedAccessException("Key is not provided.");
            }

            MailMergeRequest trans = transService.getTransactionByKey(key, MailMergeRequest.class);

            if (trans == null) {
                throw new RuntimeException("Transaction key not found.");
            }

            if (MAILMERGE_STATUS.PROCESSED.name().equals(trans.getPROCESSING_STATUS())) {
                throw new TransactionProcessedException();
            }

            List<EnterpriseTransactionParam> params = transService.getTransactionParamsByKey(key, EnterpriseTransactionParam.class);

            if (params == null || params.isEmpty()) {
                throw new RuntimeException("Transaction parameters missing.");
            }

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

            Subscription confirmedSubsc = subService.confirmSubscriber(email, listId);

            int updateResults = transService.updateStatus(key, MAILMERGE_STATUS.PROCESSED.name());

            if (updateResults <= 0) {
                throw new RuntimeException("No Transaction was udpated.");
            }

            return confirmedSubsc.getTARGET().getLIST_NAME();
        } catch (RelationshipNotFoundException ex) {
            throw new RuntimeException("You received this transaction code by mistake.", ex);
        }
    }

    public String process(String function, String key)
            throws UnwantedAccessException, TransactionProcessedException {
        if (key == null || key.isEmpty()) {
            throw new UnwantedAccessException("Key is not provided.");
        }

        MailMergeRequest trans = transService.getTransactionByKey(key, MailMergeRequest.class);

        if (trans == null) {
            throw new RuntimeException("Transaction key not found.");
        }

        if (MAILMERGE_STATUS.PROCESSED.name().equals(trans.getPROCESSING_STATUS())) {
            throw new TransactionProcessedException();
        }

        List<EnterpriseTransactionParam> params = transService.getTransactionParamsByKey(key, EnterpriseTransactionParam.class);

        if (params == null || params.isEmpty()) {
            throw new RuntimeException("Transaction parameters missing.");
        }

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

        //Determine which function to call
        MailMergeLabel functionEnum = MailMergeLabel.getMailMergeLabel(function);
        String listname = "";
        try {
            switch (functionEnum) {
                case CONFIRM:
                    Subscription confirmedSubsc = subService.confirmSubscriber(email, listId);
                    listname = confirmedSubsc.getTARGET().getLIST_NAME();
                    break;
                case UNSUBSCRIBE:
                    Subscription unsubSubsc = subService.unsubscribeSubscriber(email, listId);
                    listname = unsubSubsc.getTARGET().getLIST_NAME();
                    break;
            }
            int updateResults = transService.updateStatus(key, MAILMERGE_STATUS.PROCESSED.name());

            if (updateResults <= 0) {
                throw new RuntimeException("No Transaction was udpated.");
            }
        } catch (RelationshipNotFoundException ex) {
            throw new RuntimeException("You received this transaction code by mistake.", ex);
        }

        return listname;
    }
}
