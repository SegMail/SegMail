/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription.event;

import eds.component.DBService;
import eds.component.GenericObjectService;
import eds.component.data.EntityNotFoundException;
import eds.component.data.RelationshipNotFoundException;
import eds.entity.client.Client;
import eds.entity.mail.SentEmail;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import segmail.component.subscription.SubscriptionService;
import segmail.component.subscription.event.impl.FirstSubscriptionEvent;
import segmail.component.subscription.event.impl.NextSubscriptionEvent;
import segmail.component.subscription.event.impl.ReceivedCampaignEmailEvent;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.campaign.Trigger_Email_Activity;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberOwnership;
import segmail.entity.subscription.Subscription;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class SubscriberTimelineService extends DBService {
    
    @EJB GenericObjectService objService;
    @EJB SubscriptionService subService;

    public List<SubscriberEvent> buildSubscriberTimeline(long subscriberId) throws RelationshipNotFoundException, EntityNotFoundException {
        SubscriberAccount acc = objService.getEnterpriseObjectById(subscriberId, SubscriberAccount.class);
        if (acc == null) {
            throw new EntityNotFoundException("SubscriberAccount " + subscriberId + " not found.");
        }
        List<Client> clients = objService.getAllTargetObjectsFromSource(subscriberId, SubscriberOwnership.class, Client.class);
        if (clients == null || clients.isEmpty()) {
            throw new RelationshipNotFoundException("Subscriber " + subscriberId + " does not have a Client.");
        }
        
        // Generate campaign events
        List<SubscriberEvent> campaignEvents = generateCampaignEvent(subscriberId);
        // Generate 1st subscription
        List<SubscriberEvent> firstSubsc = generateFirstSubscriptionEvent(subscriberId);
        // Generate the rest of the subscription
        List<SubscriberEvent> nextSubsc = generateNextSubscriptionEvent(subscriberId);
        
        List<SubscriberEvent> results = new ArrayList<>();
        results.addAll(campaignEvents);
        results.addAll(firstSubsc);
        results.addAll(nextSubsc);
        
        results.sort(new Comparator<SubscriberEvent>() {

            @Override
            public int compare(SubscriberEvent o1, SubscriberEvent o2) {
                DateTime dt1 = DateTime.parse(o1.datetime(),DateTimeFormat.forPattern(o1.datetimePattern()));
                DateTime dt2 = DateTime.parse(o2.datetime(),DateTimeFormat.forPattern(o2.datetimePattern()));
                
                return -((dt1 == null) ? -1 : dt1.compareTo(dt2)); //negate to get descending order
            }
            
        });
        
        return results;
    }
    
    public List<SubscriberEvent> generateCampaignEvent(long subscriberId) {
        // Placeholder 
        ReceivedCampaignEmailEvent e = new ReceivedCampaignEmailEvent();
        String sql = "SELECT " + e.selectSQL() 
                + " FROM " + e.joinSQL() 
                + " WHERE " + e.whereSQL(subscriberId);
        List<Object[]> results = em.createQuery(sql).getResultList();
        
        List<SubscriberEvent> events = new ArrayList<>();
        for(Object[] result : results) {
            SubscriberEvent event = new ReceivedCampaignEmailEvent(
                    (Trigger_Email_Activity) result[0],
                    (CampaignActivity) result[1],
                    (SentEmail) result[2]); 
            
            events.add(event);
        }
        
        return events;
    }
    
    public List<SubscriberEvent> generateFirstSubscriptionEvent(long subscriberId) {
        SubscriberEvent e = new FirstSubscriptionEvent();
        String sql = "SELECT " + e.selectSQL() 
                + " FROM " + e.joinSQL() 
                + " WHERE " + e.whereSQL(subscriberId)
                + " ORDER BY " + e.orderBySQL();
        List<Subscription> results = em.createQuery(sql)
                .setFirstResult(e.limitSQL()[0]).setMaxResults(e.limitSQL()[1])
                .getResultList();
        
        List<SubscriberEvent> events = new ArrayList<>();
        for(Subscription result : results) {
            SubscriberEvent event = new FirstSubscriptionEvent(result);
            
            events.add(event);
        }
        
        return events;
    }
    
    public List<SubscriberEvent> generateNextSubscriptionEvent(long subscriberId) {
        SubscriberEvent e = new NextSubscriptionEvent();
        String sql = "SELECT " + e.selectSQL() 
                + " FROM " + e.joinSQL() 
                + " WHERE " + e.whereSQL(subscriberId)
                + " ORDER BY " + e.orderBySQL();
        List<Subscription> results = em.createQuery(sql)
                .setFirstResult(1)
                .getResultList();
        
        List<SubscriberEvent> events = new ArrayList<>();
        for(Subscription result : results) {
            SubscriberEvent event = new NextSubscriptionEvent(result);
            
            events.add(event);
        }
        
        return events;
    }
}
