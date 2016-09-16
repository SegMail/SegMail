/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.batch.BatchProcessingService;
import eds.component.config.GenericConfigService;
import eds.component.data.DBConnectionException;
import eds.component.data.DataValidationException;
import eds.component.data.EnterpriseObjectNotFoundException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.encryption.EncryptionUtility;
import eds.entity.client.Client;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.commons.validator.routines.UrlValidator;
import org.hibernate.exception.GenericJDBCException;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.Assign_Client_List;
import segmail.entity.subscription.FIELD_TYPE;
import segmail.entity.subscription.ListType;
import segmail.entity.subscription.ListType_;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.SubscriptionListFieldComparator;
import segmail.entity.subscription.SubscriptionListField_;
import segmail.entity.subscription.autoresponder.Assign_AutoresponderEmail_List;
import segmail.entity.subscription.autoresponder.Assign_AutoresponderEmail_List_;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class ListService {

    /**
     * Generic services
     */
    @EJB
    private GenericObjectService objectService;
    @EJB
    private UpdateObjectService updateService;
    @EJB
    private GenericConfigService configService;

    public void validateListField(SubscriptionListField field) throws DataValidationException, IncompleteDataException {
        if (field.getSNO() == 1 && !field.getTYPE().equals(FIELD_TYPE.EMAIL.name()) && !field.getFIELD_NAME().equalsIgnoreCase(FIELD_TYPE.EMAIL.name())) {
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
    public SubscriptionListField addFieldForSubscriptionList(long listId, SubscriptionListField newField) throws EntityNotFoundException, DataValidationException, IncompleteDataException {
        try {
            SubscriptionList list = objectService.getEnterpriseObjectById(listId, SubscriptionList.class);
            if (list == null) {
                throw new EntityNotFoundException(SubscriptionList.class, listId);
            }
            validateListField(newField);
            List<SubscriptionListField> existingFields = getFieldsForSubscriptionList(listId);
            List<SubscriptionListField> insertThese = new ArrayList<>();
            List<SubscriptionListField> deleteThese = new ArrayList<>();
            existingFields.add(newField);
            for (int i = 1; i <= existingFields.size(); i++) {
                SubscriptionListField field = existingFields.get(i - 1);
                if (field.equals(newField)) {
                    field.setOWNER(list);
                    field.setSNO(i);
                    insertThese.add(field);
                    continue;
                }
                if (field.getSNO() != i) {
                    deleteThese.add(field);
                    SubscriptionListField cloneField = new SubscriptionListField(list, i, field.isMANDATORY(), field.getFIELD_NAME(), FIELD_TYPE.valueOf(field.getTYPE()), field.getDESCRIPTION());
                    insertThese.add(cloneField);
                }
            }
            for (SubscriptionListField deleteField : deleteThese) {
                updateService.getEm().remove(updateService.getEm().contains(deleteField) ? deleteField : updateService.getEm().merge(deleteField));
            }
            for (SubscriptionListField insertField : insertThese) {
                updateService.getEm().persist(insertField);
            }
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
    public void fullRefreshUpdateSubscriptionListFields(List<SubscriptionListField> fieldList) 
            throws DataValidationException, IncompleteDataException {
        if(fieldList == null || fieldList.isEmpty())
            return;
        
        long listId = fieldList.get(0).getOWNER().getOBJECTID();
        
        //If any of the fields have duplicated FIELD_NAME, do not proceed
        
        //Delete all existing fields first
        updateService.deleteAllEnterpriseDataByType(listId, SubscriptionListField.class);
        
        //Sort and fill in SNO
        Collections.sort(fieldList,new SubscriptionListFieldComparator());
        for(int i = 0; i< fieldList.size(); i++) {
            SubscriptionListField field = fieldList.get(i);
            field.setSNO(i+1);
            validateListField(field); //Throws exception if something is wrong and rolls back the whole shit
            
            updateService.getEm().persist(field);
        }
    }

    /**
     * Deletes all the following relationships first:
     * - Assign_Client_List
     * - Subscription
     * 
     * Then proceeds to delete the SubscriptionList itself. It does not delete
     * the subscribers under this list.
     *
     * Potentially long running operation that requires the background job
     * scheduling mechanism.
     *
     * @param listId
     * @throws eds.component.data.EntityNotFoundException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteList(long listId, long clientId) throws EntityNotFoundException {
        int assignClientList = updateService.deleteRelationshipByTarget(listId, Assign_Client_List.class);
        
        //Asynchronous call, but no need for async actually, just an experiment
        deleteSubscriptions(listId);
        //Delete fields
        updateService.deleteAllEnterpriseDataByType(listId, SubscriptionListField.class);
        
        //Finally remove the list entity
        EntityManager em = updateService.getEm();
        em.remove(em.find(SubscriptionList.class, listId));
    }
    
    /**
     * For experimenting. Asynchronous is not really needed after all since the 
     * underlying SQL executes relatively fast.
     * 
     * @param listId 
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteSubscriptions(long listId) {
        updateService.deleteRelationshipByTarget(listId, Subscription.class);
    }

    public SubscriptionList createList(String listname, boolean remote, long clientId) throws IncompleteDataException, EnterpriseObjectNotFoundException {
        try {
            if (listname == null || listname.isEmpty()) {
                throw new IncompleteDataException("List name cannot be empty.");
            }
            SubscriptionList newList = new SubscriptionList();
            newList.setLIST_NAME(listname);
            newList.setREMOTE(remote);
            updateService.getEm().persist(newList);
            Client client = objectService.getEnterpriseObjectById(clientId, Client.class);
            if (client == null) {
                throw new EnterpriseObjectNotFoundException(Client.class);
            }
            Assign_Client_List listAssignment = new Assign_Client_List();
            listAssignment.setSOURCE(client);
            listAssignment.setTARGET(newList);
            updateService.getEm().persist(listAssignment);
            SubscriptionListField fieldEmail = new SubscriptionListField(newList, 1, true, SubscriptionService.DEFAULT_EMAIL_FIELD_NAME, FIELD_TYPE.EMAIL, "Email of your subscriber.");
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

    public ListType getListType(String listtypevalue) {
        try {
            CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
            CriteriaQuery<ListType> criteria = builder.createQuery(ListType.class);
            Root<ListType> sourceEntity = criteria.from(ListType.class);
            criteria.select(sourceEntity);
            criteria.where(builder.equal(sourceEntity.get(ListType_.VALUE), listtypevalue));
            List<ListType> results = objectService.getEm().createQuery(criteria).getResultList();
            return results.get(0);
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    /**
     * Experimental logic for EnterpriseConfiguration testing Got to build a
     * setup EJB in the future!
     */
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

    /**
     * A simple, stateless update method that merges the entity and commits.
     * Potentially there could be a generic operation that updates the entity.
     *
     * @param list
     * @throws eds.component.data.DataValidationException if:
     * <ul>
     * <li>REDIRECT_CONFIRM or REDIRECT_WELCOME are invalid</li>
     * </ul>
     */
    public void saveList(SubscriptionList list) throws DataValidationException {

        boolean validURL = true;
        if(list.getREDIRECT_CONFIRM() != null && !list.getREDIRECT_CONFIRM().isEmpty()) {
            if(UrlValidator.getInstance().isValid(list.getREDIRECT_CONFIRM()))
                throw new DataValidationException("Redirect URL "+list.getREDIRECT_CONFIRM() +" is invalid.");
        }
        if(list.getREDIRECT_WELCOME()!= null && !list.getREDIRECT_WELCOME().isEmpty()) {
            if(UrlValidator.getInstance().isValid(list.getREDIRECT_WELCOME()))
                throw new DataValidationException("Redirect URL "+list.getREDIRECT_WELCOME() +" is invalid.");
        }
        updateService.getEm().merge(list);

    }

    /**
     * Checks of a particular client has no lists created. Used in the setup
     * page.
     *
     * @param clientid
     * @return
     */
    public boolean hasNoList(long clientid) {
        try {
            long count = objectService.countRelationshipsForTarget(clientid, Assign_Client_List.class);
            return count <= 0;
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
     *
     * @param listId
     * @return SubscriptionListFieldList if there is at least 1 record available
     */
    public List<SubscriptionListField> getFieldsForSubscriptionList(long listId) {
        
        List<SubscriptionListField> allFieldList = objectService.getEnterpriseData(listId, SubscriptionListField.class);
        Collections.sort(allFieldList, new SubscriptionListFieldComparator());
        return allFieldList;
        
    }
    
    /**
     * Temporary until we build a full blown solution in MailmergeTag.
     * 
     * @param listId
     * @param mailmergeTag
     * @return 
     */
    public List<SubscriptionListField> getFieldsForSubscriptionList(long listId, String mailmergeTag) {
        
        CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
        CriteriaQuery<SubscriptionListField> query = builder.createQuery(SubscriptionListField.class);
        Root<SubscriptionListField> fromField = query.from(SubscriptionListField.class);
        
        query.select(fromField);
        query.where(
                builder.and(
                        builder.equal(fromField.get(SubscriptionListField_.OWNER), listId),
                        builder.equal(fromField.get(SubscriptionListField_.MAILMERGE_TAG), mailmergeTag)
        ));
        
        List<SubscriptionListField> results = objectService.getEm().createQuery(query)
                .getResultList();
        
        return results;
        
    }

    public List<String> getSubscriptionListFieldKeys(long listId) {
        List<SubscriptionListField> allFieldList = this.getFieldsForSubscriptionList(listId);
        List<String> results = new ArrayList<>();

        for (SubscriptionListField field : allFieldList) {
            results.add((String) field.generateKey());
        }

        return results;
    }

    public List<SubscriptionList> getAllListForClient(long clientid) {
        try {
            List<SubscriptionList> allList = objectService.getAllTargetObjectsFromSource(clientid, Assign_Client_List.class, SubscriptionList.class);
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

}
