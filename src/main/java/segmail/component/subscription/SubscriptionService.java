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
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import eds.entity.client.Client;
import eds.component.config.GenericConfigService;
import eds.component.data.DataValidationException;
import eds.entity.data.EnterpriseObject_;
import eds.entity.data.EnterpriseRelationship_;
import segmail.entity.subscription.Assign_Client_List;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberAccount_;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionList_;
import segmail.entity.subscription.email.AutoresponderEmail;
import segmail.entity.subscription.email.AutoresponderEmail_;
import segmail.entity.subscription.email.Assign_AutoresponderEmail_List;
import segmail.entity.subscription.email.Assign_AutoresponderEmail_Client;
import java.util.ArrayList;
import java.util.Collections;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.exception.GenericJDBCException;
import segmail.entity.subscription.ListType;
import segmail.entity.subscription.ListType_;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.FIELD_TYPE;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.SubscriberFieldValue_;
import segmail.entity.subscription.SubscriptionListFieldComparator;
import segmail.entity.subscription.Subscription_;
import segmail.entity.subscription.email.Assign_AutoConfirmEmail_List;
import segmail.entity.subscription.email.Assign_AutoWelcomeEmail_List;
import segmail.entity.subscription.email.AutoConfirmEmail;
import segmail.entity.subscription.email.AutoEmailTypeFactory;
import segmail.entity.subscription.email.AutoWelcomeEmail;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
@Interceptors({ClientResourceInterceptor.class})
public class SubscriptionService {

    public static final String DEFAULT_EMAIL_FIELD_NAME = "Email";

    @EJB private GenericObjectService objectService;
    @EJB private UpdateObjectService updateService;
    @EJB private GenericConfigService configService;

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

            //1. Create the list object and persist it first
            SubscriptionList newList = new SubscriptionList();
            newList.setLIST_NAME(listname);
            newList.setREMOTE(remote);

            updateService.getEm().persist(newList);

            //2. Create the assignment to the client object
            Client client = clientFacade.getClient();
            if (client == null) {
                throw new EnterpriseObjectNotFoundException(Client.class);
            }
            //Test at this point whethe the newList object still gets persisted

            Assign_Client_List listAssignment = new Assign_Client_List();
            listAssignment.setSOURCE(client);
            listAssignment.setTARGET(newList);

            updateService.getEm().persist(listAssignment);

            //3. Create the default fieldsets and assign it to newList
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
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void subscribe(long listId, List<SubscriberFieldValue> values) 
            throws EntityNotFoundException, IncompleteDataException, DataValidationException, RelationshipExistsException {
        try {
            // Find the list object
            SubscriptionList list = objectService.getEnterpriseObjectById(listId, SubscriptionList.class);
            if(list == null)
                throw new EntityNotFoundException(SubscriptionList.class,listId);
            
            // Find if there is already an existing SubscriberAccount with the email
            String email = "";
            for(SubscriberFieldValue value : values){
                if(value.getFIELD() == null){
                    throw new IncompleteDataException("Field value "+value.getVALUE()+" needs to have a SubscriptionListField object attached.");
                }
                if(value.getFIELD().getFIELD_NAME().equals(DEFAULT_EMAIL_FIELD_NAME)){
                    email = value.getVALUE();// If there exist multiple fieldvalues of email, then the latest one will be used
                }
            }
            if(email.isEmpty())
                throw new IncompleteDataException(DEFAULT_EMAIL_FIELD_NAME+" is always required for a subscription.");
            if(!EmailValidator.getInstance().isValid(email))
                throw new DataValidationException("Email address is not valid.");
            
            // Check if the account exist, if not, create a new one
            List<SubscriberAccount> existingAccs = objectService.getEnterpriseObjectsByName(email, SubscriberAccount.class);
            // Assume that email account is new
            SubscriberAccount newOrExistingAcc = new SubscriberAccount();
            newOrExistingAcc.setEMAIL(email);
            // If it is not, use the existing SubscriberAccount record found
            if(existingAccs != null && !existingAccs.isEmpty())
                newOrExistingAcc = existingAccs.get(0); //Get the first match
            
            //Update the subscriber account first by merging
            //Even if it exist, it is required to merge it to manage it later
            newOrExistingAcc = updateService.getEm().merge(newOrExistingAcc);
            updateService.getEm().flush();
            
            // Connect all new field values to the account and create them in the DB
            // If you pass in a set of fieldvalues with the same field object, then the latest one will be the last to be inserted and overwrites the rest
            for(SubscriberFieldValue value : values){
                value.setOWNER(newOrExistingAcc);
                updateService.getEm().persist(value);
            }
            
            // Create the relationship
            Subscription newSubscr = new Subscription();
            newSubscr.setTARGET(list);
            newSubscr.setSOURCE(newOrExistingAcc);
            newSubscr.setSTATUS(Subscription.STATUS.NEW);
            
            //Check if the subscription already exist
            if(checkSubscribed(email, listId))
                throw new RelationshipExistsException(newSubscr);
            
            updateService.getEm().persist(newSubscr);
            
            //Update the count of the list
            list.setCOUNT(list.getCOUNT()+1);
            list = updateService.getEm().merge(list);
            
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
        } catch (Exception ex) {
            throw new EJBException(ex);
        }
    }

