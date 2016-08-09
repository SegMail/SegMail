/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this autoEmail file, choose Tools | Templates
 * and open the autoEmail in the editor.
 */
package segmail.component.subscription;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.batch.BatchProcessingException;
import eds.component.client.ClientFacade;
import eds.component.client.ClientResourceInterceptor;
import eds.component.data.DBConnectionException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import eds.entity.client.Client;
import eds.component.data.DataValidationException;
import eds.component.data.RelationshipNotFoundException;
import eds.component.encryption.EncryptionService;
import eds.component.encryption.EncryptionType;
import eds.component.mail.InvalidEmailException;
import eds.component.mail.MailService;
import eds.entity.mail.Email;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberAccount_;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.exception.GenericJDBCException;
import org.joda.time.DateTime;
import segmail.component.subscription.autoresponder.AutoresponderService;
import segmail.component.subscription.mailmerge.MailMergeService;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.SUBSCRIPTION_STATUS;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.SubscriberFieldValue_;
import segmail.entity.subscription.SubscriberOwnership;
import segmail.entity.subscription.SubscriberOwnership_;
import segmail.entity.subscription.SubscriptionList_;
import segmail.entity.subscription.Subscription_;
import segmail.entity.subscription.autoresponder.AUTO_EMAIL_TYPE;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
@Interceptors({ClientResourceInterceptor.class})
public class SubscriptionService {

    public static final String DEFAULT_EMAIL_FIELD_NAME = "Email";
    public static final String DEFAULT_KEY_FOR_LIST = "LIST";

    /**
     * Generic services
     */
    @EJB
    private GenericObjectService objectService;
    @EJB
    private UpdateObjectService updateService;
    @EJB
    private EncryptionService encryptService;
    @EJB
    private ListService listService;
    @EJB
    private MassSubscriptionService massSubService;

    /**
     * Delegate services
     */
    @EJB
    private AutoresponderService autoresponderService;
    @EJB
    private MailMergeService mailMergeService;
    @EJB
    private MailService mailService;

    /**
     * [2015.07.12] Because the EJB Interceptor way failed, so this is a very
     * good alternative to omit clientid input for every method call.
     *
     * [2016.07.09] A service should not be injected with ClientFacade because
     * it can be also called from a webservice endpoint. Unless ClientModule
     * works in a webservice call, which is impossible because it is activated
     * by the servlet pattern "/program/*" Oh...turns out it is possible to tune
     * ClientModule to handle webservice requests. But we still can't do this
     * because webservice can be called by another system user eg. a server to
     * server call.
     */
    @Inject
    ClientFacade clientFacade;

    @Inject
    SubscriptionContainer subContainer;

