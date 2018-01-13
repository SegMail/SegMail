/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription;

import eds.component.DBService;
import eds.component.GenericObjectService;
import eds.component.data.DataValidationException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.SubscriberFieldValue_;
import segmail.entity.subscription.SubscriptionListField;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class SubscriberService extends DBService {
    
    @EJB ListService listService;
    @EJB GenericObjectService objService;
    @EJB SubscriptionService subService;
    
    /**
     * 
     * @param subscriberId Owner
     * @param fieldKey a valid SubscriptionListField
     * @return the number of records deleted
     * @throws DataValidationException if fieldKey is not a valid SubscriptionListField
     */
    public int deleteSubscriberField(long subscriberId, String fieldKey) 
            throws DataValidationException {
        
        if(subscriberId <= 0)
            throw new DataValidationException("Subscriber Id is missing.");
        
        if(fieldKey == null || fieldKey.isEmpty())
            throw new DataValidationException("Field key is missing.");
        
        // Check if the field exists
        List<String> keys = new ArrayList<>();
        keys.add(fieldKey);
        List<SubscriptionListField> fields = listService.getFieldsByKeyOrLists(keys, null);
        if(fields == null || fields.isEmpty()) 
            throw new DataValidationException("Field key "+fieldKey+" not found.");
        
        // Check if it is the default email field or other important system fields
        if(SubscriptionService.DEFAULT_EMAIL_FIELD_NAME.equalsIgnoreCase(
                fields.get(0).getFIELD_NAME()))
            throw new DataValidationException(SubscriptionService.DEFAULT_EMAIL_FIELD_NAME 
                    + " field cannot be deleted.");
        
        // Start delete query
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaDelete<SubscriberFieldValue> query = builder.createCriteriaDelete(SubscriberFieldValue.class);
        Root<SubscriberFieldValue> fromValue = query.from(SubscriberFieldValue.class);
        
        query.where(builder.and(
                builder.equal(fromValue.get(SubscriberFieldValue_.OWNER), subscriberId),
                builder.equal(fromValue.get(SubscriberFieldValue_.FIELD_KEY), fieldKey)
        ));
        
        int result = em.createQuery(query)
                .executeUpdate();
        
        return result;
    }
    
    
}
