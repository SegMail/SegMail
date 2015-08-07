package segmail.program.list;

import eds.component.GenericObjectService;
import eds.component.client.ClientService;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriptionList;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import javax.inject.Named;
import seca2.bootstrap.module.Program.ProgramContainer;
import seca2.bootstrap.module.User.UserContainer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Shared across the entire /list/ program page
 * @author LeeKiatHaw
 */
@Named("ProgramList")
@SessionScoped
public class ProgramList implements Serializable {
    
    @Inject private UserContainer userContainer;
    @Inject private ProgramContainer programContainer;
    
    @EJB
    private ClientService clientService;
    @EJB
    private GenericObjectService genericDBService;
    @EJB
    private SubscriptionService subscriptionService;
    
    private List<SubscriptionList> allLists;
    private SubscriptionList listEditing;
    private List<SubscriberAccount> subscribers;
    
    private final Map<String,String> editingPanelLocation = new HashMap<String,String>();
    
    private boolean startFirstList;
    
    public ProgramList(){
        editingPanelLocation.put("subscribers", "");
        editingPanelLocation.put("activities", "");
        editingPanelLocation.put("signupforms", "");
        editingPanelLocation.put("settings", "");
    }
    
    @PostConstruct
    public void init(){
        
    }

    public SubscriptionList getListEditing() {
        return listEditing;
    }

    public void setListEditing(SubscriptionList listEditing) {
        this.listEditing = listEditing;
    }    

    public boolean isStartFirstList() {
        return startFirstList;
    }

    public void setStartFirstList(boolean startFirstList) {
        this.startFirstList = startFirstList;
    }

    public List<SubscriptionList> getAllLists() {
        return allLists;
    }

    public void setAllLists(List<SubscriptionList> allLists) {
        this.allLists = allLists;
    }

    public List<SubscriberAccount> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<SubscriberAccount> subscribers) {
        this.subscribers = subscribers;
    }
    
    
}
