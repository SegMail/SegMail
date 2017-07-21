/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.confirm.webservice;

import eds.component.data.DataValidationException;
import eds.component.data.IncompleteDataException;
import eds.component.webservice.TransactionProcessedException;
import eds.component.webservice.UnwantedAccessException;
import segmail.program.subscribe.confirm.client.WSConfirmSubscriptionInterface;
import eds.component.data.RelationshipNotFoundException;
import eds.component.mail.InvalidEmailException;
import eds.component.transaction.TransactionService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.jws.WebParam;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.email.mailmerge.MAILMERGE_STATUS;
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
            //Check if it is a testing link
            if (key.equalsIgnoreCase("test")) {
                return "This is a testing list";
            }

            MailMergeRequest trans = transService.getTransactionByKey(key, MailMergeRequest.class);

            if (trans == null) {
                throw new UnwantedAccessException();
            }

            if (MAILMERGE_STATUS.PROCESSED.name.equals(trans.getPROCESSING_STATUS())) {
                throw new TransactionProcessedException();
            }
            
            Subscription confirmedSubsc = subService.confirmSubscriber(key);

            /*int updateResults = transService.updateStatus(key, MAILMERGE_STATUS.PROCESSED.name,MailMergeRequest.class );

            if (updateResults <= 0) {
                throw new RuntimeException("No Transaction was udpated.");
            }
            */
            trans.setPROCESSING_STATUS(MAILMERGE_STATUS.PROCESSED.name);
            trans = transService.mergeTransaction(trans);
            
            SubscriptionList list = confirmedSubsc.getTARGET();
            if(list.getREDIRECT_WELCOME() != null && !list.getREDIRECT_WELCOME().isEmpty()) {
                return "redirect: "+list.getREDIRECT_WELCOME(); //Ugly hack, could have used JAX-RS and return a redirect response
            }

            return list.getLIST_NAME();
        } catch (RelationshipNotFoundException ex) {
            Logger.getLogger(WSConfirmSubscription.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("You received this transaction code by mistake.", ex);
        } catch (IncompleteDataException ex) {
            Logger.getLogger(WSConfirmSubscription.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex.getMessage(), ex);
        } catch (DataValidationException ex) {
            Logger.getLogger(WSConfirmSubscription.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Subscriber does not have an email address.", ex);
        } catch (InvalidEmailException ex) {
            Logger.getLogger(WSConfirmSubscription.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Subscriber's or sender's email address is invalid.", ex);
        }
    }

    @Override
    public String resend(String key)
            throws UnwantedAccessException, TransactionProcessedException {
        if (key == null || key.isEmpty()) {
            throw new UnwantedAccessException("Key is not provided.");
        }
        MailMergeRequest trans = transService.getTransactionByKey(key, MailMergeRequest.class);

        if (trans == null) {
            throw new UnwantedAccessException();
        }

        if (MAILMERGE_STATUS.PROCESSED.name.equals(trans.getPROCESSING_STATUS())) {
            throw new TransactionProcessedException();
        }
        try {
            subService.retriggerConfirmation(key);

        } catch (Throwable ex) {
            Logger.getLogger(WSConfirmSubscription.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }

        return "Email has been retriggered";
    }
}
