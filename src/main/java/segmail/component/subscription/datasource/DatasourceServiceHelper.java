/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription.datasource;

import eds.component.UpdateObjectService;
import eds.component.data.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import segmail.component.subscription.MassSubscriptionService;
import segmail.entity.subscription.datasource.ListDatasource;
import segmail.entity.subscription.datasource.synchronize.ListDatasourceObjectWrapper;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class DatasourceServiceHelper {
    
    @EJB MassSubscriptionService massSubService;
    @EJB UpdateObjectService updService;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Map<String, List<Map<String, Object>>> subscribe(List<ListDatasourceObjectWrapper> newSubscribers, long listId, long clientId) throws EntityNotFoundException {
        List<Map<String,Object>> subscriberMaps = new ArrayList<>();
        newSubscribers.forEach(newSubscriber -> {
            subscriberMaps.add(newSubscriber.getDsObj().getValues());
        });
        
        return massSubService.massSubscribe(clientId, subscriberMaps, listId, false);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ListDatasource updateDatasource(ListDatasource datasource) {
        return (ListDatasource) updService.merge(datasource);
    }
}
