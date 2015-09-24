/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription;

import eds.component.GenericObjectService;
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
import eds.entity.client.Client_;
import eds.component.config.GenericConfigService;
import eds.entity.config.ConfigNotFoundException;
import eds.entity.mail.Email;
import eds.entity.resource.SystemResourceAssignment;
import segmail.entity.subscription.ClientListAssignment;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberAccount_;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionList_;
import segmail.entity.subscription.connection.SMTPConnectionSES;
import segmail.entity.subscription.email.EmailTemplate;
import segmail.entity.subscription.email.EmailTemplate.EMAIL_TYPE;
import segmail.entity.subscription.email.EmailTemplate_;
import segmail.entity.subscription.email.TemplateListAssignment;
import segmail.entity.subscription.email.TemplateClientAssignment;
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
import org.joda.time.DateTime;
import segmail.entity.subscription.ListType;
import segmail.entity.subscription.ListType_;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
@Interceptors({ClientResourceInterceptor.class})
public class SubscriptionService {

    @PersistenceContext(name = "HIBERNATE")
    private EntityManager em;

    @EJB
    private GenericObjectService objectService;
    @EJB
    private GenericConfigService configService;
    @EJB
    private MailService mailService;
    @EJB
    private UserService userService;

    /**
     * [2015.07.12] Because the EJB Interceptor way failed, so this is a very
     * good alternative to omit clientid input for every method call.
     *
     */
    @Inject
    ClientFacade clientFacade;

