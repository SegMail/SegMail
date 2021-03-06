/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
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
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.module.Client.ClientContainer;
import segmail.component.subscription.ListService;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.SUBSCRIBER_STATUS;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;

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
    @EJB ListService listService;
    @EJB UpdateObjectService updService;
    
    @Inject ClientContainer clientCont;
    @Inject UserRequestContainer reqCont;
    
    @PostConstruct
    public void init(){
        if(!FacesContext.getCurrentInstance().isPostback()) {
            program.initProgram();
            dripService.init(RECORDS_PER_PAGE);
            dripService.initCriteria();
            loadPage(1);
            updateToolbar();
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
    
    public List<SubscriptionListField> getFieldList() {
        return program.getFieldList();
    }

    public void setFieldList(List<SubscriptionListField> fieldList) {
        program.setFieldList(fieldList);
    }
    
    public Map<String, SubscriptionListField> getFieldMap() {
        return program.getFieldMap();
    }

    public void setFieldMap(Map<String, SubscriptionListField> fieldMap) {
        program.setFieldMap(fieldMap);
    }
    
    public String getAnyOrAll() {
        return program.getAnyOrAllLists();
    }

    public void setAnyOrAll(String anyOrAll) {
        program.setAnyOrAllLists(anyOrAll);
    }
    
    public Map<String, String> getStatusColor() {
        return program.getStatusColor();
    }
    
    public String getCheckedIds() {
        return program.getCheckedIds();
    }

    public void setCheckedIds(String checkedIds) {
        program.setCheckedIds(checkedIds);
    }

    public void setStatusColor(Map<String, String> statusColor) {
        program.setStatusColor(statusColor);
    }
    
    public int getCurrentPageRecordStart() {
        return 1 + ((getCurrentPage() - 1) * RECORDS_PER_PAGE);
    }
    
    public int getCurrentPageRecordEnd() {
        return getCurrentPageRecordStart() + getSubscriberTable().size() - 1;
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
        
        String anyOrAll = getAnyOrAll();
        String lastAnyOrAll = dripService.getAnyOrAll();
        if(lastAnyOrAll == null || !lastAnyOrAll.equals(anyOrAll)) {
            dripService.setAnyOrAll(anyOrAll);
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
        // Must clear all before loading subscriberTable
        for(SubscriberFieldValue value : values) {
            // Do not add the first EMAIL field as it is the ID of the Subscriber and cannot be edited!
            
            if(!program.getSubscriberTable().containsKey(value.getOWNER().getOBJECTID())) {
                program.getSubscriberTable().put(value.getOWNER().getOBJECTID(), new HashMap<String,Object>());
                program.getSubscriberTable().get(value.getOWNER().getOBJECTID()).put(SubscriberAccount.class.getSimpleName(), value.getOWNER());
            }
            
            Map<String,Object> subscriberMap = program.getSubscriberTable().get(value.getOWNER().getOBJECTID());
            
            // Another map to hold fields
            if(!subscriberMap.containsKey(SubscriberFieldValue.class.getSimpleName())) {
                subscriberMap.put(SubscriberFieldValue.class.getSimpleName(), new HashMap<String,SubscriberFieldValue>());
            }
            Map<String,SubscriberFieldValue> fieldMap = (Map<String,SubscriberFieldValue>) subscriberMap.get(SubscriberFieldValue.class.getSimpleName());
            fieldMap.put((String) value.getFIELD_KEY(), value);
            
            // Add a list to hold checkbox boolean values
            if(!subscriberMap.containsKey("checked")) {
                subscriberMap.put("checked", false); //Default to unchecked when initializing
            }
            
        }
        
        // Then run through subscriptions
        // Collect SubscriptionLists on the way
        List<SubscriptionList> lists = new ArrayList<>();
        for(Subscription subscription : subscriptions) {
            if(!program.getSubscriberTable().containsKey(subscription.getSOURCE().getOBJECTID())) {
                program.getSubscriberTable().put(subscription.getSOURCE().getOBJECTID(), new HashMap<String,Object>());
                program.getSubscriberTable().get(subscription.getSOURCE().getOBJECTID()).put(SubscriberAccount.class.getSimpleName(), subscription.getSOURCE());
            }
            
            Map<String,Object> subscriberMap = program.getSubscriberTable().get(subscription.getSOURCE().getOBJECTID());
            //Another map to hold the list of subscription
            if(!subscriberMap.containsKey(Subscription.class.getSimpleName())) {
                subscriberMap.put(Subscription.class.getSimpleName(), new ArrayList<Subscription>());
            }
            
            List<Subscription> subscriptionList = (List<Subscription>) subscriberMap.get(Subscription.class.getSimpleName());
            if(!subscriptionList.contains(subscription))
                subscriptionList.add(subscription);
            
            // Collect SubscriptionLists on the way
            if(!lists.contains(subscription.getTARGET()))
                lists.add(subscription.getTARGET());
        }
        
        List<SubscriptionListField> fields = listService.getFieldsForLists(lists);
        setFieldMap(new HashMap<>());
        for(SubscriptionListField field : fields) {
            getFieldMap().put((String) field.generateKey(), field);//Use generateKey instead of FIELD_KEY because we still have legacy fields that contain the old key type
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
    
    public long getTotalCount() {
        return dripService.count();
    }

    public int getRECORDS_PER_PAGE() {
        return RECORDS_PER_PAGE;
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
    
    /**
     * Cannot use this because of some JSF issue.
     * 
     * @param subscriberId
     * @param fieldKey 
     */
    public void saveField(long subscriberId, String fieldKey) {
        
        Map<String,SubscriberFieldValue> fieldMap = 
            (Map<String,SubscriberFieldValue>) getSubscriberTable().get(subscriberId)
                    .get(SubscriberFieldValue.class.getSimpleName());
        
        SubscriberFieldValue fieldValue = fieldMap.get(fieldKey);
        //fieldValue.setVALUE(value);
        
        fieldValue = (SubscriberFieldValue) updService.merge(fieldValue);
        
        fieldMap.put((String) fieldValue.generateKey(), fieldValue);
    }
    
    public void updateToolbar() {
        if(this.getSubscriberTable() == null || getSubscriberTable().isEmpty()) {
            reqCont.setRenderPageBreadCrumbs(false);
            reqCont.setRenderPageToolbar(false);
        }
    }
    
    public void testReadCheckbox() {
        String checkboxes = this.getCheckedIds();
        for(String checkedId : checkboxes.split(",")) {
            Long id = Long.parseLong(checkedId);
            System.out.println("Subscriber "+id+" is checked.");
        }
    }
}
