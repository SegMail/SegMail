/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription;

import eds.component.DBService;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.Table;
import segmail.entity.subscription.SUBSCRIPTION_STATUS;
import segmail.entity.subscription.Subscription;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class SubscriptionServiceHelper extends DBService {
    
    public Map<String,Long> getSubscriptionCounts(long listId) {
        
        String statusCol = "STATUS";
        String countCol = "COUNT";
        String subscriptonTableName = Subscription.class.getAnnotation(Table.class).name();
        
        String sql = "";
        for(SUBSCRIPTION_STATUS status : SUBSCRIPTION_STATUS.values()) {
            if (sql.length() > 0) {
                sql += "UNION ";
            }
            sql += "SELECT '" + status.name + "' " + statusCol + ", "
                    + "COUNT(SOURCE) " + countCol + " "
                    + "FROM " + subscriptonTableName + " "
                    + "WHERE " + statusCol + " = '" + status.name + "' "
                    + "AND TARGET = " + listId + " ";
        }
        
        Query query = em.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        
        Map<String,Long> finalResult = new HashMap<>();
        for(Object[] result : results) {
            finalResult.put((String)result[0], ((BigInteger) result[1]).longValue());
        }
        
        return finalResult;
    }
}
