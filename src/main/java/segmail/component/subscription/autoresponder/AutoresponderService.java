/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription.autoresponder;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.client.ClientFacade;
import eds.component.data.DBConnectionException;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import eds.entity.client.Client;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.exception.GenericJDBCException;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.autoresponder.AUTO_EMAIL_TYPE;
import segmail.entity.subscription.autoresponder.Assign_AutoresponderEmail_Client;
import segmail.entity.subscription.autoresponder.Assign_AutoresponderEmail_Client_;
import segmail.entity.subscription.autoresponder.Assign_AutoresponderEmail_List;
import segmail.entity.subscription.autoresponder.Assign_AutoresponderEmail_List_;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;
import segmail.entity.subscription.autoresponder.AutoresponderEmail_;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class AutoresponderService {

    @EJB
    private GenericObjectService objectService;
    @EJB
    private UpdateObjectService updateService;

    @Inject
    ClientFacade clientFacade;

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
    public AutoresponderEmail createAutoEmailWithoutAssignment(String subject, String body, AUTO_EMAIL_TYPE type)
            throws EntityExistsException, IncompleteDataException {
        try {
            AutoresponderEmail newAutoEmail = new AutoresponderEmail();//AutoEmailTypeFactory.getAutoEmailTypeInstance(type);
            newAutoEmail.setTYPE(type);
            newAutoEmail.setBODY(body);
            newAutoEmail.setSUBJECT(subject);

            //validate first
            checkAutoEmail(newAutoEmail, clientFacade.getClient().getOBJECTID());

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
     * Client method - saves the email
     *
     * @param autoEmail
     * @return
     * @throws IncompleteDataException
     * @throws EntityExistsException
     * @throws eds.component.data.EntityNotFoundException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AutoresponderEmail saveAutoEmail(AutoresponderEmail autoEmail)
            throws IncompleteDataException, EntityExistsException, EntityNotFoundException {
        try {
            //Check if autoEmail exists in the first place
            if (!objectService.checkEntityExists(autoEmail)) {
                throw new EntityNotFoundException(AutoresponderEmail.class, autoEmail.getOBJECTID());
            }

            //Various fields for a particular client
            checkAutoEmail(autoEmail, clientFacade.getClient().getOBJECTID());

            return updateService.getEm().merge(autoEmail);

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
     * @return
     * @throws EntityNotFoundException
     * @throws RelationshipExistsException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Assign_AutoresponderEmail_Client assignAutoEmailToClient(long autoEmailId, long clientId) throws EntityNotFoundException, RelationshipExistsException {
        try {
            //Check if assignment already exists
            List<Assign_AutoresponderEmail_Client> assignments = objectService.getRelationshipsForObject(autoEmailId, clientId, Assign_AutoresponderEmail_Client.class);
            if (assignments != null && !assignments.isEmpty()) {
                throw new RelationshipExistsException(assignments.get(0));
            }

            //Check if autoEmail exists
            AutoresponderEmail autoEmail = objectService.getEnterpriseObjectById(autoEmailId, AutoresponderEmail.class);
            if (autoEmail == null) {
                throw new EntityNotFoundException("Autoresponder email id " + autoEmailId + " not found!");
            }

            //Check if client exists
            Client client = objectService.getEnterpriseObjectById(clientId, Client.class);
            if (client == null) {
                throw new EntityNotFoundException("Client id " + client + " not found!");
            }

            //Assign
            Assign_AutoresponderEmail_Client assignment = new Assign_AutoresponderEmail_Client();
            assignment.setSOURCE(autoEmail);
            assignment.setTARGET(client);
            objectService.getEm().persist(assignment);

            return assignment;
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        }
    }

    /**
     * Assigns the AutoresponderEmail to a SubscriptionList.
     *
     * @param autoEmailId
     * @param listId
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Assign_AutoresponderEmail_List assignAutoEmailToList(long autoEmailId, long listId) throws RelationshipExistsException, EntityNotFoundException {
        //Check if assignment already exists
        List<Assign_AutoresponderEmail_List> assignments = objectService.getRelationshipsForObject(autoEmailId, listId, Assign_AutoresponderEmail_List.class);
        if (assignments != null && !assignments.isEmpty()) {
            throw new RelationshipExistsException(assignments.get(0));
        }

        //Check if autoEmail exists
        AutoresponderEmail autoEmail = objectService.getEnterpriseObjectById(autoEmailId, AutoresponderEmail.class);
        if (autoEmail == null) {
            throw new EntityNotFoundException("Autoresponder email id " + autoEmailId + " not found!");
        }

        //Check if client exists
        SubscriptionList list = objectService.getEnterpriseObjectById(listId, SubscriptionList.class);
        if (list == null) {
            throw new EntityNotFoundException("List id " + listId + " not found!");
        }

        //Assign
        Assign_AutoresponderEmail_List assignment = new Assign_AutoresponderEmail_List();
        assignment.setSOURCE(autoEmail);
        assignment.setTARGET(list);
        objectService.getEm().persist(assignment);

        return assignment;
    }

    /**
     * Checks if the autoresponder exists by using: - Type - Subject - Client
     *
     * This is private because it requires passing in a clientId. If you access
     * this method from another application, you could easily try all possible
     * clientIds to find out which clientIds exists and use it to call other
     * methods to learn about other clients. Therefore, any methods that require
     * client ID will not be made public, even to Programs/Forms.
     *
     * @param temp
     * @throws IncompleteDataException
     * @throws EntityExistsException
     */
    private void checkAutoEmail(AutoresponderEmail temp, long clientId)
            throws IncompleteDataException, EntityExistsException {

        if (temp.getSUBJECT() == null || temp.getSUBJECT().isEmpty()) {
            throw new IncompleteDataException("Autoresponder emails must have a subject.");
        }

        if (temp.getTYPE() == null || temp.getTYPE().isEmpty()) {
            throw new IncompleteDataException("Autoresponder emails must have a type.");
        }

        List<AutoresponderEmail> results = this.getAutoEmailsBySubjectAndTypeForClient(temp.getSUBJECT(), AUTO_EMAIL_TYPE.getType(temp.getTYPE()), clientId);

        if (results != null && !results.isEmpty()) {
            //Must check if the retrieved email is the same one as temp because update methods will 
            //also use this method to check. New emails will not have any ID and therefore hit the 
            //exception
            for (AutoresponderEmail result : results) {
                if (!result.equals(temp)) {
                    throw new EntityExistsException(temp);
                }
            }
        }
    }

    /**
     * Removes
     *
     * @param listId
     */
    public void removeAllAssignedConfirmationEmailFromList(long listId) {
        try {
            //List<Assign_AutoConfirmEmail_List> existingAssignments = objectService.getRelationshipsForTargetObject(listId, Assign_AutoConfirmEmail_List.class);
            List<Assign_AutoresponderEmail_List> existingAssignments = this.getAssignedAutoEmailsAssignmentsForList(listId, AUTO_EMAIL_TYPE.CONFIRMATION);
            List<Assign_AutoresponderEmail_List> modListCopy = new ArrayList<>(existingAssignments);
            for (Assign_AutoresponderEmail_List assign : modListCopy) {
                updateService.getEm().remove(updateService.getEm().contains(assign) ? assign : updateService.getEm().merge(assign));
            }
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    
    public void removeAllAssignedWelcomeEmailFromList(long listId) {
        try {
            List<Assign_AutoresponderEmail_List> existingAssignments = this.getAssignedAutoEmailsAssignmentsForList(listId, AUTO_EMAIL_TYPE.WELCOME);
            List<Assign_AutoresponderEmail_List> modListCopy = new ArrayList<>(existingAssignments);
            for (Assign_AutoresponderEmail_List assign : modListCopy) {
                updateService.getEm().remove(updateService.getEm().contains(assign) ? assign : updateService.getEm().merge(assign));
            }
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
     * @param subject of type String
     * @param type of type AUTO_EMAIL_TYPE
     * @param clientId of type long
     * @return
     */
    public List<AutoresponderEmail> getAutoEmailsBySubjectAndTypeForClient(String subject, AUTO_EMAIL_TYPE type, long clientId) {
        try {
            CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
            CriteriaQuery<AutoresponderEmail> query = builder.createQuery(AutoresponderEmail.class);

            Root<AutoresponderEmail> fromAutoEmail = query.from(AutoresponderEmail.class);
            Root<Assign_AutoresponderEmail_Client> fromAssignAutoEmailClient = query.from(Assign_AutoresponderEmail_Client.class);

            query.select(fromAutoEmail);
            query.where(
                    builder.and(
                            builder.equal(fromAutoEmail.get(AutoresponderEmail_.SUBJECT), subject),
                            builder.equal(fromAutoEmail.get(AutoresponderEmail_.TYPE), type.name()),
                            builder.equal(fromAutoEmail.get(AutoresponderEmail_.OBJECTID), fromAssignAutoEmailClient.get(Assign_AutoresponderEmail_Client_.SOURCE)),
                            builder.equal(fromAssignAutoEmailClient.get(Assign_AutoresponderEmail_Client_.TARGET), clientId)
                    )
            );

            List<AutoresponderEmail> results = objectService.getEm().createQuery(query)
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
    public AutoresponderEmail createAndAssignAutoEmail(String subject, String body, AUTO_EMAIL_TYPE type)
            throws EntityExistsException, IncompleteDataException, RelationshipExistsException, EntityNotFoundException {
        try {
            AutoresponderEmail newAutoEmail = this.createAutoEmailWithoutAssignment(subject, body, type);
            Client client = clientFacade.getClient();
            if (client == null) {
                throw new IncompleteDataException("No client id provided.");
            }

            this.assignAutoEmailToClient(newAutoEmail.getOBJECTID(), client.getOBJECTID());

            return newAutoEmail;

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
    public void deleteAutoEmail(long autoEmailId) throws EntityNotFoundException {
        try {
            updateService.deleteObjectDataAndRelationships(autoEmailId);
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    public List<AutoresponderEmail> getAvailableAutoEmailsForClient(long clientId, AUTO_EMAIL_TYPE type) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<AutoresponderEmail> query = builder.createQuery(AutoresponderEmail.class);
        Root<AutoresponderEmail> fromAutoEmail = query.from(AutoresponderEmail.class);
        Root<Assign_AutoresponderEmail_Client> fromAssignAutoEmailClient = query.from(Assign_AutoresponderEmail_Client.class);

        query.select(fromAutoEmail);
        query.where(builder.and(
                builder.equal(fromAutoEmail.get(AutoresponderEmail_.TYPE), type.name()),
                builder.equal(fromAutoEmail.get(AutoresponderEmail_.OBJECTID), fromAssignAutoEmailClient.get(Assign_AutoresponderEmail_Client_.SOURCE)),
                builder.equal(fromAssignAutoEmailClient.get(Assign_AutoresponderEmail_Client_.TARGET), clientId)
        ));

        List<AutoresponderEmail> results = objectService.getEm().createQuery(query)
                .getResultList();
        return results;
    }

    /**
     * Retrieves all assigned AutoresponderEmails for a SubscriptionList using 
     * the Assign_AutoresponderEmail_List relationship.
     * 
     * @param listId
     * @param type
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<AutoresponderEmail> getAssignedAutoEmailsForList(long listId, AUTO_EMAIL_TYPE type) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<AutoresponderEmail> query = builder.createQuery(AutoresponderEmail.class);
        Root<AutoresponderEmail> fromAutoEmail = query.from(AutoresponderEmail.class);
        Root<Assign_AutoresponderEmail_List> fromAssignAutoEmailList = query.from(Assign_AutoresponderEmail_List.class);

        query.select(fromAutoEmail);
        query.where(builder.and(builder.equal(fromAutoEmail.get(AutoresponderEmail_.TYPE), type.name()),
                builder.equal(fromAutoEmail.get(AutoresponderEmail_.OBJECTID), fromAssignAutoEmailList.get(Assign_AutoresponderEmail_Client_.SOURCE)),
                builder.equal(fromAssignAutoEmailList.get(Assign_AutoresponderEmail_List_.TARGET), listId)
        ));

        List<AutoresponderEmail> results = objectService.getEm().createQuery(query)
                .getResultList();
        return results;
    }

    /**
     * Retrieves all assigned Assign_AutoresponderEmail_List relationship for a 
     * SubscriptionList.
     * 
     * @param listId
     * @param type
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Assign_AutoresponderEmail_List> getAssignedAutoEmailsAssignmentsForList(long listId, AUTO_EMAIL_TYPE type) {
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<Assign_AutoresponderEmail_List> query = builder.createQuery(Assign_AutoresponderEmail_List.class);
        Root<AutoresponderEmail> fromAutoEmail = query.from(AutoresponderEmail.class);
        Root<Assign_AutoresponderEmail_List> fromAssignAutoEmailList = query.from(Assign_AutoresponderEmail_List.class);

        query.select(fromAssignAutoEmailList);
        query.where(builder.and(builder.equal(fromAutoEmail.get(AutoresponderEmail_.TYPE), type.name()),
                builder.equal(fromAutoEmail.get(AutoresponderEmail_.OBJECTID), fromAssignAutoEmailList.get(Assign_AutoresponderEmail_Client_.SOURCE)),
                builder.equal(fromAssignAutoEmailList.get(Assign_AutoresponderEmail_List_.TARGET), listId)
        ));

        List<Assign_AutoresponderEmail_List> results = objectService.getEm().createQuery(query)
                .getResultList();
        return results;
    }
}
