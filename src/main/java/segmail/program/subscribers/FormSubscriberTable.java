/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import eds.component.GenericObjectService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.joda.time.DateTime;
import seca2.bootstrap.module.Client.ClientContainer;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionListField;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormSubscriberTable")
public class FormSubscriberTable {
    
    private final int RECORDS_PER_PAGE = 20;
    private final int PAGE_RANGE = 5;
    
    @Inject ProgramSubscribers program;
    @Inject SubscribersDripService dripService; 
    
    @EJB SubscriptionService subService;
    @EJB GenericObjectService objService;
    
    @Inject ClientContainer clientCont;
    
    @PostConstruct
    public void init(){
        if(!FacesContext.getCurrentInstance().isPostback()) {
            program.initProgram();
            dripService.init(RECORDS_PER_PAGE);
            loadPage(1);
            
        }
    }
    
    public int getCurrentPage() {
        return program.getCurrentPage();
    }

    public void setCurrentPage(int currentPage) {
        program.setCurrentPage(currentPage);
    }
    
    public Map<SubscriberAccount,Map<String,Object>> getSubscriberTable() {
        return program.getSubscriberTable();
    }

    public void setSubscriberTable(Map<SubscriberAccount,Map<String,Object>> subscriberTable) {
        program.setSubscriberTable(subscriberTable);
    }
    
    public void loadPage(int page) {
        setCurrentPage(page);
        loadFilterCriteria();
        loadSubscribers();
    }
    
    /**
     * 
     */
    public void loadFilterCriteria() {
        boolean dirtyFlag = false;
        
        if(dripService.getClientId() != clientCont.getClient().getOBJECTID()) {
            dripService.setClientId(clientCont.getClient().getOBJECTID());
            dirtyFlag = true;
        }
        
        //Just placeholders, remember to replace them once the actual UI components are up
        //dripService.setCreateStart(new DateTime(1800,1,1,0,0));
        //dripService.setCreateEnd(new DateTime(9999,12,31,23,59));
        //dripService.setListIds(null);
        
        if(dirtyFlag) {
            dripService.init(RECORDS_PER_PAGE);
        }
        
    }
    
    /**
     * 
     * @param page 
     */
    public void loadSubscribers() {
        
        //Extract SubscriberAccounts
        List<SubscriberAccount> subscriberAccounts = dripService.drip(getCurrentPage());
        
        //Get Subscriptions for all SubscriberAccounts
        List<Long> subscriberIds = new ArrayList<>();
        for(SubscriberAccount subscriberAccount : subscriberAccounts) {
            subscriberIds.add(subscriberAccount.getOBJECTID());
        }
        List<Subscription> subscriptions = objService.getRelationshipsForSourceObjects(subscriberIds, Subscription.class);
        List<SubscriberFieldValue> values = subService.getSubscriberValuesBySubscriberObjects(subscriberAccounts);
        
        //Construct the subscriberTable
        //Run through values first
        for(SubscriberFieldValue value : values) {
            if(!program.getSubscriberTable().containsKey(value.getOWNER()))
                program.getSubscriberTable().put(value.getOWNER(), new HashMap<String,Object>());
            
            Map<String,Object> subscriberMap = program.getSubscriberTable().get(value.getOWNER());
            //Another map to hold fields
            if(!subscriberMap.containsKey(SubscriberFieldValue.class.getName())) {
                subscriberMap.put(SubscriberFieldValue.class.getName(), new HashMap<String,SubscriberFieldValue>());
            }
            
            Map<String,SubscriberFieldValue> fieldMap = (Map<String,SubscriberFieldValue>) subscriberMap.get(SubscriberFieldValue.class.getName());
            fieldMap.put(value.getFIELD_KEY(), value);
        }
        
        //Then run through subscriptions
        for(Subscription subscription : subscriptions) {
            if(!program.getSubscriberTable().containsKey(subscription.getSOURCE()))
                program.getSubscriberTable().put(subscription.getSOURCE(), new HashMap<String,Object>());
            
            Map<String,Object> subscriberMap = program.getSubscriberTable().get(subscription.getSOURCE());
            //Another map to hold the list of subscription
            if(!subscriberMap.containsKey(Subscription.class.getName())) {
                subscriberMap.put(Subscription.class.getName(), new ArrayList<Subscription>());
            }
            
            List<Subscription> subscriptionList = (List<Subscription>) subscriberMap.get(Subscription.class.getName());
            subscriptionList.add(subscription);
        }
        
    }
    
    public long getTotalPage() {
        return (dripService.count() / RECORDS_PER_PAGE) + 1;
    }
    
    public long getStartPage() {
        return Math.max(1, getCurrentPage() - (PAGE_RANGE/2));
    }
    
    public long getEndPage() {
        return Math.min(getTotalPage(), getCurrentPage() + (PAGE_RANGE/2));
    }
    
}
