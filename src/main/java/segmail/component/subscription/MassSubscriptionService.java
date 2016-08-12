/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription;

import eds.component.GenericObjectService;
import eds.component.batch.BatchProcessingException;
import eds.component.data.DataValidationException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.mail.InvalidEmailException;
import eds.component.mail.MailService;
import eds.entity.client.Client;
import eds.entity.mail.Email;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import org.apache.commons.validator.routines.EmailValidator;
import org.joda.time.DateTime;
import seca2.bootstrap.module.Client.ClientContainer;
import static segmail.component.subscription.SubscriptionService.DEFAULT_EMAIL_FIELD_NAME;
import segmail.component.subscription.autoresponder.AutoresponderService;
import segmail.component.subscription.mailmerge.MailMergeService;
import segmail.entity.subscription.SUBSCRIPTION_STATUS;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.SubscriberFieldValueComparator;
import segmail.entity.subscription.SubscriberFieldValue_;
import segmail.entity.subscription.SubscriberOwnership;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.autoresponder.AUTO_EMAIL_TYPE;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class MassSubscriptionService {

    public final int MAX_RECORDS_PER_FLUSH = 100;

    @EJB
    private GenericObjectService objService;
    @EJB
    private SubscriptionService subService;
    @EJB
    private ListService listService;

    @EJB
    private AutoresponderService autoresponderService;
    @EJB
    private MailMergeService mailMergeService;
    @EJB
    private MailService mailService;

    @Inject
    SubscriptionContainer subContainer;
    @Inject
    ClientContainer clientContainer;

    /**
     * There is no check for duplicates or already subscribed, like single
     * subscribe() method. The latest duplicates will overwrite all previous
     * version. If the existing subscriber has a particular field value, and a
     * later version of it has another new different field value, both of them
     * will be retained.
     *
     * No emails will be sent out with this mode as of now.
     *
     * @param subscribers
     * @return a Map of error messages and their records. A list of possible
     * error messages:
     * <ul>
     * <li>Mandatory list field [SubscriptionListField.FIELD_NAME] is
     * missing.</li>
     * <li>Invalid email</li>
     *
     * @throws EntityNotFoundException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Map<String, List<Map<String, Object>>> massSubscribe(List<Map<String, Object>> subscribers, boolean doubleOptin)
            throws EntityNotFoundException {

        //Set up the return results Map
        Map<String, List<Map<String, Object>>> results = new HashMap<>();

        SubscriptionList list = subContainer.getList(); //objService.getEnterpriseObjectById(listId, SubscriptionList.class); //DB hit, can be cached
        if (list == null) {
            throw new EntityNotFoundException("SubscriptionList not initialized.");
        }

        Client client = clientContainer.getClient(); //objService.getEnterpriseObjectById(clientId, Client.class); //DB hit, can be cached
        if (client == null) {
            throw new EntityNotFoundException("Client not initialized.");
        }

        List<SubscriptionListField> fields = subContainer.getListFields(); //listService.getFieldsForSubscriptionList(listId); //DB hit, can be cached

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
                    email = email.trim(); //For some reason JS cannot remove this \r character
                    //Validate email format
                    if (!EmailValidator.getInstance().isValid(email)) {
                        errorKey = "Invalid email";
                        break;
                    }
                    subscriber.put((String) field.generateKey(), email); //put in the processed email string
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
         */
        //First we have to retrieve all SubscriberAccount objects
        //This assumes that SubscriberAccount and SubscriberOwneship always exists together and never mutually exclusive.
        List<SubscriberAccount> existingSubscribers = subService.getSubscribersForClientByEmails(emails, client.getOBJECTID()); //DB hit, can be cached
        Collections.sort(existingSubscribers);//Meaningless...not entirely...
        List<Long> existingSubscriberIds = new ArrayList<>();
        for (SubscriberAccount account : existingSubscribers) {
            existingSubscriberIds.add(account.getOBJECTID());
        }
        //And also all their SubscriberFieldValues
        List<SubscriberFieldValue> existingFieldValues = subService.getSubscriberValuesBySubscribers(existingSubscriberIds); //DB hit, can be cached
        Collections.sort(existingFieldValues); //Default sorting method for EnterpriseData
        //All existing subscriptions
        //Just in case the SubscriberAccount, Ownership and FieldValues are created but the subscription is not,
        //this is used for checking.
        List<Subscription> existingSubscription = subService.getSubscriptionsByEmails(emails, list.getOBJECTID());

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
        List<SubscriberOwnership> createNewSubOwnership = new ArrayList<>();
        List<SubscriberFieldValue> updateFieldValueList = new ArrayList<>();
        List<SubscriberFieldValue> createFieldValueList = new ArrayList<>();
        List<Subscription> createNewSubscription = new ArrayList<>();

        //try {
        for (int i = 0; i < survivors.size(); i++) {
            Map<String, Object> survivor = survivors.get(i);
            String email = emails.get(i); //emails and survivors have the same order
            List<SubscriberFieldValue> subFieldValues = new ArrayList<>();
            SubscriberAccount account = null; //Hypothetical account

            //Find the existing SubscriberAccount
            for (SubscriberAccount existingSubscriber : existingSubscribers) {
                //If found, get SubscriberFieldValues from existingFieldValues
                if (existingSubscriber.getEMAIL() == null ? email == null : existingSubscriber.getEMAIL().equals(email)) {
                    account = existingSubscriber;
                    break;
                }
            }
            //Find all its existing SubscriberFieldValues
            for (SubscriberFieldValue existingFieldValue : existingFieldValues) {
                if (existingFieldValue.getOWNER().equals(account)) {
                    subFieldValues.add(existingFieldValue);
                }
            }
            Collections.sort(subFieldValues, new SubscriberFieldValueComparator()); //Sorting is necessary because u need to get highest SNO

            //If subFieldValues exists, it means that the subscriber already exist and might require updates.
            //If it exists in survivor Map, update the value
            //If it doesn't exist in survivor Map, create it
            for (String key : survivor.keySet()) { //Loop through incoming keySet
                if (!containsKey(fields, key)) {
                    continue;
                }
                boolean createNew = true;
                for (SubscriberFieldValue existingFieldValue : subFieldValues) { //Loop through existing field values
                    //If exist, add it into updateFieldValueList
                    //Note that if duplicates exist here, the latest value will be taken
                    if (key != null && key.equals(existingFieldValue.getFIELD_KEY())) {
                        if (survivor.get(key) != null
                                && !((String) survivor.get(key)).equals(existingFieldValue.getVALUE())) {
                            existingFieldValue.setVALUE((String) survivor.get(key)); //Update existing value
                            updateFieldValueList.add(existingFieldValue);
                        }
                        createNew = false;
                        break; //break from this loop
                    }
                }
                //If it was not found, construct it and add it in to createFieldValueList
                //First check if it is a valid field with the List
                if (createNew) {
                    SubscriberFieldValue newValue = new SubscriberFieldValue();
                    newValue.setFIELD_KEY(key);
                    newValue.setVALUE((String) survivor.get(key));
                    if (account == null) {
                        account = new SubscriberAccount();
                        account.setEMAIL(email);
                        createNewSubAccList.add(account);
                        //Create SubscriberOwnership!!!
                        SubscriberOwnership ownership = new SubscriberOwnership();
                        ownership.setSOURCE(account);
                        ownership.setTARGET(client);
                        createNewSubOwnership.add(ownership);

                    }
                    newValue.setOWNER(account);

                    //Set the SNO
                    int highestSNO = (subFieldValues.isEmpty())
                            ? 0 : subFieldValues.get(subFieldValues.size() - 1).getSNO() + 1;
                    newValue.setSNO(highestSNO);

                    createFieldValueList.add(newValue);
                    //Need to add and sort otherwise you won't get the correct highestSNO
                    subFieldValues.add(newValue);
                    Collections.sort(subFieldValues, new SubscriberFieldValueComparator());

                }
            }
            //Decide if we should create Subscription
            Subscription subscription = new Subscription();
            subscription.setSOURCE(account);
            subscription.setTARGET(list);
            if (doubleOptin) {
                subscription.setSTATUS(SUBSCRIPTION_STATUS.NEW);
            } else {
                subscription.setSTATUS(SUBSCRIPTION_STATUS.CONFIRMED);
            }

            if (!existingSubscription.contains(subscription)) {
                createNewSubscription.add(subscription);
            }
        }

        //Time to do db updates and inserts
        try {
            DateTime start = DateTime.now();
            int updateFieldValueResult = updateFieldValueList(updateFieldValueList);
            createNewSubAccList = createNewSubscriberAccounts(createNewSubAccList);
            createFieldValueList = createFieldValueList(createFieldValueList);
            //Create SubscriberOwnership!!!
            createNewSubOwnership = createSubscriberOwnership(createNewSubOwnership);
            //Create Subscription!!!
            createNewSubscription = createSubscription(createNewSubscription);
            //Send confirmation email if double optin is turned on
            if (doubleOptin) {
                sendConfirmationEmails(createNewSubscription);
            }
            //Update the number of subscribers (async call)
            subService.updateSubscriberCount(list.getOBJECTID());
            DateTime end = DateTime.now();
            long timeTaken = end.getMillis() - start.getMillis();
            System.out.println("Time taken to update " + subscribers.size() + " subscribers is " + timeTaken);
        } catch (Throwable ex) {
            //If any exception occurs, return the entire list of survivors
            String errorMsg = "";
            errorMsg = ex.getMessage();
            if(errorMsg == null || errorMsg.isEmpty())
                errorMsg = ex.getClass().getSimpleName() + ((ex.getCause() == null) ? "" : ex.getCause().getMessage());
            results.put(errorMsg, survivors);
        }

        return results;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<SubscriberAccount> createNewSubscriberAccounts(List<SubscriberAccount> newSubAccList) {
        for (int i = 0; i < newSubAccList.size(); i++) {
            SubscriberAccount acc = newSubAccList.get(i);
            objService.getEm().persist(acc);

            if (i >= MAX_RECORDS_PER_FLUSH || i >= newSubAccList.size() - 1) {
                objService.getEm().flush();
            }
        }
        return newSubAccList;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int updateFieldValueList(List<SubscriberFieldValue> fieldValueList) {
        int results = 0;

        for (int i = 0; i < fieldValueList.size(); i++) {
            SubscriberFieldValue fieldValue = fieldValueList.get(i);
            CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
            CriteriaUpdate<SubscriberFieldValue> query = builder.createCriteriaUpdate(SubscriberFieldValue.class);
            Root<SubscriberFieldValue> fromValue = query.from(SubscriberFieldValue.class);

            query.set(SubscriberFieldValue_.VALUE, fieldValue.getVALUE());
            query.where(
                    builder.and(
                            builder.equal(fromValue.get(SubscriberFieldValue_.OWNER), fieldValue.getOWNER())),
                    builder.equal(fromValue.get(SubscriberFieldValue_.START_DATE), fieldValue.getSTART_DATE()),
                    builder.equal(fromValue.get(SubscriberFieldValue_.END_DATE), fieldValue.getEND_DATE()),
                    builder.equal(fromValue.get(SubscriberFieldValue_.SNO), fieldValue.getSNO()
                    )
            );

            results += objService.getEm().createQuery(query)
                    .executeUpdate();

            if (i >= MAX_RECORDS_PER_FLUSH || i >= fieldValueList.size() - 1) {
                objService.getEm().flush();
            }
        }
        return results;
    }

    public List<SubscriberFieldValue> createFieldValueList(List<SubscriberFieldValue> fieldValueList) {
        for (int i = 0; i < fieldValueList.size(); i++) {
            SubscriberFieldValue acc = fieldValueList.get(i);
            objService.getEm().persist(acc);

            if (i >= MAX_RECORDS_PER_FLUSH || i >= fieldValueList.size() - 1) {
                objService.getEm().flush();
            }
        }
        return fieldValueList;
    }

    public List<SubscriberOwnership> createSubscriberOwnership(List<SubscriberOwnership> createNewSubOwnership) {
        for (int i = 0; i < createNewSubOwnership.size(); i++) {
            SubscriberOwnership acc = createNewSubOwnership.get(i);
            objService.getEm().persist(acc);

            if (i >= MAX_RECORDS_PER_FLUSH || i >= createNewSubOwnership.size() - 1) {
                objService.getEm().flush();
            }
        }
        return createNewSubOwnership;
    }

    public List<Subscription> createSubscription(List<Subscription> createNewSubscription) {
        for (int i = 0; i < createNewSubscription.size(); i++) {
            Subscription acc = createNewSubscription.get(i);
            String confirmKey = subService.getConfirmationHashCode(acc.getSOURCE().getOBJECTID(), acc.getTARGET().getOBJECTID());
            String unsubKey = subService.getUnsubscribeHashCode(acc.getSOURCE().getOBJECTID(), acc.getTARGET().getOBJECTID());

            acc.setCONFIRMATION_KEY(confirmKey);
            acc.setUNSUBSCRIBE_KEY(unsubKey);
            objService.getEm().persist(acc);

            if (i >= MAX_RECORDS_PER_FLUSH || i >= createNewSubscription.size() - 1) {
                objService.getEm().flush();
            }
        }
        return createNewSubscription;
    }

    private boolean containsKey(List<SubscriptionListField> fields, String key) {
        for (SubscriptionListField field : fields) {
            if (key != null && field.generateKey() != null && key.equals((String) field.generateKey())) {
                return true;
            }
        }
        return false;
    }

    /*@Asynchronous
     @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
     public void sendConfirmationEmails(List<Subscription> newSubscriptions) 
     throws IncompleteDataException, 
     BatchProcessingException, 
     DataValidationException, 
     InvalidEmailException {
     for(Subscription newSubscription : newSubscriptions) {
     subService.sendConfirmationEmail(newSubscription);
     }
     }*/
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendConfirmationEmails(List<Subscription> newSubscriptions)
            throws IncompleteDataException,
            BatchProcessingException,
            DataValidationException,
            InvalidEmailException {
        //Group the SubscriberAccounts together by SubscriptionList
        Map<SubscriptionList, List<SubscriberAccount>> groupedByLists = new HashMap<>();
        for (Subscription subscription : newSubscriptions) {
            List<SubscriberAccount> subscribers = groupedByLists.get(subscription.getTARGET());
            if (subscribers == null) {
                subscribers = new ArrayList<>();
                groupedByLists.put(subscription.getTARGET(), subscribers);
            }
            subscribers.add(subscription.getSOURCE());
        }

        for (SubscriptionList list : groupedByLists.keySet()) {
            //Get the necessary information needed
            List<AutoresponderEmail> emails = autoresponderService.getAssignedAutoEmailsForList(list.getOBJECTID(), AUTO_EMAIL_TYPE.CONFIRMATION);
            if (emails == null || emails.isEmpty()) {
                throw new IncompleteDataException("Confirmation emails are not assigned to list " + list.getLIST_NAME() + " yet.");
            }

            AutoresponderEmail assignedConfirmEmail = emails.get(0);

            //Send the email using MailService
            Email confirmEmail = new Email();
            confirmEmail.setSENDER_ADDRESS(list.getSEND_AS_EMAIL());
            confirmEmail.setSENDER_NAME(list.getSEND_AS_NAME());
            confirmEmail.setBODY(assignedConfirmEmail.getBODY());
            confirmEmail.setSUBJECT(assignedConfirmEmail.getSUBJECT());

            for (SubscriberAccount subscriber : groupedByLists.get(list)) {

                confirmEmail.setRECIPIENTS(new HashSet<String>());
                confirmEmail.addSingleRecipient(subscriber.getEMAIL());
        //Parse all mailmerge functions using MailMergeService
                //Get the confirmation key first
                String confirmKey = "";
                for (Subscription newSubscription : newSubscriptions) {
                    if (newSubscription.getSOURCE().equals(subscriber) && newSubscription.getTARGET().equals(list)) {
                        confirmKey = newSubscription.getCONFIRMATION_KEY();
                        if (confirmKey == null || confirmKey.isEmpty()) {
                            throw new RuntimeException("Confirmation key is not generated for subscription " + newSubscription.toString());
                        }
                        break;
                    }
                }

                if (confirmKey == null || confirmKey.isEmpty()) {
                    throw new RuntimeException("No subscription found for subscriber " + subscriber.toString());
                }
                confirmEmail.setBODY(mailMergeService.parseConfirmationLink(confirmEmail.getBODY(), confirmKey));
                mailService.queueEmail(confirmEmail, DateTime.now());
                
            }
        }
    }
}