    /**
     * This assumes that the client has constructed a list of
     * SubscriberFieldValue with the correct FIELD_KEY values set in these
     * objects. There should be another method that takes in just a
     * Map<String,String> of objects.
     *
     * @param clientId
     * @param listId
     * @param values
     * @param doubleOptin
     * @throws EntityNotFoundException
     * @throws IncompleteDataException
     * @throws segmail.component.subscription.SubscriptionException
     * @throws eds.component.batch.BatchProcessingException
     * @throws RelationshipExistsException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void subscribe(long clientId, long listId, Map<String, Object> values, boolean doubleOptin)
            throws EntityNotFoundException, IncompleteDataException, SubscriptionException, BatchProcessingException, RelationshipExistsException {

        SubscriptionList list = objectService.getEnterpriseObjectById(listId, SubscriptionList.class);
        if (list == null) {
            throw new EntityNotFoundException(SubscriptionList.class, listId);
        }

        List<SubscriptionListField> fields = listService.getFieldsForSubscriptionList(listId);
        if (fields == null || fields.isEmpty()) {
            throw new IncompleteDataException("List " + listId + " does not have fields created.");
        }

        //Look for the key in fields for DEFAULT_EMAIL_FIELD_NAME
        String email = "";
        for (SubscriptionListField field : fields) {
            if (field.getFIELD_NAME().equals(DEFAULT_EMAIL_FIELD_NAME)) {
                email = (String) values.get((String) field.generateKey());
                if (email != null && checkSubscribed(email, listId)) {
                    throw new RelationshipExistsException("Subscriber is already on this list");
                }
            }
        }
        if (email == null || email.isEmpty()) {
            throw new IncompleteDataException("Mandatory list field " + DEFAULT_EMAIL_FIELD_NAME + " is missing.");
        }

        subContainer.setList(list);
        subContainer.setListFields(fields);

        List<Map<String, Object>> singleSubscriberMap = new ArrayList<>();
        singleSubscriberMap.add(values);

        Map<String, List<Map<String, Object>>> results = massSubService.massSubscribe(singleSubscriberMap, doubleOptin);
        for (String key : results.keySet()) {
            throw new SubscriptionException(key);
        }
    }

    /**
     * There is no check for duplicates or already subscribed, like single
     * subscribe() method. The latest duplicates will overwrite all previous
     * version. If the existing subscriber has a particular field value, and a
     * later version of it has another new different field value, both of them
     * will be retained.
     *
     * @param clientId
     * @param listId
     * @param subscribers
     * @param doubleOptin
     * @return a Map of error messages and their records. A list of possible
     * error messages:
     * <ul>
     * <li>Mandatory list field [SubscriptionListField.FIELD_NAME] is
     * missing.</li>
     * <li>Invalid email</li>
     *
     * @throws EntityNotFoundException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Map<String, List<Map>> massSubscribe(
            long clientId,
            long listId,
            List<Map<String, Object>> subscribers,
            boolean doubleOptin) throws EntityNotFoundException {

        //Set up the return results Map
        Map<String, List<Map>> results = new HashMap<>();

        SubscriptionList list = objectService.getEnterpriseObjectById(listId, SubscriptionList.class); //DB hit, can be cached
        if (list == null) {
            throw new EntityNotFoundException(SubscriptionList.class, listId);
        }

        Client client = objectService.getEnterpriseObjectById(clientId, Client.class); //DB hit, can be cached
        if (client == null) {
            throw new EntityNotFoundException(Client.class, clientId);
        }

        List<SubscriptionListField> fields = listService.getFieldsForSubscriptionList(listId); //DB hit, can be cached

        //This is the list of emails to be added and checked for existing subscribers
        List<String> emails = new ArrayList<>();
        //This is the list of subscriber field values that will be added because they passed
        //field validatons
        List<Map<String, Object>> survivors = new ArrayList<>();

        //Check for mandatory fields and retrieve all emails in a list so that we can check for existing later
        for (Map<String, Object> subscriber : subscribers) {
            String email = "";
            String errorKey = "";
            for (SubscriptionListField field : fields) {
                if (field.isMANDATORY()
                        && (subscriber.get((String) field.generateKey()) == null || ((String) subscriber.get((String) field.generateKey())).isEmpty())) {
                    //throw new IncompleteDataException("Mandatory list field " + field.getFIELD_NAME() + " is missing.");
                    errorKey = "Mandatory list field " + field.getFIELD_NAME() + " is missing.";
                    break;
                }
                if (field.getFIELD_NAME().equals(DEFAULT_EMAIL_FIELD_NAME)) {
                    email = (String) subscriber.get((String) field.generateKey());// If there exist multiple fieldvalues of email, then the latest one will be used
                    //Validate email format
                    if (!EmailValidator.getInstance().isValid(email)) {
                        errorKey = "Invalid email";
                        break;
                    }
                }
            }
            if (!errorKey.isEmpty()) {
                if (results.get(errorKey) == null) {
                    results.put(errorKey, new ArrayList());
                }
                results.get(errorKey).add(subscriber);
                continue;
            }
            //If it passes field validations, add it to emails List
            emails.add(email);
            survivors.add(subscriber);//This will overwrite duplicates!
            //If there are duplicates, the latest copy in the file will be used.
        }
        if (survivors.isEmpty()) {
            return results;
        }

        /**
         * There will be 3 groups: 1) Subscribers who already exist but have no
         * update 2) Subscribers who already exist but requires update
         * (identified by email) 3) Fresh subscribers
         *
         * First we have to retrieve all SubscriberAccount objects
         */
        List<SubscriberAccount> existingSubscribers = this.getSubscribersForClientByEmails(emails, clientId); //DB hit, can be cached
        Collections.sort(existingSubscribers);//Meaningless...not entirely...
        List<Long> existingSubscriberIds = new ArrayList<>();
        for (SubscriberAccount account : existingSubscribers) {
            existingSubscriberIds.add(account.getOBJECTID());
        }
        //And also all their SubscriberFieldValues
        List<SubscriberFieldValue> existingFieldValues = this.getSubscriberValuesBySubscribers(existingSubscriberIds); //DB hit, can be cached
        Collections.sort(existingFieldValues); //Default sorting method for EnterpriseData
        /**
         * For each survivor: 1) Check if SubscriberAccount has already been
         * created for. - If yes, no need to update it. Remove it from the
         * survivor list. - If no, create it and add it to createNewSubAccList
         *
         * 2) Check if it exists in existingFieldValues by comparing it using
         * email and the field key - If yes, compare their values. - If same,
         * ignore it. - If different, add it to updateFieldValueList - If no,
         * add it to createFieldValueList
         */
        List<SubscriberAccount> createNewSubAccList = new ArrayList<>();
        List<SubscriberFieldValue> updateFieldValueList = new ArrayList<>();
        List<SubscriberFieldValue> createFieldValueList = new ArrayList<>();

