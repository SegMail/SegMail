/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription.reporting;

import eds.component.DBService;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.joda.time.DateTime;
import segmail.component.subscription.ListService;
import segmail.entity.subscription.Assign_Client_List;
import segmail.entity.subscription.Assign_Client_List_;
import segmail.entity.subscription.SUBSCRIPTION_STATUS;
import segmail.entity.subscription.SubscriberCount;
import segmail.entity.subscription.SubscriberCount_;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.SubscriberOwnership;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionListField;

/**
 * Pure reporting bean.
 * 
 * @author LeeKiatHaw
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class SubscriptionReportService extends DBService {
    
    @EJB ListService listService;
    
    public SignupCountContainer getSignupsByDate(long clientId, DateTime start, DateTime end) {
        String subTable = Subscription.class.getAnnotation(Table.class).name();
        String assignTable = Assign_Client_List.class.getAnnotation(Table.class).name();
        String subAlias = "sub";
        String assignAlias = "assign";
        String source = "SOURCE";
        String target = "TARGET";
        String dateCreated = "DATE_CREATED";
        
        String sql = "";
        sql += "SELECT " + subAlias + "." + dateCreated + ", COUNT(" + subAlias + "." + source + ") ";
        sql += "FROM " + subTable + " " + subAlias + " ";
        sql += "JOIN " + assignTable + " " + assignAlias + " ";
        sql +=      "ON " + subAlias + "." + target + " = " + assignAlias + "." + target + " ";
        sql +=      "AND " + assignAlias + "." + source + " = " + clientId + " ";
        sql +=      "AND " + subAlias + "." + dateCreated + " BETWEEN " 
                        + "'" + start.toString("yyyy-MM-dd") + "'" 
                        + " AND " + "'" + end.toString("yyyy-MM-dd") + "'" +" ";
        sql += "GROUP BY " + subAlias + "." + dateCreated + " ";
        
        Query query = em.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        
        SignupCountContainer cont = new SignupCountContainer();
        for(Object[] result : results) {
            String dateString = result[0].toString();
            BigInteger count = (BigInteger) result[1];
            cont.addSignup(dateString,count.longValue());
        }
        
        return cont;
    }
    
    public UnsubscribeCountContainer getUnsubsByDate(long clientId, DateTime start, DateTime end) {
        String subTable = Subscription.class.getAnnotation(Table.class).name();
        String assignTable = Assign_Client_List.class.getAnnotation(Table.class).name();
        String subAlias = "sub";
        String assignAlias = "assign";
        String source = "SOURCE";
        String target = "TARGET";
        String dateChanged = "DATE_CHANGED";
        String status = "STATUS";
        
        String sql = "";
        sql += "SELECT " + subAlias + "." + dateChanged + ", COUNT(" + subAlias + "." + source + ") ";
        sql += "FROM " + subTable + " " + subAlias + " ";
        sql += "JOIN " + assignTable + " " + assignAlias + " ";
        sql +=      "ON " + subAlias + "." + target + " = " + assignAlias + "." + target + " ";
        sql +=      "AND " + subAlias + "." + status + " = '" + SUBSCRIPTION_STATUS.UNSUBSCRIBED.name + "' ";
        sql +=      "AND " + assignAlias + "." + source + " = " + clientId + " ";
        sql +=      "AND " + subAlias + "." + dateChanged + " BETWEEN " 
                        + "'" + start.toString("yyyy-MM-dd") + "'" 
                        + " AND " + "'" + end.toString("yyyy-MM-dd") + "'" +" ";
        sql += "GROUP BY " + subAlias + "." + dateChanged + " ";
        
        Query query = em.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        
        UnsubscribeCountContainer cont = new UnsubscribeCountContainer();
        for(Object[] result : results) {
            String dateString = result[0].toString();
            BigInteger count = (BigInteger) result[1];
            cont.addUnsubscribe(dateString,count.longValue());
        }
        
        return cont;
    }
    
    public TotalSubscriptionContainer getActiveSubscriptions(long clientId, DateTime start, DateTime end) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<SubscriberCount> query = builder.createQuery(SubscriberCount.class);
        Root<SubscriberCount> fromCount = query.from(SubscriberCount.class);
        Root<Assign_Client_List> fromAssign = query.from(Assign_Client_List.class);
        
        query.select(fromCount);
        query.where(builder.and(
                builder.lessThanOrEqualTo(fromCount.get(SubscriberCount_.START_DATE), new java.sql.Date(end.getMillis())),
                builder.greaterThanOrEqualTo(fromCount.get(SubscriberCount_.END_DATE), new java.sql.Date(start.getMillis())),
                builder.equal(fromCount.get(SubscriberCount_.OWNER), fromAssign.get(Assign_Client_List_.TARGET)),
                builder.equal(fromAssign.get(Assign_Client_List_.SOURCE), clientId)
        ));
        
        List<SubscriberCount> results = em.createQuery(query)
                .getResultList();
        TotalSubscriptionContainer cont = new TotalSubscriptionContainer();
        for(SubscriberCount result : results) {
            Map<String,Long> count = result.getCOUNT();
            for(String status : count.keySet()) {
                SUBSCRIPTION_STATUS s = SUBSCRIPTION_STATUS.valueOf(status);
                cont.addCount(new DateTime(result.getSTART_DATE()), s, count.get(status));
            }
        }
        
        return cont;
    }
    
    public LatestSubscribersContainer getLatestSubscribers(long clientId, DateTime start, DateTime end, int n) {
        String subValueTable = SubscriberFieldValue.class.getAnnotation(Table.class).name();
        String ownerTable = SubscriberOwnership.class.getAnnotation(Table.class).name();
        String valAlias = "val";
        String ownerAlias = "own";
        
        String sql = "";
        sql += "SELECT " + valAlias + ".* "
            + "FROM " + subValueTable + " " + valAlias + " "
            + "JOIN (" 
                + "SELECT DISTINCT OWNER "
                + "FROM SUBSCRIBER_FIELD_VALUE val "
                + "JOIN SUBSCRIBER_OWNERSHIP own ON val.OWNER = own.SOURCE AND own.TARGET = " + clientId + " "
                + "ORDER BY val.DATE_CREATED DESC " 
                + "LIMIT 0," + n + " "
                + ") top ON val.OWNER = top.OWNER "
        ;
        
        Query query = em.createNativeQuery(sql,SubscriberFieldValue.class);
        List<SubscriberFieldValue> results = query.getResultList();
        
        LatestSubscribersContainer cont = new LatestSubscribersContainer();
        Set<String> fieldKeys = new HashSet<>();
        for(SubscriberFieldValue value : results) {
            fieldKeys.add(value.getFIELD_KEY());
            cont.addSubscriberValue(value);
        }
        List<SubscriptionListField> fields = listService.getFieldsByKeyOrLists(fieldKeys.stream().collect(toList()), null);
        for(SubscriptionListField field : fields) {
            cont.addListField(field);
        }
        
        return cont;
    }
}
