/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription.event;

import java.util.List;

/**
 *
 * @author LeeKiatHaw
 */
public interface SubscriberEvent {
    
    public String title();
    
    public String body();
    
    public String datetime();
    
    public String datetimePattern();
    
    public String isoDatetime();
    
    public String eventIcon();
    
    public List<SubscriberEventAction> actions();
    
    /**
     * Keys = table names
     * Values = column names
     * @return 
     */
    //public Map<String,List<String>> fields();
    
    public String selectSQL();
    
    /**
     * In JPQL
     * It will be called in the context "FROM "+event.joinSQL()+" WHERE..."
     * @return 
     */
    public String joinSQL();
    
    /**
     * In JPQL
     * It will be called in the context "WHERE "+event.whereSQL()
     * @return 
     */
    public String whereSQL(Object... params);
    
    /**
     * In JPQL
 It will be called in the context "ORDER BY "+event.orderBySQL()
     * @return 
     */
    public String orderBySQL();
    
    /**
     * In JPQL
     * It will be called in the context 
     * .setFirstResult(event.limitSQL()[0])
     * .setMaxResults(event.limitSQL()[1])
     * @return 
     */
    public int[] limitSQL();
}
