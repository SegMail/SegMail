/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription.datasource;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipNotFoundException;
import eds.entity.client.Client;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.joda.time.DateTime;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.Assign_Client_List;
import segmail.entity.subscription.SUBSCRIPTION_STATUS;
import static segmail.entity.subscription.SUBSCRIPTION_STATUS.CONFIRMED;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.datasource.LAST_SYNC_RESULT;
import segmail.entity.subscription.datasource.ListDataMapping;
import segmail.entity.subscription.datasource.ListDatasource;
import segmail.entity.subscription.datasource.ListDatasource_;
import segmail.entity.subscription.datasource.synchronize.ListDatasourceObjectWrapper;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class DatasourceService {
    
    public static final long DEFAULT_SYNC_TIME_MS = 60*60*2*1000;
    public static final String SYNC_TIME_MS_OVERWRITE_KEY = "DatasourceService.SYNC_TIME_MS_OVERWRITE_KEY";
    
    final int SYNC_DS_BATCH_SIZE = 10;
    final int SYNC_REC_BATCH_SIZE = 1000;
    
    /**
     * This is the number of batches to sync for 1st time sync of a new list.
     */
    final int NUM_BATCHES_1ST = 100;
    
    /**
     * This is the number of batches to sync for subsequent sync of an existing list.
     */
    final int NUM_BATCHES_SUB = 10;
    
    @EJB GenericObjectService objService;
    @EJB UpdateObjectService updService;
    
    @EJB DatasourceQueryService dsQueryService;
    @EJB SubscriptionService subService;
    @EJB DatasourceServiceHelper helper;
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<ListDataMapping> refreshDataMappings(long listId, List<ListDataMapping> mappings) {
        updService.deleteAllEnterpriseDataByType(listId, ListDataMapping.class);
        
        for(ListDataMapping mapping : mappings) {
            updService.persist(mapping);
        }
        
        return mappings;
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void syncAllLists(){
        int next = 0;
        int index = 0;
        
        String systemSet = System.getProperty(SYNC_TIME_MS_OVERWRITE_KEY);
        long delay = DEFAULT_SYNC_TIME_MS;
        if(systemSet != null && !systemSet.isEmpty()) {
            delay = Long.parseLong(systemSet);
        }
        
        do{
            List<ListDatasource> datasources = this.getNextNDatasources(
                    true, true, SYNC_DS_BATCH_SIZE*index++, SYNC_DS_BATCH_SIZE,DateTime.now(),delay); 
            next = datasources.size();

            for(ListDatasource datasource : datasources) {
                try {
                    this.syncList(datasource);
                } catch (Exception ex) {
                    Logger.getLogger(DatasourceService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }while(next > 0);
    }
    
    /**
     * Synchronizes the entire list.
     * 
     * @param datasource
     * 
     * @throws eds.component.data.EntityNotFoundException 
     * @throws eds.component.data.IncompleteDataException 
     * @throws java.sql.SQLException 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void syncList(ListDatasource datasource) {
        try {
            //Retrieve the necessary data
            //First check if a synchronization is required.
            if(datasource == null || !datasource.isACTIVE())
                return;
            
            //If not time yet, do not sync
            if(datasource.getLAST_SYNC() != null && !checkRunSync(
                    DateTime.now(),
                    new DateTime(datasource.getLAST_SYNC().getTime())))
                return;
            
            //Update sync status at the start so that if there are concurrent jobs, this datasource would not be picked up
            datasource.setLAST_SYNC(new java.sql.Timestamp(DateTime.now().getMillis()));
            datasource = (ListDatasource) updService.merge(datasource);
            
            long listId = datasource.getOWNER().getOBJECTID();
            SubscriptionList list = datasource.getOWNER();
            
            List<ListDataMapping> mappings = objService.getEnterpriseData(listId, ListDataMapping.class);
            if(mappings == null || mappings.isEmpty())
                throw new IncompleteDataException("No data mappings configured for list ID");
            
            //Start with the new subscribers
            syncNew(datasource, mappings, datasource.getNEXT_SYNC_NEW_INDEX(), SYNC_REC_BATCH_SIZE,
                    //If 1st sync for this list, use NUM_BATCHES_1ST. Else, NUM_BATCHES_SUB.
                    (datasource.getSYNC_NEW_CYCLES() == 0) ?
                            NUM_BATCHES_1ST : NUM_BATCHES_SUB);
            
            //Then the removed subscribers
            syncRemoved(datasource, mappings, datasource.getNEXT_SYNC_NEW_INDEX(), SYNC_REC_BATCH_SIZE,
                    //If 1st sync for this list, use NUM_BATCHES_1ST. Else, NUM_BATCHES_SUB.
                    (datasource.getSYNC_NEW_CYCLES() == 0) ?
                            NUM_BATCHES_1ST : NUM_BATCHES_SUB);
            
        } catch (SQLException ex) {
            Logger.getLogger(DatasourceService.class.getName()).log(Level.SEVERE, null, ex);
            //Update sync status
            datasource.setLAST_SYNC_MESSAGE(LAST_SYNC_RESULT.CONN_ERROR);
        } catch (IncompleteDataException ex) { 
            //Logger.getLogger(DatasourceService.class.getName()).log(Level.SEVERE, null, ex);//no need 
            //Update sync status
            datasource.setLAST_SYNC_MESSAGE(LAST_SYNC_RESULT.NO_MAPPING);
        } catch (EntityNotFoundException ex) {
            Logger.getLogger(DatasourceService.class.getName()).log(Level.SEVERE, null, ex);
            //Update sync status
            datasource.setLAST_SYNC_MESSAGE(LAST_SYNC_RESULT.NO_CLIENT);
        } finally {
            datasource.setLAST_SYNC(new java.sql.Timestamp(DateTime.now().getMillis()));
            datasource.setLAST_SYNC_MESSAGE(LAST_SYNC_RESULT.COMPLETE);
            datasource = (ListDatasource) updService.merge(datasource); 
        }
    }
    
    /**
     * Think of a nicer name...
     * 
     * Note:
     * - Does not update/overwrite any existing statuses in Segmail, ie. if the record exists in Segmail, regardless of status,
     * it will not be synced over.
     * - Potentially to also update the attributes of existing subscribers
     * - Will iterate the entire list from index start (does not stop after 1 batch size!) until the batch*size-th record
     * - 
     * 
     * 
     * @param datasource 
     * @param mappings 
     * @param start the start index from the source
     * @param size the fetch size for each batch from the source
     * @param batch the number of batches of [size] to complete. -1 to ignore.
     * @return the original datasource object 
     * @throws java.sql.SQLException  
     * @throws eds.component.data.EntityNotFoundException  if no Client assigned
     * @throws eds.component.data.IncompleteDataException  
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public ListDatasource syncNew(
            ListDatasource datasource,
            List<ListDataMapping> mappings, 
            int start, 
            int size,
            int batch) throws SQLException, EntityNotFoundException, IncompleteDataException {
        
        List<Client> clients = objService.getAllSourceObjectsFromTarget(datasource.getOWNER().getOBJECTID(), Assign_Client_List.class, Client.class);
        if(clients == null || clients.isEmpty())
            throw new EntityNotFoundException("No client assigned for list "+datasource.getOWNER().getOBJECTID());

        int resultCount = 0;
        int deltaCount = 0;
        int index = start;
        int batchIndex = 0;
        
        do{
            //Get remote subscribers
            List<ListDatasourceObjectWrapper> objs1 = dsQueryService.getRemoteSubscriberWrappers(
                    datasource, 
                    mappings, 
                    datasource.getKEY_FIELD(), 
                    null,
                    start + size*batchIndex++, 
                    size);

            //Subscribe them
            Map<String, List<Map<String, Object>>> results = helper.subscribe(objs1, datasource.getOWNER().getOBJECTID(), clients.get(0).getOBJECTID());

            for(List<Map<String, Object>> result : results.values()) {
                resultCount += result.size();
            }
            deltaCount = objs1.size();
            
            datasource.setNEXT_SYNC_NEW_INDEX(deltaCount > 0 ? index : 0); //If <= 0, the list has completed and next start should be reset to 0.
            //datasource = (ListDatasource) updService.merge(datasource); //Flush the updates
            
        } while (deltaCount > 0 && batchIndex < batch);
        
        
        return datasource;
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void syncRemoved(ListDatasource datasource, 
            List<ListDataMapping> mappings, 
            int start, 
            int size,
            int batch) throws SQLException, EntityNotFoundException, IncompleteDataException {
        
        int index = start;
        int deltaCount = 0;
        do{
            //Get local subscribers already sorted in ascending email order
            List<SubscriberAccount> accounts = subService.getNextNSubscribers(
                    datasource.getOWNER().getOBJECTID(), 
                    index, 
                    size);
            Map<String,SubscriberAccount> accountsMap = new HashMap<>();
            accounts.forEach(acc -> {
                accountsMap.put(acc.getEMAIL(), acc);
            });
            //Get remote subscribers
            List<ListDatasourceObjectWrapper> objs1 = dsQueryService.getRemoteSubscriberWrappers(
                    datasource, 
                    mappings, 
                    datasource.getKEY_FIELD(), 
                    new ArrayList<>(accountsMap.keySet()),
                    -1, 
                    -1);
            //For each local subscriber that does not exist in the remote list, unsubscribe it
            //objs1 is a subset of accounts
            //Sort the remote lists first
            List<String> toBeRemoved = new ArrayList<>();
            Collections.sort(objs1);
            int i = 0;
            int j = 0;
            while (i < accounts.size() && j < objs1.size()) {
                //If exists
                if(accounts.get(i).getEMAIL() == null ? objs1.get(j).getId() == null : accounts.get(i).getEMAIL().equals(objs1.get(j).getId())) {
                    i++;
                    j++;
                    continue;
                }
                //If doesn't exist
                toBeRemoved.add(accounts.get(i).getEMAIL());
                i++;
            }
            List<Subscription> subscriptions = subService.getSubscriptionsByEmails(toBeRemoved, datasource.getOWNER().getOBJECTID(), 
                    new SUBSCRIPTION_STATUS[]{ CONFIRMED });
            subscriptions.forEach(sub -> {
                try {
                    subService.unsubscribeSubscriber(sub.getUNSUBSCRIBE_KEY());
                } catch (RelationshipNotFoundException ex) {
                    Logger.getLogger(DatasourceService.class.getName()).log(Level.SEVERE, null, ex);
                    
                }
            });
            
        } while (deltaCount > 0 && index - start < batch*size);
        
    }
    
    /**
     * 
     * @param activeOnly True to select only active ListDatasource.
     * @param orderByListId True to order by list id, else random order.
     * @param start -1 to disregard this criteria, max will also be disregarded.
     * @param max -1 to disregard this criteria, max will also be disregarded.
     * @param lastSync null to disregard this criteria.
     * @param delay the amount of time that should pass after lastSync.
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<ListDatasource> getNextNDatasources(
            boolean activeOnly, 
            boolean orderByListId, 
            int start, 
            int max,
            DateTime lastSync,
            long delay) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<ListDatasource> query = builder.createQuery(ListDatasource.class);
        Root<ListDatasource> fromSource = query.from(ListDatasource.class);
        
        query.select(fromSource);
        List<Predicate> conditions = new ArrayList<>();
        if(activeOnly) {
            conditions.add(builder.isTrue(fromSource.get(ListDatasource_.ACTIVE)));
        }
        
        if(lastSync != null) {
            //Need to select records that fulfil the last sync timing and null 
            long millis = lastSync.getMillis() - Math.max(delay, 0);
            Timestamp ts = new Timestamp(millis);
            conditions.add(builder.or(
                    builder.lessThanOrEqualTo(fromSource.get(ListDatasource_.LAST_SYNC), ts),
                    builder.isNull(fromSource.get(ListDatasource_.LAST_SYNC))
            ));
        }
        //All connection attributes must be present
        conditions.add(builder.isNotNull(fromSource.get(ListDatasource_.ENDPOINT_TYPE)));
        conditions.add(builder.isNotNull(fromSource.get(ListDatasource_.SERVER_NAME)));
        conditions.add(builder.isNotNull(fromSource.get(ListDatasource_.DB_NAME)));
        conditions.add(builder.isNotNull(fromSource.get(ListDatasource_.TABLE_NAME)));
        conditions.add(builder.isNotNull(fromSource.get(ListDatasource_.USERNAME)));
        conditions.add(builder.isNotNull(fromSource.get(ListDatasource_.PASSWORD)));
        
        
        query.where(builder.and(conditions.toArray(new Predicate[]{})));
        
        if(orderByListId) {
            query.orderBy(builder.asc(fromSource.get(ListDatasource_.OWNER)));
        }
        TypedQuery q = objService.getEm().createQuery(query);
        if(start >= 0 && max >= 0) {
            q.setFirstResult(start).setMaxResults(max);
        }
        List<ListDatasource> results = q.getResultList();
        
        return results;
    }
    
    /**
     * 
     * @param ld
     * @return
     * @throws SQLException
     * @throws IncompleteDataException 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<String> getColumns(ListDatasource ld) throws SQLException, IncompleteDataException {
        List<String> results = dsQueryService.getRemoteTableColumns(ld);
        
        return results;
    }
    
    public List<String> getDistinctValues(ListDatasource ld, String fieldName) 
            throws IncompleteDataException, SQLException {
        List<String> results = dsQueryService.getDistinctValues(ld,fieldName);
        
        return results;
    }
    
    /**
     * We don't really need this, but we'll just keep here for a reference.
     * 
     * @param now
     * @param lastSync
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean checkRunSync(DateTime now, DateTime lastSync) {
        String systemSet = System.getProperty(SYNC_TIME_MS_OVERWRITE_KEY);
        long delay = DEFAULT_SYNC_TIME_MS;
        if(systemSet != null && !systemSet.isEmpty()) {
            delay = Long.parseLong(systemSet);
        }
        
        return now.getMillis() - lastSync.getMillis() > delay;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ListDatasource update(ListDatasource updated) {
        updated = (ListDatasource) updService.merge(updated);
        
        return updated;
    }
}
