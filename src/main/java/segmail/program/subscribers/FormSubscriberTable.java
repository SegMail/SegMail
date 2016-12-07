/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import eds.component.GenericObjectService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.SUBSCRIBER_STATUS;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.Subscription;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormSubscriberTable")
public class FormSubscriberTable {
    
    private final int RECORDS_PER_PAGE = 20;
    private final int PAGE_RANGE = 7; //Must be an odd number 
    
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
            dripService.initCriteria();
            loadPage(1);
        }
    }
    
    public int getCurrentPage() {
        return program.getCurrentPage();
    }

    public void setCurrentPage(int currentPage) {
        program.setCurrentPage(currentPage);
    }
    
    public Map<Long,Map<String,Object>> getSubscriberTable() {
        return program.getSubscriberTable();
    }

    public void setSubscriberTable(Map<Long,Map<String,Object>> subscriberTable) {
        program.setSubscriberTable(subscriberTable);
    }
    
    public List<Long> getConvertedAssignedLists() {
        return program.getConvertedAssignedLists();
    }

    public void setConvertedAssignedLists(List<Long> convertedAssignedLists) {
        program.setConvertedAssignedLists(convertedAssignedLists);
    }
    
    public SUBSCRIBER_STATUS[] getSubscriberStatuses() {
        return program.getAllStatuses();
    }

    public List<String> getSubscriberStatus() {
        return program.getSubscriberStatus();
    }

    public void setSubscriberStatus(List<String> subscriberStatus) {
        program.setSubscriberStatus(subscriberStatus);
    }
    
    public List<SUBSCRIBER_STATUS> getConvertedSubscriberStatus() {
        return program.getConvertedSubscriberStatus();
    }

    public void setConvertedSubscriberStatus(List<SUBSCRIBER_STATUS> convertedSubscriberStatus) {
        program.setConvertedSubscriberStatus(convertedSubscriberStatus);
    }
    
    public String getEmailSearch() {
        return program.getEmailSearch();
    }

    public void setEmailSearch(String emailSearch) {
        program.setEmailSearch(emailSearch);
    }
    
    public void loadPage(int page) {
        setCurrentPage(page);
        loadFilterCriteria();
        loadSubscribers();
        updatePageNumbers();
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
        
        List<Long> convertedList = getConvertedAssignedLists();
        List<Long> dripList = dripService.getListIds();
        Collections.sort(convertedList);
        Collections.sort(dripList);
        if(dripList == null || !dripList.equals(convertedList)) {
            dripService.setListIds(convertedList);
            dirtyFlag = true;
        }
        
        List<SUBSCRIBER_STATUS> statuses = this.getConvertedSubscriberStatus();
        List<SUBSCRIBER_STATUS> statusList = dripService.getStatuses();
        Collections.sort(statuses);
        Collections.sort(statusList);
        if(statusList == null || !statusList.equals(statuses)) {
            dripService.setStatuses(statuses);
            dirtyFlag = true;
        }
        
        String searchString = getEmailSearch();
        String lastSearch = dripService.getEmailSearch();
        if(lastSearch == null || !lastSearch.equals(searchString)) {
            dripService.setEmailSearch(searchString);
            dirtyFlag = true;
        }
        
        //Just placeholders, remember to replace them once the actual UI components are up
        //dripService.setCreateStart(new DateTime(1800,1,1,0,0));
        //dripService.setCreateEnd(new DateTime(9999,12,31,23,59));
        //dripService.setListIds(null);
        
        if(dirtyFlag) {
            dripService.init(RECORDS_PER_PAGE); //cannot use init, because it will clear off all criteria
            //we need another method that will clear the objects and reset pointers only
        }
        
    }
    
    /**
     * 
     */
    public void loadSubscribers() {
        
        //Clear variables
        setSubscriberTable(new HashMap<Long,Map<String,Object>>());
        
        //Extract SubscriberAccounts
        List<SubscriberAccount> subscriberAccounts = dripService.drip(getCurrentPage());
        
        //Get Subscriptions for all SubscriberAccounts
        List<Long> subscriberIds = objService.extractIds(subscriberAccounts);
        List<Subscription> subscriptions = objService.getRelationshipsForSourceObjects(subscriberIds, Subscription.class);
        List<SubscriberFieldValue> values = subService.getSubscriberValuesBySubscriberObjects(subscriberAccounts);
        
        //Cache this part
        //Construct the subscriberTable
        //Run through values first
        for(SubscriberFieldValue value : values) {
            if(!program.getSubscriberTable().containsKey(value.getOWNER().getOBJECTID())) {
                program.getSubscriberTable().put(value.getOWNER().getOBJECTID(), new HashMap<String,Object>());
                program.getSubscriberTable().get(value.getOWNER().getOBJECTID()).put(SubscriberAccount.class.getName(), value.getOWNER());
            }
            
            Map<String,Object> subscriberMap = program.getSubscriberTable().get(value.getOWNER().getOBJECTID());
            //Another map to hold fields
            if(!subscriberMap.containsKey(SubscriberFieldValue.class.getName())) {
                subscriberMap.put(SubscriberFieldValue.class.getName(), new HashMap<String,SubscriberFieldValue>());
            }
            
            Map<String,SubscriberFieldValue> fieldMap = (Map<String,SubscriberFieldValue>) subscriberMap.get(SubscriberFieldValue.class.getName());
            fieldMap.put(value.getFIELD_KEY(), value);
        }
        
        //Then run through subscriptions
        for(Subscription subscription : subscriptions) {
            if(!program.getSubscriberTable().containsKey(subscription.getSOURCE().getOBJECTID())) {
                program.getSubscriberTable().put(subscription.getSOURCE().getOBJECTID(), new HashMap<String,Object>());
                program.getSubscriberTable().get(subscription.getSOURCE().getOBJECTID()).put(SubscriberAccount.class.getName(), subscription.getSOURCE());
            }
            
            Map<String,Object> subscriberMap = program.getSubscriberTable().get(subscription.getSOURCE().getOBJECTID());
            //Another map to hold the list of subscription
            if(!subscriberMap.containsKey(Subscription.class.getName())) {
                subscriberMap.put(Subscription.class.getName(), new ArrayList<Subscription>());
            }
            
            List<Subscription> subscriptionList = (List<Subscription>) subscriberMap.get(Subscription.class.getName());
            if(!subscriptionList.contains(subscription))
                subscriptionList.add(subscription);
        }
        
        //We have to do this because of the exceptionally large data from EJBs
        objService.getEm().clear();
    }
    
    public int getTotalPage() {
        return (int) ((dripService.count() / RECORDS_PER_PAGE) + 1);
    }
    
    public List<Integer> getPages() {
        return program.getPages();
    }

    public void setPages(List<Integer> pages) {
        program.setPages(pages);
    }
    
    /*
    public long startPage() {
        return Math.max(1, getCurrentPage() - (PAGE_RANGE/2));
    }
    
    public long endPage() {
        return Math.min(getTotalPage(), getCurrentPage() + (PAGE_RANGE/2));
    }
    */

    public void updatePageNumbers() {
        //This shit doesn't work
        //Update: this shit works in this way, not when it's managed in EL
        int startPage = Math.max(1, getCurrentPage() - (PAGE_RANGE/2));
        int endPage = Math.min(getTotalPage(), startPage + PAGE_RANGE -1);
        
        setPages(new ArrayList<Integer>());
        for(int i = startPage; i<= endPage; i++){
            getPages().add(i);
        }
    }
}