    /**
     * Get all available Confirmation emails assigned to a Client.
     *
     * @param clientid
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<AutoConfirmEmail> getAvailableConfirmationEmailForClient(long clientid) {
        try {
            List<AutoConfirmEmail> results = objectService
                    .getAllSourceObjectsFromTarget(clientid, Assign_AutoresponderEmail_Client.class,
                            AutoConfirmEmail.class);

            return results;
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    /**
     * Get all available Welcome emails assigned to a Client.
     *
     * @param clientid
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<AutoWelcomeEmail> getAvailableWelcomeEmailForClient(long clientid) {
        try {
            List<AutoWelcomeEmail> results = objectService
                    .getAllSourceObjectsFromTarget(clientid, Assign_AutoresponderEmail_Client.class,
                            AutoWelcomeEmail.class);

            return results;
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    /**
     * Creates a new AutoresponderEmail without assigning to any Client or List.
     *
     * @param subject
     * @param body
     * @param type
     * @return
     * @throws EntityExistsException
     * @throws IncompleteDataException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AutoresponderEmail createAutoEmailWithoutAssignment(String subject, String body, AutoEmailTypeFactory.TYPE type)
            throws EntityExistsException, IncompleteDataException {
        try {

            AutoresponderEmail newAutoEmail = AutoEmailTypeFactory.getAutoEmailTypeInstance(type);
            newAutoEmail.setBODY(body);
            newAutoEmail.setSUBJECT(subject);

            this.checkAutoEmail(newAutoEmail);

            updateService.getEm().persist(newAutoEmail);

            return newAutoEmail;

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    /**
     * Get a list of all AutoresponderEmails for a given type and given subject.
     *
     *
     * @param subject
     * @param type
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<? extends AutoresponderEmail> getAutoEmailsBySubjectAndType(String subject, AutoEmailTypeFactory.TYPE type) {
        try {
            Class<? extends AutoresponderEmail> e = AutoEmailTypeFactory.getAutoEmailTypeClass(type);
            CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
            CriteriaQuery<? extends AutoresponderEmail> query = builder.createQuery(e);
            Root<? extends AutoresponderEmail> sourceEntity = query.from(e);

            query.where(builder.and(builder.equal(sourceEntity.get(AutoresponderEmail_.SUBJECT), subject)
            //builder.equal(sourceEntity.get(AutoresponderEmail_.FIELD_TYPE), type) we define type as a Entity class instead of an enum
            ));

            List<? extends AutoresponderEmail> results = objectService.getEm().createQuery(query)
                    .getResultList();

            return results;
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    /**
     * Assigns an AutoresponderEmail to a Client.
     *
     * @param autoEmailId
     * @param clientId
     * @throws EntityNotFoundException
     * @throws RelationshipExistsException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void assignAutoEmailToClient(long autoEmailId, long clientId)
            throws EntityNotFoundException, RelationshipExistsException {
        try {
            List<Assign_AutoresponderEmail_Client> assignments = objectService.getRelationshipsForObject(autoEmailId, clientId, Assign_AutoresponderEmail_Client.class);
            if (assignments != null && !assignments.isEmpty()) {
                throw new RelationshipExistsException(assignments.get(0));
            }

            AutoresponderEmail autoEmail = this.objectService.getEnterpriseObjectById(autoEmailId, AutoresponderEmail.class);
            if (autoEmail == null) {
                throw new EntityNotFoundException("Autoresponder email id " + autoEmailId + " not found!");
            }

            Client client = this.objectService.getEnterpriseObjectById(clientId, Client.class);
            if (client == null) {
                throw new EntityNotFoundException("Client id " + client + " not found!");
            }

            Assign_AutoresponderEmail_Client assignment = new Assign_AutoresponderEmail_Client();
            assignment.setSOURCE(autoEmail);
            assignment.setTARGET(client);

            objectService.getEm().persist(assignment);

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } /*catch (Exception ex) {
         throw new EJBException(ex);
         }*/

    }

    /**
     * Assigns a ConfirmationEmail to a Subscriptionlist
     *
     * @param confirmationEmailId
     * @param listId
     * @return
     * @throws EntityNotFoundException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Assign_AutoConfirmEmail_List assignConfirmationEmailToList(long confirmationEmailId, long listId) throws EntityNotFoundException {
        try {
            //Retrieve both objects and check they both exists
            AutoConfirmEmail confirmationEmail = objectService.getEnterpriseObjectById(confirmationEmailId, AutoConfirmEmail.class);
            if (confirmationEmail == null) {
                throw new EntityNotFoundException(AutoConfirmEmail.class, confirmationEmailId);
            }

            SubscriptionList list = objectService.getEnterpriseObjectById(listId, SubscriptionList.class);
            if (list == null) {
                throw new EntityNotFoundException(SubscriptionList.class, listId);
            }

            Assign_AutoConfirmEmail_List newAssignment = new Assign_AutoConfirmEmail_List();
            newAssignment.setSOURCE(confirmationEmail);
            newAssignment.setTARGET(list);

            //Let's not complicate things and just do a delete-all-and-add-new
            //this.removeAllAssignedConfirmationEmailFromList(listId);
            this.updateService.deleteRelationshipByTarget(listId, Assign_AutoConfirmEmail_List.class);
            objectService.getEm().persist(newAssignment);

            return newAssignment;

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    /**
     * Assigns a WelcomeEmail to a Subscriptionlist.
     *
     * @param welcomeEmailId
     * @param listId
     * @return
     * @throws EntityNotFoundException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Assign_AutoWelcomeEmail_List assignWelcomeEmailToList(long welcomeEmailId, long listId) throws EntityNotFoundException {
        try {
            //Retrieve both objects and check they both exists
            AutoWelcomeEmail welcomeEmail = objectService.getEnterpriseObjectById(welcomeEmailId, AutoWelcomeEmail.class);
            if (welcomeEmail == null) {
                throw new EntityNotFoundException(AutoWelcomeEmail.class, welcomeEmailId);
            }

            SubscriptionList list = objectService.getEnterpriseObjectById(listId, SubscriptionList.class);
            if (list == null) {
                throw new EntityNotFoundException(SubscriptionList.class, listId);
            }

            Assign_AutoWelcomeEmail_List newAssignment = new Assign_AutoWelcomeEmail_List();
            newAssignment.setSOURCE(welcomeEmail);
            newAssignment.setTARGET(list);

            //Let's not complicate things and just do a delete-all-and-add-new
            this.removeAllAssignedWelcomeEmailFromList(listId);
            objectService.getEm().persist(newAssignment);

            return newAssignment;

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    /**
     * Creates a new AutoresponderEmail and assigns it to the ClientFacade in
     * context. If no ClientFacade is available in context, then an
     * IncompleteDataException will be thrown.
     *
     * @param subject
     * @param body
     * @param type
     * @return
     * @throws EntityExistsException
     * @throws IncompleteDataException
     * @throws RelationshipExistsException
     * @throws EntityNotFoundException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AutoresponderEmail createAndAssignAutoEmail(String subject, String body, AutoEmailTypeFactory.TYPE type)
            throws EntityExistsException, IncompleteDataException, RelationshipExistsException, EntityNotFoundException {
        try {
            //Create the new autoEmail first
            AutoresponderEmail newAutoEmail = this.createAutoEmailWithoutAssignment(subject, body, type);

            //Get the client
            Client client = clientFacade.getClient();
            if (client == null) {
                throw new IncompleteDataException("No client id provided.");
            }
            //Assign it to the client
            this.assignAutoEmailToClient(newAutoEmail.getOBJECTID(), client.getOBJECTID());

            return newAutoEmail;

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } /*catch (Exception ex) {
         throw new EJBException(ex);
         } catch (EntityNotFoundException ex) {
         throw new RuntimeException(ex); // Something is very wrong here!
         }*/

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AutoresponderEmail saveAutoEmail(AutoresponderEmail autoEmail)
            throws IncompleteDataException, EntityExistsException {
        try {
            checkAutoEmail(autoEmail);

            return updateService.getEm().merge(autoEmail);

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    /**
     *
     * @param autoEmailId
     * @throws EntityNotFoundException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteAutoEmail(long autoEmailId) throws EntityNotFoundException {
        try {
            //Again, changing to casting because https://github.com/SegMail/SegMail/issues/35 
            //EmailTemplate delTemplate = objectService.getEnterpriseObjectById(autoEmailId, AutoresponderEmail.class);
            /**
             * EmailTemplate delTemplate = (EmailTemplate)
             * objectService.getEnterpriseObjectById(templateId); if
             * (delTemplate == null) { throw new
             * EntityNotFoundException(EmailTemplate.class, templateId); }
             * updateService.deleteObjectAndRelationships(templateId);
             */
            updateService.deleteObjectDataAndRelationships(autoEmailId);

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    public void checkAutoEmail(AutoresponderEmail temp) throws IncompleteDataException, EntityExistsException {
        if (temp.getSUBJECT() == null || temp.getSUBJECT().isEmpty()) {
            throw new IncompleteDataException("Subject cannot be null.");
        }
        // Check if the autoEmail subject already exist for the type
        List<? extends AutoresponderEmail> autoEmails = getAutoEmailsBySubjectAndType(temp.getSUBJECT(), temp.type());
        if (autoEmails != null
                && !autoEmails.isEmpty()
                && !autoEmails.contains(temp)) {
            throw new EntityExistsException("Please choose a different email subject");
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
     * Removes
     *
     * @param listId
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeAllAssignedConfirmationEmailFromList(long listId) {
        try {
            List<Assign_AutoConfirmEmail_List> existingAssignments = objectService.getRelationshipsForTargetObject(listId, Assign_AutoConfirmEmail_List.class);

            List<Assign_AutoConfirmEmail_List> modListCopy = new ArrayList<>(existingAssignments);
            for (Assign_AutoConfirmEmail_List assign : modListCopy) {
                updateService.getEm().remove(
                        updateService.getEm().contains(assign)
                                ? assign : updateService.getEm().merge(assign));
            }

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeAllAssignedWelcomeEmailFromList(long listId) {
        try {
            List<Assign_AutoWelcomeEmail_List> existingAssignments = objectService.getRelationshipsForTargetObject(listId, Assign_AutoWelcomeEmail_List.class);

            List<Assign_AutoWelcomeEmail_List> modListCopy = new ArrayList<>(existingAssignments);
            for (Assign_AutoWelcomeEmail_List assign : modListCopy) {
                updateService.getEm().remove(
                        updateService.getEm().contains(assign)
                                ? assign : updateService.getEm().merge(assign));
            }

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
            //Cannot be 1 as 1 is always Email
            validateListField(newField);

            //Reorder the SNO for the entire list
            //If the new field added has the same SNO as an existing field, then insert it infront of the existing field
            List<SubscriptionListField> existingFields = getFieldsForSubscriptionList(listId); //All managed?
            Collections.sort(existingFields, new SubscriptionListFieldComparator()); //Doesn't include new field yet
            for (int i = existingFields.size(); i > 0; i--) {
                SubscriptionListField field = existingFields.get(i - 1);
                /*if(newField.getSNO() == field.getSNO()){
                 field.setSNO(i+1);
                 updateService.getEm().merge(field); //Assuming the entity is already managed
                 }
                 else if(newField.getSNO() < field.getSNO()){
                 field.setSNO(i+1);
                 updateService.getEm().merge(field); //Assuming the entity is already managed
                 }
                 else {
                 field.setSNO(i);
                 updateService.getEm().merge(field); //Assuming the entity is already managed
                 }*/
                //
                if (newField.getSNO() <= field.getSNO()) {
                    field.setSNO(i + 1);
                } else {
                    field.setSNO(i);
                }
                updateService.getEm().merge(field); //Assuming the entity is already managed
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
    public Map<SubscriberAccount,Map<SubscriptionListField,SubscriberFieldValue>> getSubscriberFieldValues(long listId, int startIndex, int limit){
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
            
            Map<SubscriberAccount,Map<SubscriptionListField,SubscriberFieldValue>> resultMap = 
                    new HashMap<>();
            
            //Collections.sort(results);//Sorting before creating the map may not be the most correct solution but it's the most efficient and effective one at the moment
            for(SubscriberFieldValue field : results){
                SubscriberAccount subscriber = field.getOWNER();
                if(!resultMap.containsKey(subscriber)){
                    resultMap.put(subscriber, new HashMap<SubscriptionListField,SubscriberFieldValue>());
                    
                }
                resultMap.get(subscriber).put(field.getFIELD(), field);
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
    public List<SubscriberFieldValue> constructSubscriberFieldValues(List<SubscriptionListField> fields){
        List<SubscriberFieldValue> values = new ArrayList<>();
        
        if(fields == null)
            return values;
        
        List<SubscriptionListField> sortedFields = new ArrayList<>(fields);
        Collections.sort(sortedFields);
        
        for (int i=0; i<fields.size(); i++){
            SubscriptionListField field = fields.get(i);
            SubscriberFieldValue newValue = new SubscriberFieldValue();
            newValue.setFIELD(field);
            newValue.setSNO(i);
            values.add(newValue);
        }
        
        return values;
    }
}
