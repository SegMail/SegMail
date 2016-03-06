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
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.exception.GenericJDBCException;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.email.Assign_AutoConfirmEmail_List;
import segmail.entity.subscription.email.Assign_AutoWelcomeEmail_List;
import segmail.entity.subscription.email.Assign_AutoresponderEmail_Client;
import segmail.entity.subscription.email.AutoConfirmEmail;
import segmail.entity.subscription.email.AutoEmailTypeFactory;
import segmail.entity.subscription.email.AutoWelcomeEmail;
import segmail.entity.subscription.email.AutoresponderEmail;
import segmail.entity.subscription.email.AutoresponderEmail_;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class AutoresponderService {

    @EJB private GenericObjectService objectService;
    @EJB private UpdateObjectService updateService;
    
    @Inject ClientFacade clientFacade;
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
     * Get all available Welcome emails assigned to a Client.
     *
     * @param clientid
     * @return
     */
    public List<AutoWelcomeEmail> getAvailableWelcomeEmailForClient(long clientid) {
        try {
            List<AutoWelcomeEmail> results = objectService.getAllSourceObjectsFromTarget(clientid, Assign_AutoresponderEmail_Client.class, AutoWelcomeEmail.class);
            return results;
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    public AutoresponderEmail saveAutoEmail(AutoresponderEmail autoEmail) throws IncompleteDataException, EntityExistsException {
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
     * Assigns a ConfirmationEmail to a Subscriptionlist
     *
     * @param confirmationEmailId
     * @param listId
     * @return
     * @throws EntityNotFoundException
     */
    public Assign_AutoConfirmEmail_List assignConfirmationEmailToList(long confirmationEmailId, long listId) throws EntityNotFoundException {
        try {
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
            updateService.deleteRelationshipByTarget(listId, Assign_AutoConfirmEmail_List.class);
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
     * Assigns an AutoresponderEmail to a Client.
     *
     * @param autoEmailId
     * @param clientId
     * @throws EntityNotFoundException
     * @throws RelationshipExistsException
     */
    public void assignAutoEmailToClient(long autoEmailId, long clientId) throws EntityNotFoundException, RelationshipExistsException {
        try {
            List<Assign_AutoresponderEmail_Client> assignments = objectService.getRelationshipsForObject(autoEmailId, clientId, Assign_AutoresponderEmail_Client.class);
            if (assignments != null && !assignments.isEmpty()) {
                throw new RelationshipExistsException(assignments.get(0));
            }
            AutoresponderEmail autoEmail = objectService.getEnterpriseObjectById(autoEmailId, AutoresponderEmail.class);
            if (autoEmail == null) {
                throw new EntityNotFoundException("Autoresponder email id " + autoEmailId + " not found!");
            }
            Client client = objectService.getEnterpriseObjectById(clientId, Client.class);
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
        }
    }

    public void checkAutoEmail(AutoresponderEmail temp) throws IncompleteDataException, EntityExistsException {
        if (temp.getSUBJECT() == null || temp.getSUBJECT().isEmpty()) {
            throw new IncompleteDataException("Subject cannot be null.");
        }
        List<? extends AutoresponderEmail> autoEmails = getAutoEmailsBySubjectAndType(temp.getSUBJECT(), temp.type());
        if (autoEmails != null && !autoEmails.isEmpty() && !autoEmails.contains(temp)) {
            throw new EntityExistsException("Please choose a different email subject");
        }
    }

    /**
     * Removes
     *
     * @param listId
     */
    public void removeAllAssignedConfirmationEmailFromList(long listId) {
        try {
            List<Assign_AutoConfirmEmail_List> existingAssignments = objectService.getRelationshipsForTargetObject(listId, Assign_AutoConfirmEmail_List.class);
            List<Assign_AutoConfirmEmail_List> modListCopy = new ArrayList<>(existingAssignments);
            for (Assign_AutoConfirmEmail_List assign : modListCopy) {
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
     * Get all available Confirmation emails assigned to a Client.
     *
     * @param clientid
     * @return
     */
    public List<AutoConfirmEmail> getAvailableConfirmationEmailForClient(long clientid) {
        try {
            List<AutoConfirmEmail> results = objectService.getAllSourceObjectsFromTarget(clientid, Assign_AutoresponderEmail_Client.class, AutoConfirmEmail.class);
            return results;
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    public void removeAllAssignedWelcomeEmailFromList(long listId) {
        try {
            List<Assign_AutoWelcomeEmail_List> existingAssignments = objectService.getRelationshipsForTargetObject(listId, Assign_AutoWelcomeEmail_List.class);
            List<Assign_AutoWelcomeEmail_List> modListCopy = new ArrayList<>(existingAssignments);
            for (Assign_AutoWelcomeEmail_List assign : modListCopy) {
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
     * @param subject
     * @param type
     * @return
     */
    public List<? extends AutoresponderEmail> getAutoEmailsBySubjectAndType(String subject, AutoEmailTypeFactory.TYPE type) {
        try {
            Class<? extends AutoresponderEmail> e = AutoEmailTypeFactory.getAutoEmailTypeClass(type);
            CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
            CriteriaQuery<? extends AutoresponderEmail> query = builder.createQuery(e);
            Root<? extends AutoresponderEmail> sourceEntity = query.from(e);
            query.where(builder.and(builder.equal(sourceEntity.get(AutoresponderEmail_.SUBJECT), subject)));
            List<? extends AutoresponderEmail> results = objectService.getEm().createQuery(query).getResultList();
            return results;
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
    public Assign_AutoWelcomeEmail_List assignWelcomeEmailToList(long welcomeEmailId, long listId) throws EntityNotFoundException {
        try {
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
    public AutoresponderEmail createAndAssignAutoEmail(String subject, String body, AutoEmailTypeFactory.TYPE type) throws EntityExistsException, IncompleteDataException, RelationshipExistsException, EntityNotFoundException {
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
    
}