    @PostConstruct
    public void init() {
        //setupListTypes();
    }

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
    public SubscriptionList addList(String listname, String listTypeName)
            throws IncompleteDataException, ConfigNotFoundException {
        try {
            if (listname == null || listname.isEmpty()) {
                throw new IncompleteDataException("List name cannot be empty.");
            }

            ListType listType = this.getListType(listTypeName);
            if (listType == null) {
                throw new ConfigNotFoundException(ListType.class, listTypeName);
            }

            //1. Create the list object and persist it first
            SubscriptionList newList = new SubscriptionList();
            newList.setLIST_NAME(listname); //Right now we don't keep history
            //newList.setLOCATION(remote ? SubscriptionList.LOCATION.REMOTE : SubscriptionList.LOCATION.LOCAL);
            newList.setType(listType);

            em.persist(newList);

            //2. Create the assignment to the client object
            //Client client = this.objectService.getEnterpriseObjectById(clientid, Client.class);
            Client client = clientFacade.getClient();
            if (client == null) {
                throw new EnterpriseObjectNotFoundException(Client.class);
            }
            //Test at this point whethe the newList object still gets persisted

            ClientListAssignment listAssignment = new ClientListAssignment();
            listAssignment.setSOURCE(client);
            listAssignment.setTARGET(newList);

            em.persist(listAssignment);

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
            long count = this.objectService.countRelationshipsForTarget(clientid, ClientListAssignment.class);
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
                    = this.objectService.getAllTargetObjectsFromSource(clientid, ClientListAssignment.class, SubscriptionList.class);

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
     * Subscribes a new subscriber to an existing list
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

            // Get confirmation email template
            // Assume that there is only 1
            List<EmailTemplate> templates = this.getTemplateForList(listId, EMAIL_TYPE.CONFIRMATION);
            if (templates == null || templates.isEmpty()) {
                throw new SubscriptionException("Cannot find any confimation email templates for list " + list.getLIST_NAME());
            }
            EmailTemplate template = templates.get(0);

            Email email = template.generateEmail();
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
     * Check if an email is already subscribed to a list
     *
     * @param email
     * @param listId
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean checkSubscribed(String email, long listId) {
        try {
            //Retrieving Subscriptions which has a SubscriberAccount email and a SubscriptionList ID
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Subscription> criteria = builder.createQuery(Subscription.class);
            Root<Subscription> source = criteria.from(Subscription.class);

            Join<Subscription, SubscriberAccount> subAcc = source.join("SOURCE");
            Join<Subscription, SubscriptionList> subList = source.join("TARGET");
            List<Predicate> conditions = new ArrayList();

            conditions.add(builder.equal(subAcc.get(SubscriberAccount_.EMAIL), email));
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

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<EmailTemplate> getTemplateForList(long listid, EMAIL_TYPE type) {
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(EmailTemplate.class);
            Root<TemplateListAssignment> sourceEntity = query.from(TemplateListAssignment.class);

            Join<TemplateListAssignment, EmailTemplate> template = sourceEntity.join("SOURCE");
            Join<TemplateListAssignment, SubscriptionList> list = sourceEntity.join("TARGET");

            query.select(template);

            List<Predicate> conditions = new ArrayList();

            if (type != null) {
                conditions.add(builder.equal(template.get(EmailTemplate_.TYPE), type));
            }
            conditions.add(builder.equal(list.get(SubscriptionList_.OBJECTID), listid));

            query.where(conditions.toArray(new Predicate[]{}));

            List<EmailTemplate> results = em.createQuery(query)
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

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<EmailTemplate> getAvailableTemplatesForClient(long clientid, EMAIL_TYPE type) {
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(EmailTemplate.class);
            Root<TemplateClientAssignment> sourceEntity = query.from(TemplateClientAssignment.class);

            Join<TemplateClientAssignment, EmailTemplate> template = sourceEntity.join("SOURCE");
            Join<TemplateClientAssignment, Client> client = sourceEntity.join("TARGET");

            query.select(template);

            List<Predicate> conditions = new ArrayList();

            if (type != null) {
                conditions.add(builder.equal(template.get(EmailTemplate_.TYPE), type));
            }
            conditions.add(builder.equal(client.get(Client_.OBJECTID), clientid));

            query.where(conditions.toArray(new Predicate[]{}));

            List<EmailTemplate> results = em.createQuery(query)
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

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public EmailTemplate addTemplateWithoutAssignment(String subject, String body, EMAIL_TYPE type)
            throws EntityExistsException, IncompleteDataException {
        try {
            /*if (subject == null || subject.isEmpty()) {
             throw new IncompleteDataException("Subject cannot be null.");
             }
             // Check if the template subject already exist for the type
             if (getTemplatesBySubjectAndType(subject, type)) {
             throw new EntityExistsException("Please choose a different email subject");
             }*/

            EmailTemplate newTemplate = new EmailTemplate();
            newTemplate.setBODY(body);
            newTemplate.setSUBJECT(subject);
            newTemplate.setTYPE(type);

            this.checkEmailTemplate(newTemplate);

            em.persist(newTemplate);

            return newTemplate;

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } /*catch (Exception ex) { 
         throw new EJBException(ex); // Stupid idea
         }*/

    }

    /**
     * Check if
     *
     * @param subject
     * @param type
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<EmailTemplate> getTemplatesBySubjectAndType(String subject, EMAIL_TYPE type) {
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<EmailTemplate> query = builder.createQuery(EmailTemplate.class);
            Root<EmailTemplate> sourceEntity = query.from(EmailTemplate.class);

            query.where(builder.and(
                    builder.equal(sourceEntity.get(EmailTemplate_.SUBJECT), subject),
                    builder.equal(sourceEntity.get(EmailTemplate_.TYPE), type)
            ));

            List<EmailTemplate> results = em.createQuery(query)
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

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void assignEmailTemplateToClient(long emailTemplateId, long clientId)
            throws EntityNotFoundException, RelationshipExistsException {
        try {
            List<TemplateClientAssignment> assignments = objectService.getRelationshipsForObject(emailTemplateId, clientId, TemplateClientAssignment.class);
            if (assignments != null && !assignments.isEmpty()) {
                throw new RelationshipExistsException(assignments.get(0));
            }

            EmailTemplate template = this.objectService.getEnterpriseObjectById(emailTemplateId, EmailTemplate.class);
            if (template == null) {
                throw new EntityNotFoundException("Template id " + emailTemplateId + " not found!");
            }

            Client client = this.objectService.getEnterpriseObjectById(clientId, Client.class);
            if (client == null) {
                throw new EntityNotFoundException("Client id " + client + " not found!");
            }

            TemplateClientAssignment assignment = new TemplateClientAssignment();
            assignment.setSOURCE(template);
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

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TemplateListAssignment assignConfirmationEmailToList(long confirmationEmailId, long listId) throws EntityNotFoundException {
        try {
            //Retrieve both objects and check they both exists
            EmailTemplate confirmationEmail = objectService.getEnterpriseObjectById(confirmationEmailId, EmailTemplate.class);
            if (confirmationEmail == null) {
                throw new EntityNotFoundException(EmailTemplate.class, confirmationEmailId);
            }

            if (!EmailTemplate.EMAIL_TYPE.CONFIRMATION.equals(confirmationEmail.getTYPE())) {
                throw new RuntimeException("EmailTemplate (id=" + confirmationEmail.getOBJECTID() + ") is not a confirmation email template.");
            }

            SubscriptionList list = objectService.getEnterpriseObjectById(listId, SubscriptionList.class);
            if (list == null) {
                throw new EntityNotFoundException(SubscriptionList.class, listId);
            }

            //Check if there is already an assignment, if yes, delete it as this is a 1-to-many (email to list) relationship
            List<TemplateListAssignment> existingAssignemnts = objectService.getRelationshipsForTargetObject(listId, TemplateListAssignment.class);
            if (existingAssignemnts != null && !existingAssignemnts.isEmpty()) {
                em.remove(existingAssignemnts);
            }

            TemplateListAssignment newAssignment = new TemplateListAssignment();
            newAssignment.setSOURCE(confirmationEmail);
            newAssignment.setTARGET(list);

            em.persist(newAssignment);

            return newAssignment;

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public EmailTemplate addTemplate(String subject, String body, EMAIL_TYPE type, long clientid)
            throws EntityExistsException, IncompleteDataException, RelationshipExistsException {
        try {
            //Create the new template first
            EmailTemplate newTemplate = this.addTemplateWithoutAssignment(subject, body, type);

            //Get the client
            Client client = clientFacade.getClient();
            if (client == null) {
                throw new EnterpriseObjectNotFoundException(Client.class);
            }
            //Assign it to the client
            this.assignEmailTemplateToClient(newTemplate.getOBJECTID(), client.getOBJECTID());

            return newTemplate;

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } /*catch (Exception ex) {
         throw new EJBException(ex);
         }*/ catch (EntityNotFoundException ex) {
            throw new RuntimeException(ex); // Something is very wrong here!
        }

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public EmailTemplate saveTemplate(EmailTemplate template)
            throws IncompleteDataException, EntityExistsException {
        try {
            checkEmailTemplate(template);

            return em.merge(template);

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteTemplate(long templateId) throws EntityNotFoundException {
        try {
            EmailTemplate delTemplate = objectService.getEnterpriseObjectById(templateId, EmailTemplate.class);
            if (delTemplate == null) {
                throw new EntityNotFoundException(EmailTemplate.class, templateId);
            }

            em.remove(delTemplate);

        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    public void checkEmailTemplate(EmailTemplate temp) throws IncompleteDataException, EntityExistsException {
        if (temp.getSUBJECT() == null || temp.getSUBJECT().isEmpty()) {
            throw new IncompleteDataException("Subject cannot be null.");
        }
        // Check if the template subject already exist for the type
        List<EmailTemplate> existingTemplates = getTemplatesBySubjectAndType(temp.getSUBJECT(), temp.getTYPE());
        if (existingTemplates != null
                && !existingTemplates.isEmpty()
                && !existingTemplates.contains(temp)) {
            throw new EntityExistsException("Please choose a different email subject");
        }
    }

}