        for (int i = 0; i < survivors.size(); i++) {
            Map<String, Object> survivor = survivors.get(i);
            String email = emails.get(i); //emails and survivors have the same order
            List<SubscriberFieldValue> fieldValues = new ArrayList<>();
            SubscriberAccount owner = null; //Hypothetical owner

            //Find the existing SubscriberAccount and its corresponding SubscriberFieldValues
            for (SubscriberAccount existingSubscriber : existingSubscribers) {
                //If found, get SubscriberFieldValues from existingFieldValues
                if (existingSubscriber.getEMAIL() == null ? email == null : existingSubscriber.getEMAIL().equals(email)) {
                    owner = existingSubscriber;
                    //find all its fieldvalues and add them in fieldValues
                    /*for(SubscriberFieldValue existingFieldValue : existingFieldValues) {
                     //Having a null owner is quite impossible so we would see nullpointerexception if it happens and fix this issue
                     if(existingSubscriber.equals(existingFieldValue.getOWNER())) {
                     fieldValues.add(existingFieldValue);
                     }
                     }*/
                }
            }
            //If fieldValues exists, it means that the subscriber already exist and might require updates.
            //If it exists in survivor Map, update the value
            //If it doesn't exist in survivor Map, create it
            for (String key : survivor.keySet()) {
                boolean createNew = true;
                for (SubscriberFieldValue fieldValue : existingFieldValues) {
                    //If exist, add it into updateFieldValueList
                    //Note that if duplicates exist here, the latest value will be taken
                    if (key == null ? fieldValue.getFIELD_KEY() == null : key.equals(fieldValue.getFIELD_KEY())) {
                        fieldValue.setVALUE((String) survivor.get(key));
                        updateFieldValueList.add(fieldValue);
                        createNew = false;
                        break; //break from this loop
                    }
                }
                //If it was not found, construct it and add it in to createFieldValueList
                if (createNew) {
                    SubscriberFieldValue newValue = new SubscriberFieldValue();
                    newValue.setFIELD_KEY(key);
                    newValue.setVALUE((String) survivor.get(key));
                    if (owner == null) {
                        owner = new SubscriberAccount();
                        owner.setEMAIL(email);
                        createNewSubAccList.add(owner);
                    }
                    newValue.setOWNER(owner);

                    createFieldValueList.add(newValue);
                }
            }
        }

