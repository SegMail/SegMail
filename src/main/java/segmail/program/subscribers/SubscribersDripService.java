/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import eds.component.GenericObjectService;
import eds.component.data.DripFeederService;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import org.joda.time.DateTime;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.SUBSCRIBER_STATUS;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberFieldValue;

/**
 *
 * @author LeeKiatHaw
 */
@SessionScoped
public class SubscribersDripService extends DripFeederService<SubscriberFieldValue> implements Serializable {

    @EJB GenericObjectService objService;
    @EJB SubscriptionService subService;
    
    //Filters
    private Long clientId;
    /**
     * If empty, select all lists
     */
    private List<Long> listIds;
    
    /**
     * Start and end criteria of the SubscriberAccount.DATE_CREATED field
     */
    private DateTime createStart;
    private DateTime createEnd;
    
    List<SUBSCRIBER_STATUS> statuses;
    
    /**
     * This implementation should: 
     * 1) select the SubscriberAccount IDs and
     * 2) select its SubscriberFieldValues
     * 
     * Note that the start and size params are referring to SubscriberAccount records, not SubscriberFieldValues.
     * Each SubscriberAccount has more than 1 SubscriberFieldValues records.
     * 
     * @param start
     * @param size
     * @return 
     */
    @Override
    public List<SubscriberFieldValue> refill(int start, int size) {
        return subService.getFieldValuesForClient(clientId, listIds, createStart, createEnd, statuses, start, size);
    }

    @Override
    protected long countFromDB() {
        return subService.countNumberSubscribers(clientId, listIds, createStart, createEnd, statuses);
    }

    
}
