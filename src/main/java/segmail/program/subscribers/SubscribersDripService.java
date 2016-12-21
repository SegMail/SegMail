/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import eds.component.GenericObjectService;
import eds.component.data.DataValidationException;
import eds.component.data.DripFeederService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import org.joda.time.DateTime;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.SUBSCRIBER_STATUS;
import segmail.entity.subscription.SubscriberAccount;

/**
 *
 * @author LeeKiatHaw
 */
@SessionScoped
public class SubscribersDripService extends DripFeederService<SubscriberAccount> implements Serializable {

    @EJB GenericObjectService objService;
    @EJB SubscriptionService subService;
    
    //Filters
    private long clientId;
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
    
    private String emailSearch;

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public List<Long> getListIds() {
        return listIds;
    }

    public void setListIds(List<Long> listIds) {
        this.listIds = listIds;
    }

    public DateTime getCreateStart() {
        return createStart;
    }

    public void setCreateStart(DateTime createStart) {
        this.createStart = createStart;
    }

    public DateTime getCreateEnd() {
        return createEnd;
    }

    public void setCreateEnd(DateTime createEnd) {
        this.createEnd = createEnd;
    }

    public List<SUBSCRIBER_STATUS> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<SUBSCRIBER_STATUS> statuses) {
        this.statuses = statuses;
    }

    public String getEmailSearch() {
        return emailSearch;
    }

    public void setEmailSearch(String emailSearch) {
        this.emailSearch = emailSearch;
    }
    
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
    public List<SubscriberAccount> refill(int start, int size) {
        try {
            return subService.getSubscribersForClient(clientId, listIds, createStart, createEnd, statuses, emailSearch, start, size);
        } catch (DataValidationException ex) {
            Logger.getLogger(SubscribersDripService.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected long countFromDB() {
        try {
            return subService.countNumberSubscribers(clientId, listIds, createStart, createEnd, statuses, emailSearch);
        } catch (DataValidationException ex) {
            Logger.getLogger(SubscribersDripService.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void initCriteria() {
        this.listIds = new ArrayList<>();
        this.statuses = new ArrayList<>();
    }

    
}