        //Time to do db updates and inserts
        return results;
    }

    /**
     * Check if a subscriber subscriberEmail is already subscribed to a list.
     *
     * Might be wrong! Re-test the implementation
     *
     * @param subscriberEmail
     * @param listId
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean checkSubscribed(String subscriberEmail, long listId) {
        try {
            //Retrieving Subscriptions which has a SubscriberAccount subscriberEmail and a SubscriptionList ID
            CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            Root<Subscription> fromSubscr = criteria.from(Subscription.class);
            Root<SubscriberAccount> fromSubscrAcc = criteria.from(SubscriberAccount.class);

            criteria.select(builder.count(fromSubscrAcc));

            criteria.where(
                    builder.and(
                            builder.equal(fromSubscr.get(Subscription_.TARGET), listId),
                            builder.equal(fromSubscr.get(Subscription_.SOURCE), fromSubscrAcc.get(SubscriberAccount_.OBJECTID)),
                            builder.equal(fromSubscrAcc.get(SubscriberAccount_.EMAIL), subscriberEmail)
                    )
            );

            Long results = objectService.getEm().createQuery(criteria).getSingleResult();

            return results > 0;

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Map<Long, Map<String, String>> getSubscriberValuesMap(long listId, int startIndex, int limit) {
        try {
            //Get all the SubscriptionFields first
            List<String> fields = listService.getSubscriptionListFieldKeys(listId);

            CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
            CriteriaQuery criteria = builder.createQuery(SubscriberFieldValue.class);
            Root<Subscription> fromSubscr = criteria.from(Subscription.class);
            Root<SubscriberFieldValue> fromFieldValue = criteria.from(SubscriberFieldValue.class);

            criteria.select(fromFieldValue);
            criteria.where(
                    builder.and(
                            builder.equal(fromSubscr.get(Subscription_.TARGET), listId),
                            builder.equal(
                                    fromSubscr.get(Subscription_.SOURCE),
                                    fromFieldValue.get(SubscriberFieldValue_.OWNER)),
                            fromFieldValue.get(SubscriberFieldValue_.FIELD_KEY).in(fields)
                    )
            );

            List<SubscriberFieldValue> results = objectService.getEm().createQuery(criteria)
                    .setFirstResult(startIndex)
                    .setMaxResults(limit)
                    .getResultList();

            Map<Long, Map<String, String>> resultMap
                    = new HashMap<>();

            //Collections.sort(results);//Sorting before creating the map may not be the most correct solution but it's the most efficient and effective one at the moment
            for (SubscriberFieldValue field : results) {
                SubscriberAccount subscriber = field.getOWNER();
                if (!resultMap.containsKey(subscriber.getOBJECTID())) {
                    resultMap.put(subscriber.getOBJECTID(), new HashMap<String, String>());

                }
                resultMap.get(subscriber.getOBJECTID()).put(field.getFIELD_KEY(), field.getVALUE());
            }
            return resultMap;

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    /**
     * Non-update helper method to construct a list of SubscriberFieldValue to
     * be populated. Mainly for frontend forms usage. The returned result is not
     * ready to be updated into the DB yet because it is still missing the
     * SubscriberAccount EnterpriseObject.
     *
     * @param fields
     * @return
     */
    public Map<String, Object> constructSubscriberFieldValues(List<SubscriptionListField> fields) {
        Map<String, Object> values = new HashMap<>();

        if (fields == null || fields.isEmpty()) {
            return values;
        }

        List<SubscriptionListField> sortedFields = new ArrayList<>(fields);
        Collections.sort(sortedFields);

        for (int i = 0; i < fields.size(); i++) {
            SubscriptionListField field = fields.get(i);
            values.put(field.generateKey().toString(), "");
        }

        return values;
    }

    /**
     *
     * @param sub
     * @throws IncompleteDataException if no Send As address is set for list or
     * a confirmation email is not assigned to the list.
     * @throws BatchProcessingException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
<<<<<<< HEAD
    public void sendConfirmationEmail(Subscription sub) throws IncompleteDataException 
            {
        try {
=======
    public void sendConfirmationEmail(Subscription sub)
            throws IncompleteDataException, BatchProcessingException, DataValidationException, InvalidEmailException {
>>>>>>> master
            //Retrieve "Send as" from the list
            /*SubscriptionList list = objectService.getEnterpriseObjectById(listId, SubscriptionList.class);
         if(list == null)
         throw new EntityNotFoundException(SubscriptionList.class,listId);*/

        SubscriptionList list = sub.getTARGET();

        String sendAs = list.getSEND_AS_EMAIL();
        if (sendAs == null || sendAs.isEmpty()) {
            throw new IncompleteDataException("Please set \"Send As\" address before sending confirmation emails.");
        }

            //Retrieve the autoemail from list using AutoresponderService
        //List<AutoresponderEmail> assignedAutoEmails = objectService.getAllSourceObjectsFromTarget(
        //        listId,Assign_AutoConfirmEmail_List.class, AutoConfirmEmail.class);
        List<AutoresponderEmail> assignedAutoEmails = autoresponderService.getAssignedAutoEmailsForList(list.getOBJECTID(), AUTO_EMAIL_TYPE.CONFIRMATION);
        AutoresponderEmail assignedConfirmEmail = (assignedAutoEmails == null || assignedAutoEmails.isEmpty())
                ? null : assignedAutoEmails.get(0);

        if (assignedConfirmEmail == null) {
            throw new IncompleteDataException("Please assign a Confirmation email before adding subscribers.");
        }

        //Parse all mailmerge functions using MailMergeService
        String newEmailBody = assignedConfirmEmail.getBODY();
        newEmailBody = mailMergeService.parseConfirmationLink(newEmailBody, sub.getCONFIRMATION_KEY());
        //newEmailBody = mailMergeService.parseListAttributes(newEmailBody, listId);
        //newEmailBody = mailMergeService.parseUnsubscribeLink(newEmailBody, sub.getUNSUBSCRIBE_KEY()); //Should not be here!

        //Send the email using MailService
        Email confirmEmail = new Email();
        confirmEmail.setSENDER_ADDRESS(list.getSEND_AS_EMAIL());
        confirmEmail.setSENDER_NAME(list.getSEND_AS_NAME());
        confirmEmail.setBODY(newEmailBody);
        confirmEmail.setSUBJECT(assignedConfirmEmail.getSUBJECT());
        confirmEmail.addRecipient(sub.getSOURCE().getEMAIL());

            //mailService.sendEmailByAWS(confirmEmail, true);
        //BatchJobStep step = batchService.createJobStep("MailService", "sendEmailNow", new Object[] {confirmEmail,true});
        //batchService.executeJobStep(step);
        mailService.queueEmail(confirmEmail, DateTime.now());

    }

    /**
     * Get a list of SubscriberAccount for a specific client with the given
     * email.
     *
     * @param email
     * @param clientId
     * @return
     */
    public List<SubscriberAccount> getSubscribersForClientByEmail(String email, long clientId) {
        List<String> emails = new ArrayList<>();
        emails.add(email);

        List<SubscriberAccount> results = this.getSubscribersForClientByEmails(emails, clientId);

        return results;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private SubscriberAccount getExistingOrCreateNewSubscriber(String email, long clientId) throws EntityNotFoundException {
        //Retrieve the client object
        Client client = objectService.getEnterpriseObjectById(clientId, Client.class);
        if (client == null) {
            throw new EntityNotFoundException(Client.class, clientId);
        }

        return getExistingOrCreateNewSubscriber(email, client);
    }

    /**
     * Retrieves existing subscriber or create a new one using an email address
     * for a given client.
     *
     * @param email
     * @param clientId
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private SubscriberAccount getExistingOrCreateNewSubscriber(String email, Client client) throws EntityNotFoundException {
        List<SubscriberAccount> existingAccs = this.getSubscribersForClientByEmail(email, client.getOBJECTID());
        //If the email subscriber exists for the given client, return the found subscriber
        if (existingAccs != null && !existingAccs.isEmpty()) {
            return existingAccs.get(0);
        }

        //If not found, create new and assign it to the client
        SubscriberAccount newOrExistingAcc = new SubscriberAccount();
        newOrExistingAcc.setEMAIL(email);

        this.updateService.getEm().persist(newOrExistingAcc);

        //Assign it to the client
        SubscriberOwnership assign = new SubscriberOwnership();
        assign.setTARGET(client);
        assign.setSOURCE(newOrExistingAcc);

        this.updateService.getEm().persist(assign);

        return newOrExistingAcc;
    }

    public List<Subscription> getSubscriptions(String subscriberEmail, long listId) {
        CriteriaBuilder builder = this.objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<Subscription> query = builder.createQuery(Subscription.class);
        Root<SubscriberAccount> fromSubscriber = query.from(SubscriberAccount.class);
        Root<SubscriptionList> fromList = query.from(SubscriptionList.class);
        Root<Subscription> fromSubscription = query.from(Subscription.class);

        query.select(fromSubscription);
        query.where(builder.and(
                builder.equal(fromSubscriber.get(SubscriberAccount_.EMAIL), subscriberEmail),
                builder.equal(fromSubscription.get(Subscription_.TARGET), listId),
                builder.equal(fromSubscription.get(Subscription_.SOURCE), fromSubscriber.get(SubscriberAccount_.OBJECTID))
        ));

        List<Subscription> results = this.objectService.getEm().createQuery(query)
                .getResultList();

        return results;
    }

    public List<Subscription> getSubscriptionsByEmails(List<String> subscriberEmails, long listId) {
        CriteriaBuilder builder = this.objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<Subscription> query = builder.createQuery(Subscription.class);
        Root<SubscriberAccount> fromSubscriber = query.from(SubscriberAccount.class);
        Root<SubscriptionList> fromList = query.from(SubscriptionList.class);
        Root<Subscription> fromSubscription = query.from(Subscription.class);

        query.select(fromSubscription);
        query.where(builder.and(
                fromSubscriber.get(SubscriberAccount_.EMAIL).in(subscriberEmails),
                builder.equal(fromSubscription.get(Subscription_.TARGET), listId),
                builder.equal(fromSubscription.get(Subscription_.SOURCE), fromSubscriber.get(SubscriberAccount_.OBJECTID))
        ));

        List<Subscription> results = this.objectService.getEm().createQuery(query)
                .getResultList();

        return results;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Subscription confirmSubscriber(String confirmKey)
            throws RelationshipNotFoundException {
        /*List<Subscription> subscriptions = getSubscriptions(subscriberEmail,listId);
        
         if(subscriptions == null || subscriptions.isEmpty())
         throw new RelationshipNotFoundException("The email "+subscriberEmail+" or list ID "+listId+" was not found.");
        
         //Subscriptions should be unique, so only 1 result is expected
         Subscription subsc = subscriptions.get(0);
         */
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<Subscription> query = builder.createQuery(Subscription.class);
        Root<Subscription> fromSubsc = query.from(Subscription.class);

        query.select(fromSubsc);
        query.where(builder.equal(fromSubsc.get(Subscription_.CONFIRMATION_KEY), confirmKey));

        List<Subscription> results = objectService.getEm().createQuery(query)
                .getResultList();

        if (results == null || results.isEmpty()) {
            throw new RelationshipNotFoundException("Subscription not found for confirmation key.");
        }

        Subscription sub = results.get(0);

        sub.setSTATUS(SUBSCRIPTION_STATUS.CONFIRMED.toString());
        //sub.setCONFIRMATION_KEY("");//remove confirmation key?

        sub = updateService.getEm().merge(sub);

        return sub;

    }

    public Subscription unsubscribeSubscriber(String email, long listId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
<<<<<<< HEAD
    
    /**
     * This is designed to be called from a WEB server to retrigger a confirmation
     * email.
     * 
     * @param key 
     * @throws eds.component.data.IncompleteDataException 
     */
    public void retriggerConfirmation(String key) throws IncompleteDataException {
        List<Subscription> subscriptions = getSubscriptionByConfirmKey(key);
        //Impossible to have a duplicate because the key was created with list id and 
        //subscriber id. 
        if(subscriptions == null || subscriptions.isEmpty()) {
            //What should we do if the key is not found?
            return;
        }
        
        Subscription subscription = subscriptions.get(0);
        //We also need to check the status to see if the subscription is still NEW.
        //If the status has already been confirmed, do not send out anything and
        //log this request. 
        //send out an email to system administrator?
        if(!subscription.getSTATUS().equals(SUBSCRIPTION_STATUS.NEW.name())) {
            //What should we do?
            return;
        }
        
        this.sendConfirmationEmail(subscription);
    }
    
    /**
     * This is a helper method that should not be exposed.
     * 
     * @param confirmOrUnsubKey
     * @return 
     */
    private List<Subscription> getSubscriptionByConfirmKey(String confirmKey) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<Subscription> query = builder.createQuery(Subscription.class);
        Root<Subscription> fromSubsc = query.from(Subscription.class);
        
        query.where(builder.equal(fromSubsc.get(Subscription_.CONFIRMATION_KEY), confirmKey));
        
        List<Subscription> results = objectService.getEm().createQuery(query)
                .getResultList();
        
        return results;
    }
