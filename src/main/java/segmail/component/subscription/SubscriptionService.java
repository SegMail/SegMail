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

    public static final String MASS_SUBSCRIBE_TOTAL_PROCESSED = "MASS_SUBSCRIBE_TOTAL_PROCESSED";
    public static final String MASS_SUBSCRIBE_INCOMPLETE = "MASS_SUBSCRIBE_INCOMPLETE";
    public static final String MASS_SUBSCRIBE_INVALID = "MASS_SUBSCRIBE_INVALID";
    public static final String MASS_SUBSCRIBE_EXISTING = "MASS_SUBSCRIBE_EXISTING";
    public static final String MASS_SUBSCRIBE_SEND_ERRORS = "MASS_SUBSCRIBE_SEND_ERRORS";
    public static final String MASS_SUBSCRIBE_SUCCESS = "MASS_SUBSCRIBE_SUCCESS";

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

    /**
     * This assumes that the client has constructed a list of
     * SubscriberFieldValue with the correct FIELD_KEY values set in these
     * objects. There should be another method that takes in just a
     * Map<String,String> of objects.
     *
     * @param clientId
     * @param listId
     * @param values
     * @throws EntityNotFoundException
     * @throws IncompleteDataException
     * @throws DataValidationException
     * @throws RelationshipExistsException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void subscribe(long clientId, long listId, Map<String, Object> values, boolean doubleOptin)
            throws EntityNotFoundException, IncompleteDataException, DataValidationException, RelationshipExistsException, BatchProcessingException {
        try {
            // Find the list object
            SubscriptionList list = objectService.getEnterpriseObjectById(listId, SubscriptionList.class);
            if (list == null) {
                throw new EntityNotFoundException(SubscriptionList.class, listId);
            }

            // Check if the values provided for the subscriber is valid for the list
            // Check if mandatory fields are not filled in
            String email = "";
            List<SubscriptionListField> fields = listService.getFieldsForSubscriptionList(listId);
            for (SubscriptionListField field : fields) {
                if (field.isMANDATORY()
                        && (values.get(field.generateKey().toString()) == null || ((String) values.get(field.generateKey().toString())).isEmpty())) {
                    throw new IncompleteDataException("Mandatory list field " + field.getFIELD_NAME() + " is missing.");
                }
                if (field.getFIELD_NAME().equals(DEFAULT_EMAIL_FIELD_NAME)) {
                    email = (String) values.get(field.generateKey().toString());// If there exist multiple fieldvalues of email, then the latest one will be used
                }
            }
            if (email.isEmpty()) {
                throw new IncompleteDataException(DEFAULT_EMAIL_FIELD_NAME + " is always required for a subscription.");
            }
            if (!EmailValidator.getInstance().isValid(email)) {
                throw new DataValidationException("Email address is not valid.");
            }

            // Check if the account exist, if not, create a new one
            // Check if account exist with the same client only
            SubscriberAccount newOrExistingAcc = getExistingOrCreateNewSubscriber(email, clientId);

            //Update the subscriber account first by merging
            //Even if it exist, it is required to merge it to manage it later
            newOrExistingAcc = updateService.getEm().merge(newOrExistingAcc);
            //updateService.getEm().flush();

            //Retrieve all existing fields for SubscriberAccount
            List<SubscriberFieldValue> existingFieldValues = objectService.getEnterpriseData(newOrExistingAcc.getOBJECTID(), SubscriberFieldValue.class);

            // Connect all new field values to the account and create them in the DB
            // If you pass in a set of fieldvalues with the same field object, then the latest one will be the last to be inserted and overwrites the rest
            // Get the highest count of the FieldValue SNO
            int maxSNO = objectService.getHighestSNO(SubscriberFieldValue.class, newOrExistingAcc.getOBJECTID());
            for (SubscriptionListField field : fields) {

                SubscriberFieldValue value = new SubscriberFieldValue();
                value.setOWNER(newOrExistingAcc);
                value.setFIELD_KEY(field.generateKey().toString());

                //If the new field value already exist in the DB, just update it
                if (existingFieldValues.contains(value)) {
                    value = existingFieldValues.get(existingFieldValues.indexOf(value));//Make use of equals()
                    value.setVALUE(values.get(field.generateKey().toString()).toString());//Update value no matter what
                    updateService.getEm().merge(value);
                } else {
                    value.setVALUE(values.get(field.generateKey().toString()).toString());//Update value no matter what
                    value.setSNO(++maxSNO);
                    updateService.getEm().persist(value);
                }
            }

            // Create the relationship
            Subscription newSubscr = new Subscription();
            newSubscr.setTARGET(list);
            newSubscr.setSOURCE(newOrExistingAcc);
            if (!doubleOptin) {
                newSubscr.setSTATUS(SUBSCRIPTION_STATUS.CONFIRMED);
            }

            // Create confirmation and unsubscribe keys
            //String confirmKey = encryptService.getHash("confirm subscription of "+newOrExistingAcc.getOBJECTID()+" to list "+list.getOBJECTID(), EncryptionType.SHA256);
            //String unsubKey = encryptService.getHash("unsubscribe "+newOrExistingAcc.getOBJECTID()+" from list "+list.getOBJECTID(), EncryptionType.SHA256);
            String confirmKey = getConfirmationHashCode(newOrExistingAcc.getOBJECTID(), listId);
            String unsubKey = getUnsubscribeHashCode(newOrExistingAcc.getOBJECTID(), listId);

            newSubscr.setCONFIRMATION_KEY(confirmKey);
            newSubscr.setUNSUBSCRIBE_KEY(unsubKey);

            //Check if the subscription already exist
            if (checkSubscribed(email, listId)) {
                throw new RelationshipExistsException(newSubscr);
            }

            updateService.getEm().persist(newSubscr);

            //Update the count of the list
            list.setSUBSCRIBER_COUNT(list.getSUBSCRIBER_COUNT() + 1);
            list = updateService.getEm().merge(list);

            //Send confirmation email
            if (doubleOptin) {
                sendConfirmationEmail(newSubscr);
            }

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    /**
     * Written for ERP mass import.
     *
     * @param clientId
     * @param listId
     * @param subscribers
     * @param doubleOptin
     * @return
     * @throws EntityNotFoundException
     */
    /*@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
     public Map<String, List<Map>> massSubscribe(
     long clientId,
     long listId,
     List<Map<String,Object>> subscribers,
     boolean doubleOptin) throws EntityNotFoundException {
     // Find the list object
     SubscriptionList list = objectService.getEnterpriseObjectById(listId, SubscriptionList.class);
     if (list == null) {
     throw new EntityNotFoundException(SubscriptionList.class, listId);
     }
        
     Client client = objectService.getEnterpriseObjectById(clientId, Client.class);
     if (client == null) {
     throw new EntityNotFoundException(Client.class, clientId);
     }

     // Check if the values provided for the subscriber is valid for the list
     // Check if mandatory fields are not filled in
     String email = "";
     List<SubscriptionListField> fields = listService.getFieldsForSubscriptionList(listId);
        
     //Set up the return results Map
     Map<String, List<Map>> results = new HashMap<>();
     ;
     //Loop through each Map object, which represents a subscriber
     for (int i = 0; i < subscribers.size(); i++) {
     Map subscriber  = subscribers.get(i);
            
     try {
     for (SubscriptionListField field : fields) {
     if (field.isMANDATORY()
     && (subscriber.get(field.generateKey().toString()) == null || ((String) subscriber.get(field.generateKey().toString())).isEmpty())) {
     throw new IncompleteDataException("Mandatory list field " + field.getFIELD_NAME() + " is missing.");
     }
     if (field.getFIELD_NAME().equals(DEFAULT_EMAIL_FIELD_NAME)) {
     email = (String) subscriber.get(field.generateKey().toString());// If there exist multiple fieldvalues of email, then the latest one will be used
     }
     }
     if (email.isEmpty()) {
     throw new IncompleteDataException(DEFAULT_EMAIL_FIELD_NAME + " is always required for a subscription.");
     }
     if (!EmailValidator.getInstance().isValid(email)) {
     throw new DataValidationException("Email address is not valid.");
     }
                
     //Check if the subscription already exist
     if (checkSubscribed(email, listId)) {
     throw new RelationshipExistsException("Subscriber is already on this list.");
     }

     // Check if the account exist, if not, create a new one
     // Check if account exist with the same client only
     SubscriberAccount newOrExistingAcc = getExistingOrCreateNewSubscriber(email, client);

     //Update the subscriber account first by merging
     //Even if it exist, it is required to merge it to manage it later
     newOrExistingAcc = updateService.getEm().merge(newOrExistingAcc);
     //updateService.getEm().flush();

     //Retrieve all existing fields for SubscriberAccount
     List<SubscriberFieldValue> existingFieldValues = objectService.getEnterpriseData(newOrExistingAcc.getOBJECTID(), SubscriberFieldValue.class);

     // Connect all new field values to the account and create them in the DB
     // If you pass in a set of fieldvalues with the same field object, then the latest one will be the last to be inserted and overwrites the rest
     // Get the highest count of the FieldValue SNO
     int maxSNO = objectService.getHighestSNO(SubscriberFieldValue.class, newOrExistingAcc.getOBJECTID());
     for (SubscriptionListField field : fields) {

     SubscriberFieldValue value = new SubscriberFieldValue();
     value.setOWNER(newOrExistingAcc);
     value.setFIELD_KEY(field.generateKey().toString());

     //If the new field value already exist in the DB, just update it
     if (existingFieldValues.contains(value)) {
     value = existingFieldValues.get(existingFieldValues.indexOf(value));//Make use of equals()
     value.setVALUE((String)subscriber.get(field.generateKey().toString()));//Update value no matter what
     updateService.getEm().merge(value);
     } else {
     value.setVALUE((String)subscriber.get(field.generateKey().toString()));//Update value no matter what
     value.setSNO(++maxSNO);
     updateService.getEm().persist(value);
     }
     }

     // Create the relationship
     Subscription newSubscr = new Subscription();
     newSubscr.setTARGET(list);
     newSubscr.setSOURCE(newOrExistingAcc);
     if (!doubleOptin) {
     newSubscr.setSTATUS(SUBSCRIPTION_STATUS.CONFIRMED);
     }

     // Create confirmation and unsubscribe keys
     //String confirmKey = encryptService.getHash("confirm subscription of "+newOrExistingAcc.getOBJECTID()+" to list "+list.getOBJECTID(), EncryptionType.SHA256);
     //String unsubKey = encryptService.getHash("unsubscribe "+newOrExistingAcc.getOBJECTID()+" from list "+list.getOBJECTID(), EncryptionType.SHA256);
     String confirmKey = getConfirmationHashCode(newOrExistingAcc.getOBJECTID(), listId);
     String unsubKey = getUnsubscribeHashCode(newOrExistingAcc.getOBJECTID(), listId);

     newSubscr.setCONFIRMATION_KEY(confirmKey);
     newSubscr.setUNSUBSCRIBE_KEY(unsubKey);
     updateService.getEm().persist(newSubscr);

     //Update the count of the list
     list.setSUBSCRIBER_COUNT(list.getSUBSCRIBER_COUNT() + 1);
     list = updateService.getEm().merge(list);

     //Send confirmation email
     if (doubleOptin) {
     sendConfirmationEmail(newSubscr);
     }
                
     //For all kinds of exceptions, just log the index with the error message as key, 
     //so that we don't have to specifically tell the calling service what the results
     //are and let them interpret themselves.
     } catch (Exception ex) {
     if(results.get(ex.getMessage()) == null)
     results.put(ex.getMessage(), new ArrayList());
     results.get(ex.getMessage()).add(subscriber);
     }
     }
     return results;
     }*/
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
            if(!errorKey.isEmpty()) {
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
                                    fromFieldValue.get(SubscriberFieldValue_.OWNER))
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
    public void sendConfirmationEmail(Subscription sub) throws IncompleteDataException, BatchProcessingException {
        try {
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
            newEmailBody = mailMergeService.parseUnsubscribeLink(newEmailBody, sub.getUNSUBSCRIBE_KEY()); //Should not be here!

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

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
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

        sub.setSTATUS(SUBSCRIPTION_STATUS.CONFIRMED);
        //sub.setCONFIRMATION_KEY("");//remove confirmation key?

        sub = updateService.getEm().merge(sub);

        return sub;

    }

    public Subscription unsubscribeSubscriber(String email, long listId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

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
}
