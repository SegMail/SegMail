/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this autoEmail file, choose Tools | Templates
 * and open the autoEmail in the editor.
 */
package segmail.component.subscription;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.client.ClientFacade;
import eds.component.client.ClientResourceInterceptor;
import eds.component.data.DBConnectionException;
import eds.component.data.EnterpriseObjectNotFoundException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import eds.entity.client.Client;
import eds.component.config.GenericConfigService;
import eds.component.data.DataValidationException;
import eds.component.data.RelationshipNotFoundException;
import eds.component.mail.InvalidEmailException;
import eds.component.mail.MailService;
import eds.entity.data.EnterpriseObject_;
import eds.entity.mail.Email;
import segmail.entity.subscription.Assign_Client_List;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberAccount_;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.exception.GenericJDBCException;
import seca2.component.landing.LandingService;
import segmail.component.subscription.autoresponder.AutoresponderService;
import segmail.component.subscription.mailmerge.MailMergeService;
import segmail.entity.subscription.ListType;
import segmail.entity.subscription.ListType_;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.FIELD_TYPE;
import segmail.entity.subscription.SUBSCRIPTION_STATUS;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.SubscriberFieldValue_;
import segmail.entity.subscription.SubscriberOwnership;
import segmail.entity.subscription.SubscriberOwnership_;
import segmail.entity.subscription.SubscriptionListFieldComparator;
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
    @EJB private GenericObjectService objectService;
    @EJB private UpdateObjectService updateService;
    @EJB private GenericConfigService configService;
    
    /**
     * External services
     */
    @EJB private LandingService landingService;
    
    /**
     * Delegate services
     */
    @EJB private AutoresponderService autoresponderService;
    @EJB private MailMergeService mailMergeService;
    @EJB private MailService mailService;

    /**
     * [2015.07.12] Because the EJB Interceptor way failed, so this is a very
     * good alternative to omit clientid input for every method call.
     *
     */
    @Inject ClientFacade clientFacade;

    /**
     * Experimental logic for EnterpriseConfiguration testing Got to build a
     * setup EJB in the future!
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setupListTypes() {
        try {
            List<ListType> listTypes = configService.getConfigList(ListType.class);
            if (listTypes == null || listTypes.isEmpty()) {
                ListType remote = new ListType();
                ListType local = new ListType();
                remote.setVALUE(ListType.TYPE.REMOTE.name());
                local.setVALUE(ListType.TYPE.LOCAL.name());

                configService.getEm().persist(remote);
                configService.getEm().persist(local);
            }
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }

    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ListType getListType(String listtypevalue) {
        try {
            CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
            CriteriaQuery<ListType> criteria = builder.createQuery(ListType.class);
            Root<ListType> sourceEntity = criteria.from(ListType.class);

            criteria.select(sourceEntity);
            criteria.where(builder.equal(sourceEntity.get(ListType_.VALUE), listtypevalue));

            List<ListType> results = objectService.getEm().createQuery(criteria)
                    .getResultList();

            return results.get(0);//only return the first match
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SubscriptionList createList(String listname, boolean remote)
            throws IncompleteDataException, EnterpriseObjectNotFoundException {
        try {
            if (listname == null || listname.isEmpty()) {
                throw new IncompleteDataException("List name cannot be empty.");
            }

            // Create the list object and persist it first
            SubscriptionList newList = new SubscriptionList();
            newList.setLIST_NAME(listname);
            newList.setREMOTE(remote);
            
            updateService.getEm().persist(newList);

            // Create the assignment to the client object
            Client client = clientFacade.getClient();
            if (client == null) {
                throw new EnterpriseObjectNotFoundException(Client.class);
            }

            Assign_Client_List listAssignment = new Assign_Client_List();
            listAssignment.setSOURCE(client);
            listAssignment.setTARGET(newList);

            updateService.getEm().persist(listAssignment);

            // Create the default fieldsets and assign it to newList
            SubscriptionListField fieldEmail = new SubscriptionListField(1, true, DEFAULT_EMAIL_FIELD_NAME, FIELD_TYPE.EMAIL, "Email of your subscriber.");
            fieldEmail.setOWNER(newList);

            updateService.getEm().persist(fieldEmail);
            
            return newList;

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    /**
     * Checks of a particular client has no lists created. Used in the setup
     * page.
     *
     * @param clientid
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean hasNoList(long clientid) {
        try {
            long count = this.objectService.countRelationshipsForTarget(clientid, Assign_Client_List.class);
            return (count <= 0);
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } catch (Exception ex) {
            throw new EJBException(ex);
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SubscriptionList> getAllListForClient(long clientid) {
        try {
            List<SubscriptionList> allList
                    = this.objectService.getAllTargetObjectsFromSource(clientid, Assign_Client_List.class, SubscriptionList.class);

            return allList;
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } catch (Exception ex) {
            throw new EJBException(ex);
        }
    }
    
    /**
     * This assumes that the client has constructed a list of SubscriberFieldValue
     * with the correct FIELD_KEY values set in these objects. There should be 
     * another method that takes in just a Map<String,String> of objects.
     * 
     * @param listId
     * @param values
     * @throws EntityNotFoundException
     * @throws IncompleteDataException
     * @throws DataValidationException
     * @throws RelationshipExistsException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void subscribe(long listId, Map<String,Object> values) 
            throws EntityNotFoundException, IncompleteDataException, DataValidationException, RelationshipExistsException, InvalidEmailException {
        try {
            // Find the list object
            SubscriptionList list = objectService.getEnterpriseObjectById(listId, SubscriptionList.class);
            if(list == null)
                throw new EntityNotFoundException(SubscriptionList.class,listId);
            
            // Check if the values provided for the subscriber is valid for the list
            // Check if mandatory fields are not filled in
            String email = "";
            List<SubscriptionListField> fields = getFieldsForSubscriptionList(listId);
            for(SubscriptionListField field : fields ){
                if(field.isMANDATORY() &&
                    (values.get(field.generateKey().toString()) == null || ((String)values.get(field.generateKey().toString())).isEmpty())){
                    throw new IncompleteDataException("Mandatory list field "+field.getFIELD_NAME()+" is missing.");
                }
                if(field.getFIELD_NAME().equals(DEFAULT_EMAIL_FIELD_NAME)){
                    email = (String) values.get(field.generateKey().toString());// If there exist multiple fieldvalues of email, then the latest one will be used
                }
            }
            if(email.isEmpty())
                throw new IncompleteDataException(DEFAULT_EMAIL_FIELD_NAME+" is always required for a subscription.");
            if(!EmailValidator.getInstance().isValid(email))
                throw new DataValidationException("Email address is not valid.");
            
            // Check if the account exist, if not, create a new one
            // Check if account exist with the same client only
            //List<SubscriberAccount> existingAccs = objectService.getEnterpriseObjectsByName(email, SubscriberAccount.class);
            //List<SubscriberAccount> existingAccs = this.getExistingSubscribersForClient(email,clientFacade.getClient().getOBJECTID());
            // Assume that email account is new
            //SubscriberAccount newOrExistingAcc = new SubscriberAccount();
            //newOrExistingAcc.setEMAIL(email);
            // If it is not, use the existing SubscriberAccount record found
            //if(existingAccs != null && !existingAccs.isEmpty())
            //    newOrExistingAcc = existingAccs.get(0); //Get the first match
            SubscriberAccount newOrExistingAcc = getExistingOrCreateNewSubscriber(email,clientFacade.getClient().getOBJECTID());
            
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
            for(SubscriptionListField field : fields){
                
                SubscriberFieldValue value = new SubscriberFieldValue();
                value.setOWNER(newOrExistingAcc);
                value.setFIELD_KEY(field.generateKey().toString());
                
                
                //If the new field value already exist in the DB, just update it
                if(existingFieldValues.contains(value)){
                    value = existingFieldValues.get(existingFieldValues.indexOf(value));//Make use of equals()
                    value.setVALUE(values.get(field.generateKey().toString()).toString());//Update value no matter what
                    updateService.getEm().merge(value);
                }
                else {
                    value.setVALUE(values.get(field.generateKey().toString()).toString());//Update value no matter what
                    value.setSNO(++maxSNO);
                    updateService.getEm().persist(value);
                }
            }
            
            // Create the relationship
            Subscription newSubscr = new Subscription();
            newSubscr.setTARGET(list);
            newSubscr.setSOURCE(newOrExistingAcc);
            newSubscr.setSTATUS(SUBSCRIPTION_STATUS.NEW);
            
            //Check if the subscription already exist
            if(checkSubscribed(email, listId))
                throw new RelationshipExistsException(newSubscr);
            
            updateService.getEm().persist(newSubscr);
            
            //Update the count of the list
            list.setSUBSCRIBER_COUNT(list.getSUBSCRIBER_COUNT()+1);
            list = updateService.getEm().merge(list);
            
            //Send confirmation email
            sendConfirmationEmail(email, listId);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } 
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


    /**
     * A simple, stateless update method that merges the entity and commits.
     * Potentially there could be a generic operation that updates the entity.
     *
     * @param list
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveList(SubscriptionList list) {
        try {
            updateService.getEm().merge(list);

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    /**
     * Deletes a list and all its assignment.
     *
     * Potentially long running operation that requires the background job
     * scheduling mechanism.
     *
     * @param listId
     * @throws eds.component.data.EntityNotFoundException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteList(long listId) throws EntityNotFoundException {
        try {
            this.updateService.deleteObjectDataAndRelationships(listId);
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }


    /**
     *
     * @param listId
     * @return SubscriptionListFieldList if there is at least 1 record available
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SubscriptionListField> getFieldsForSubscriptionList(long listId) {
        try {
            List<SubscriptionListField> allFieldList = this.objectService.getEnterpriseData(listId, SubscriptionListField.class);

            return allFieldList;

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    /**
     * A field list should always be sorted and the SNO of every member should
     * follow a natural order - 1, 2, 3, ..., etc. If users purposely changes an
     * existing member's SNO to skip the natural sequence, then it should be
     * changed back.
     *
     * If a member's SNO has been changed to equal another member's SNO, then
     * the "new" member should take precedence over the "old" member. Each
     * member's old SNO should be interpreted by its list order.
     *
     * @param listId
     * @param newField
     * @return
     * @throws EntityNotFoundException
     * @throws DataValidationException
     * @throws IncompleteDataException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SubscriptionListField addFieldForSubscriptionList(long listId, SubscriptionListField newField)
            throws EntityNotFoundException,
            DataValidationException,
            IncompleteDataException {
        try {
            SubscriptionList list = this.objectService.getEnterpriseObjectById(listId, SubscriptionList.class);

            //Check if list exist first
            if (list == null) {
                throw new EntityNotFoundException(SubscriptionList.class, listId);
            }

            //Check the SNO order
            //Cannot be 1 as 1 is always EmailLegacy
            validateListField(newField);

            //Reorder the SNO for the entire list
            //If the new field added has the same SNO as an existing field, then insert it infront of the existing field
            List<SubscriptionListField> existingFields = getFieldsForSubscriptionList(listId); //All managed?
            Collections.sort(existingFields, new SubscriptionListFieldComparator()); //Doesn't include new field yet
            for (int i = existingFields.size(); i > 0; i--) {
                SubscriptionListField field = updateService.getEm().merge(existingFields.get(i - 1));
                
                if (newField.getSNO() <= field.getSNO()) {
                    field.setSNO(i + 1);
                } else {
                    field.setSNO(i);
                }
                //updateService.getEm().merge(field); //Assuming the entity is already managed
            }
            newField.setOWNER(list);
            updateService.getEm().persist(newField);
            return newField;

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    /**
     * A field list should always be sorted and the SNO of every member should
     * follow a natural order - 1, 2, 3, ..., etc. If users purposely changes an
     * existing member's SNO to skip the natural sequence, then it should be
     * changed back.
     *
     * If a member's SNO has been changed to equal another member's SNO, then
     * the "new" member should take precedence over the "old" member. Each
     * member's old SNO should be interpreted by its list order.
     * 
     * Don't allow any change in SNO at the moment
     * 
     * @param fieldList 
     * @throws eds.component.data.DataValidationException 
     * @throws eds.component.data.IncompleteDataException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateSubscriptionListFields(List<SubscriptionListField> fieldList) 
            throws DataValidationException, IncompleteDataException {
        try {
            EntityManager em = updateService.getEm();
            for(SubscriptionListField f : fieldList){
                validateListField(f);
                em.merge(f);
            }
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Map<Long,Map<String,String>> getSubscriberValuesMap(long listId, int startIndex, int limit){
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
                    .getResultList();
            
            Map<Long,Map<String,String>> resultMap = 
                    new HashMap<>();
            
            //Collections.sort(results);//Sorting before creating the map may not be the most correct solution but it's the most efficient and effective one at the moment
            for(SubscriberFieldValue field : results){
                SubscriberAccount subscriber = field.getOWNER();
                if(!resultMap.containsKey(subscriber.getOBJECTID())){
                    resultMap.put(subscriber.getOBJECTID(), new HashMap<String,String>());
                    
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

    public void validateListField(SubscriptionListField field) throws DataValidationException, IncompleteDataException {
        if (field.getSNO() == 1
                && !field.getTYPE().equals(FIELD_TYPE.EMAIL.name())
                && !field.getFIELD_NAME().equals("Email")) {
            throw new DataValidationException("Only the \"Email\" field can have order number 1");
        }

        if (field.getSNO() < 1) {
            throw new DataValidationException("Field order must be greater than 1 (1 is always \"Email\").");
        }

        if (field.getFIELD_NAME() == null || field.getFIELD_NAME().isEmpty()) {
            throw new IncompleteDataException("Field name must not be empty.");
        }

        if (field.getDESCRIPTION() == null || field.getDESCRIPTION().isEmpty()) {
            throw new IncompleteDataException("Description must not be empty.");
        }

    }
    
    /**
     * Non-update helper method to construct a list of SubscriberFieldValue to be 
     * populated. Mainly for frontend forms usage. The returned result is not 
     * ready to be updated into the DB yet because it is still missing the 
     * SubscriberAccount EnterpriseObject.
     * 
     * @param fields
     * @return 
     */
    public Map<String,Object> constructSubscriberFieldValues(List<SubscriptionListField> fields){
        Map<String,Object> values = new HashMap<>();
        
        if(fields == null || fields.isEmpty())
            return values;
        
        List<SubscriptionListField> sortedFields = new ArrayList<>(fields);
        Collections.sort(sortedFields);
        
        for (int i=0; i<fields.size(); i++){
            SubscriptionListField field = fields.get(i);
            values.put(field.generateKey().toString(),"");
        }
        
        return values;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void sendConfirmationEmail(String email, long listId) 
            throws IncompleteDataException, EntityNotFoundException, InvalidEmailException{
        try {
            //Retrieve "Send as" from the list
            SubscriptionList list = objectService.getEnterpriseObjectById(listId, SubscriptionList.class);
            if(list == null)
                throw new EntityNotFoundException(SubscriptionList.class,listId);
            
            String sendAs = list.getSEND_AS_EMAIL();
            if(sendAs == null || sendAs.isEmpty())
                throw new IncompleteDataException("Please set \"Send As\" address before sending confirmation emails.");
            
            //Retrieve the autoemail from list using AutoresponderService
            //List<AutoresponderEmail> assignedAutoEmails = objectService.getAllSourceObjectsFromTarget(
            //        listId,Assign_AutoConfirmEmail_List.class, AutoConfirmEmail.class);
            List<AutoresponderEmail> assignedAutoEmails = autoresponderService.getAssignedAutoEmailsForList(listId, AUTO_EMAIL_TYPE.CONFIRMATION);
            AutoresponderEmail assignedConfirmEmail = (assignedAutoEmails == null || assignedAutoEmails.isEmpty())?
                            null : assignedAutoEmails.get(0);
            
            if(assignedConfirmEmail == null)
                throw new IncompleteDataException("Please assign a Confirmation email before adding subscribers.");
            
            //Parse all mailmerge functions using MailMergeService
            String newEmailBody = assignedConfirmEmail.getBODY();
            newEmailBody = mailMergeService.parseConfirmationLink(newEmailBody, email, listId);
            newEmailBody = mailMergeService.parseListAttributes(newEmailBody, listId);
            
            //Send the email using MailService
            
            Email confirmEmail = new Email();
            confirmEmail.setSENDER_ADDRESS(list.getSEND_AS_EMAIL());
            confirmEmail.setSENDER_NAME(list.getSEND_AS_NAME());
            confirmEmail.setBODY(newEmailBody);
            confirmEmail.setSUBJECT(assignedConfirmEmail.getSUBJECT());
            confirmEmail.addRecipient(email);
            
            mailService.sendEmailByAWS(confirmEmail, true);
            //mailService.sendEmailBySMTP(confirmEmail);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    
    public List<SubscriberAccount> getExistingSubscribersForClient(String email, long clientId) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<SubscriberAccount> query = builder.createQuery(SubscriberAccount.class);
        
        Root<SubscriberAccount> fromSubscAcc = query.from(SubscriberAccount.class);
        Root<SubscriberOwnership> fromSubscOwnership = query.from(SubscriberOwnership.class);
        
        query.select(fromSubscAcc);
        query.where(builder.and(
                builder.equal(fromSubscAcc.get(SubscriberAccount_.EMAIL), email),
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

    /**
     * Retrieves existing subscriber or create a new one using an email address
     * for a given client.
     * 
     * @param email
     * @param clientId
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private SubscriberAccount getExistingOrCreateNewSubscriber(String email, long clientId) {
        List<SubscriberAccount> existingAccs = this.getExistingSubscribersForClient(email,clientId);
        //If the email subscriber exists for the given client, return the found subscriber
        if(existingAccs != null && !existingAccs.isEmpty())
            return existingAccs.get(0);
        
        //If not found, create new and assign it to the client
        SubscriberAccount newOrExistingAcc = new SubscriberAccount();
        newOrExistingAcc.setEMAIL(email);
        
        this.updateService.getEm().persist(newOrExistingAcc);
        
        //Retrieve the client object
        Client client = objectService.getEnterpriseObjectById(clientId, Client.class);
        
        //Assign it to the client
        SubscriberOwnership assign = new SubscriberOwnership();
        assign.setTARGET(client);
        assign.setSOURCE(newOrExistingAcc);
        
        this.updateService.getEm().persist(assign);
        
        return newOrExistingAcc;
    }
    
    public List<Subscription> getSubscriptions(String subscriberEmail, long listId){
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
    public Subscription confirmSubscriber(String subscriberEmail, long listId) 
            throws RelationshipNotFoundException{
        List<Subscription> subscriptions = getSubscriptions(subscriberEmail,listId);
        
        if(subscriptions == null || subscriptions.isEmpty())
            throw new RelationshipNotFoundException("The email "+subscriberEmail+" or list ID "+listId+" was not found.");
        
        //Subscriptions should be unique, so only 1 result is expected
        Subscription subsc = subscriptions.get(0);
        subsc.setSTATUS(SUBSCRIPTION_STATUS.CONFIRMED);
        
        this.updateService.getEm().merge(subsc);
        
        return subsc;
        
    }
}