=======

    public String getConfirmationHashCode(long subscriberId, long listId) {
        String confirmKey = encryptService.getHash("confirm subscription of " + subscriberId + " to list " + listId, EncryptionType.SHA256);
        return confirmKey;

    }

    public String getUnsubscribeHashCode(long subscriberId, long listId) {
        String unsubKey = encryptService.getHash("unsubscribe " + subscriberId + " from list " + listId, EncryptionType.SHA256);
        return unsubKey;
    }

    /**
     * Get a list of SubscriberAccount for a specific client with the given list
     * of emails.
     *
     * @param emails
     * @param clientId
     * @return
     */
    public List<SubscriberAccount> getSubscribersForClientByEmails(List<String> emails, long clientId) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<SubscriberAccount> query = builder.createQuery(SubscriberAccount.class);

        Root<SubscriberAccount> fromSubscAcc = query.from(SubscriberAccount.class);
        Root<SubscriberOwnership> fromSubscOwnership = query.from(SubscriberOwnership.class);

        query.select(fromSubscAcc);
        query.where(builder.and(
                fromSubscAcc.get(SubscriberAccount_.EMAIL).in(emails),
                builder.equal(
                        fromSubscAcc.get(SubscriberAccount_.OBJECTID),
                        fromSubscOwnership.get(SubscriberOwnership_.SOURCE)
                ),
                builder.equal(fromSubscOwnership.get(SubscriberOwnership_.TARGET), clientId)
        ));

        List<SubscriberAccount> results = objectService.getEm().createQuery(query)
                .getResultList();

        return results;
    }

    public List<SubscriberFieldValue> getSubscriberValuesBySubscribers(List<Long> subscribers) {
        List<SubscriberFieldValue> results = objectService.getEnterpriseDataByIds(subscribers, SubscriberFieldValue.class);
        return results;
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<Integer> updateSubscriberCount(long listId) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaUpdate<SubscriptionList> query = builder.createCriteriaUpdate(SubscriptionList.class);
        Root<SubscriptionList> fromList = query.from(SubscriptionList.class);

        Subquery<Long> countQuery = query.subquery(Long.class);
        Root<Subscription> fromSubscription = countQuery.from(Subscription.class);
        countQuery.select(builder.count(fromSubscription));
        countQuery.where(builder.equal(fromSubscription.get(Subscription_.TARGET), listId));

        query.set(fromList.get(SubscriptionList_.SUBSCRIBER_COUNT), countQuery);
        query.where(builder.equal(fromList.get(SubscriptionList_.OBJECTID), listId));

        int result = objectService.getEm().createQuery(query)
                .executeUpdate();

        return new AsyncResult<>(result);
    }
>>>>>>> master
}
