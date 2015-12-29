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
import eds.component.mail.MailService;
import eds.component.user.UserService;
import eds.entity.client.Client;
import eds.component.config.GenericConfigService;
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
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
import segmail.entity.subscription.SubscriptionListFieldList;
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

    @PersistenceContext(name = "HIBERNATE")
    private EntityManager em;

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

                em.persist(remote);
                em.persist(local);
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
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<ListType> criteria = builder.createQuery(ListType.class);
            Root<ListType> sourceEntity = criteria.from(ListType.class);

            criteria.select(sourceEntity);
            criteria.where(builder.equal(sourceEntity.get(ListType_.VALUE), listtypevalue));

            List<ListType> results = em.createQuery(criteria)
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

            em.persist(newList);

            //2. Create the assignment to the client object
            Client client = clientFacade.getClient();
            if (client == null) {
                throw new EnterpriseObjectNotFoundException(Client.class);
            }
            //Test at this point whethe the newList object still gets persisted

            Assign_Client_List listAssignment = new Assign_Client_List();
            listAssignment.setSOURCE(client);
            listAssignment.setTARGET(newList);

            em.persist(listAssignment);
            
            //3. Create the default fieldsets and assign it to newList
            SubscriptionListFieldList fieldlist = new SubscriptionListFieldList();
            fieldlist.addField(new SubscriptionListField(1,"Email","Text","Email of your subscriber."));
            fieldlist.setOWNER(newList);
            
            em.persist(fieldlist);

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
     * Subscribes a new subscriber to an existing list. 
     *
     * @param newSub
     * @param listId
     * @param confirmation
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void subscribe(SubscriberAccount newSub, long listId, boolean confirmation) {
        try {
            //Validate the email address rules
            EmailValidator validator = EmailValidator.getInstance();
            if (!validator.isValid(newSub.getEMAIL())) {
                throw new SubscriptionException("Email address is not valid!");
            }

            //Find the list object
            SubscriptionList list = this.objectService.getEnterpriseObjectById(listId, SubscriptionList.class);
            if (list == null) {
                throw new SubscriptionException("List " + listId + " not found!");
            }

            if (this.checkSubscribed(newSub.getEMAIL(), listId)) {
                throw new SubscriptionException("Email is already subscribed to list.");
            }

            //Persist the new subscriber
            em.persist(newSub);

            //Create new subscription
            Subscription subsc = new Subscription();
            subsc.setSOURCE(newSub);
            subsc.setTARGET(list);
            subsc.setSTATUS(Subscription.STATUS.NEW);

            em.persist(subsc);

            // Send out the confimration email
            if (!confirmation) {
                return;
            }

            /*
            // Get confirmation email autoEmail
            // Assume that there is only 1
            List<EmailTemplate> templates = this.getAutoEmailForList(listId);
            if (templates == null || templates.isEmpty()) {
                throw new SubscriptionException("Cannot find any confimation email templates for list " + list.getLIST_NAME());
            }
            AutoresponderEmail autoEmail = templates.get(0);

            Email email = autoEmail.generateEmail();
            //email.setAUTHOR(list);
            email.addRecipient(newSub);

            // Get the SMTP connection settings from the List
            // Assume that there is only 1
            List<SMTPConnectionSES> smtps = this.objectService
                    .getAllSourceObjectsFromTarget(listId, SystemResourceAssignment.class, SMTPConnectionSES.class);

            // If the connection was not found, queue the message to be sent later
            if (smtps == null || smtps.isEmpty()) {
                email.schedule(new DateTime());
                em.persist(email);
                throw new SubscriptionException("Cannot find any SMTP connection set for list " + list.getLIST_NAME());
            }

            SMTPConnectionSES smtp = smtps.get(0);

            //mailService.sendEmail(email, smtp, true);
            */
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
     * Check if a subscriber subscriberEmail is already subscribed to a list
     *
     * @param subscriberEmail
     * @param listId
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean checkSubscribed(String subscriberEmail, long listId) {
        try {
            //Retrieving Subscriptions which has a SubscriberAccount subscriberEmail and a SubscriptionList ID
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Subscription> criteria = builder.createQuery(Subscription.class);
            Root<Subscription> source = criteria.from(Subscription.class);

            Join<Subscription, SubscriberAccount> subAcc = source.join("SOURCE");
            Join<Subscription, SubscriptionList> subList = source.join("TARGET");
            List<Predicate> conditions = new ArrayList();

            conditions.add(builder.equal(subAcc.get(SubscriberAccount_.EMAIL), subscriberEmail));
            conditions.add(builder.equal(subList.get(SubscriptionList_.OBJECTID), listId));

            criteria.where(conditions.toArray(new Predicate[]{}));

            List<Subscription> results = em.createQuery(criteria).getResultList();

            return !(results == null || results.isEmpty());

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
     * Get a list of all AutoresponderEmails assigned to a particular list.
     * @param listid
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<AutoresponderEmail> getAutoEmailForList(long listid) {
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(AutoresponderEmail.class);
            Root<Assign_AutoresponderEmail_List> sourceEntity = query.from(Assign_AutoresponderEmail_List.class);

            Join<Assign_AutoresponderEmail_List, AutoresponderEmail> autoEmail = sourceEntity.join("SOURCE");
            Join<Assign_AutoresponderEmail_List, SubscriptionList> list = sourceEntity.join("TARGET");

            query.select(autoEmail);

            List<Predicate> conditions = new ArrayList();

            conditions.add(builder.equal(list.get(SubscriptionList_.OBJECTID), listid));

            query.where(conditions.toArray(new Predicate[]{}));

            List<AutoresponderEmail> results = em.createQuery(query)
                    .getResultList();

            return results;

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
    public List<AutoConfirmEmail> getAvailableConfirmationEmailForClient(long clientid){
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
    public List<AutoWelcomeEmail> getAvailableWelcomeEmailForClient(long clientid){
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

            em.persist(newAutoEmail);

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
    public List<? extends AutoresponderEmail> getAutoEmailsBySubjectAndType(String subject,AutoEmailTypeFactory.TYPE type) {
        try {
            Class<? extends AutoresponderEmail> e = AutoEmailTypeFactory.getAutoEmailTypeClass(type);
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<? extends AutoresponderEmail> query = builder.createQuery(e);
            Root<? extends AutoresponderEmail> sourceEntity = query.from(e);

            query.where(builder.and(builder.equal(sourceEntity.get(AutoresponderEmail_.SUBJECT), subject)
                    //builder.equal(sourceEntity.get(AutoresponderEmail_.TYPE), type) we define type as a Entity class instead of an enum
            ));

            List<? extends AutoresponderEmail> results = em.createQuery(query)
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

            em.persist(assignment);

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
            em.persist(newAssignment);

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
            em.persist(newAssignment);

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
     * context. If no ClientFacade is available in context, then an IncompleteDataException
     * will be thrown.
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

            return em.merge(autoEmail);

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
            /**EmailTemplate delTemplate = (EmailTemplate) objectService.getEnterpriseObjectById(templateId);
            if (delTemplate == null) {
                throw new EntityNotFoundException(EmailTemplate.class, templateId);
            }
            updateService.deleteObjectAndRelationships(templateId);
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
     * @param list 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveList(SubscriptionList list){
        try {
            em.merge(list);
            
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
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteList(long listId) throws EntityNotFoundException{
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
     * @param listId 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeAllAssignedConfirmationEmailFromList(long listId){
        try {
            List<Assign_AutoConfirmEmail_List> existingAssignments = objectService.getRelationshipsForTargetObject(listId, Assign_AutoConfirmEmail_List.class);
            
            List<Assign_AutoConfirmEmail_List> modListCopy = new ArrayList<>(existingAssignments);
            for(Assign_AutoConfirmEmail_List assign:modListCopy){
                    em.remove(em.contains(assign) ? assign : em.merge(assign));
            }
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeAllAssignedWelcomeEmailFromList(long listId){
        try {
            List<Assign_AutoWelcomeEmail_List> existingAssignments = objectService.getRelationshipsForTargetObject(listId, Assign_AutoWelcomeEmail_List.class);
            
            List<Assign_AutoWelcomeEmail_List> modListCopy = new ArrayList<>(existingAssignments);
            for(Assign_AutoWelcomeEmail_List assign:modListCopy){
                    em.remove(em.contains(assign) ? assign : em.merge(assign));
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
    public SubscriptionListFieldList getFieldListForSubscriptionList(long listId){
        try {
            List<SubscriptionListFieldList> allFieldList = this.objectService.getEnterpriseData(listId, SubscriptionListFieldList.class);
            
            //we currently only accept 1 record for each SubscriptionList
            return (allFieldList == null || allFieldList.isEmpty()) ? null : allFieldList.get(0);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }
}
