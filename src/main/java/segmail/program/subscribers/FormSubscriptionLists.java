/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import eds.entity.client.Client;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import segmail.component.subscription.ListService;
import segmail.component.subscription.SubscriptionContainer;
import segmail.entity.subscription.SubscriptionList;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormSubscriptionLists")
public class FormSubscriptionLists {
    
    @Inject ProgramSubscribers program;
    @Inject FormSubscriberTable formSubscriberTable;
    @Inject FormImportSubscribers formImportSubscribers;
    
    @Inject ClientContainer clientCont;
    
    @EJB ListService listService;
    
    @PostConstruct
    public void init() {
        loadLists();
    }
    
    public List<Long> getConvertedAssignedLists() {
        return program.getConvertedAssignedLists();
    }

    public void setConvertedAssignedLists(List<Long> convertedAssignedLists) {
        program.setConvertedAssignedLists(convertedAssignedLists);
    }
    
    public List<String> getAssignedLists() {
        return program.getAssignedLists();
    }

    public void setAssignedLists(List<String> assignedLists) {
        program.setAssignedLists(assignedLists);
    }

    public List<SubscriptionList> getOwnedLists() {
        return program.getOwnedLists();
    }

    public void setOwnedLists(List<SubscriptionList> ownedLists) {
        program.setOwnedLists(ownedLists);
    }
    
    public String getAnyOrAllLists() {
        return program.getAnyOrAllLists();
    }

    public void setAnyOrAllLists(String anyOrAll) {
        program.setAnyOrAllLists(anyOrAll);
    }
    
    public void listChangeUpdate() {
        
        //Convert assignedLists to convertedAssignedLists
        setConvertedAssignedLists(new ArrayList<Long>());
        for(String idString : getAssignedLists()) {
            long id = Long.parseLong(idString);
            getConvertedAssignedLists().add(id);   
        }
        
        //formImportSubscribers.setupImport();
        
        //Collections.sort(getConvertedAssignedLists()); //redundant since we use List.containsAll()
        //erm...containsAll() is not the correct method to use
        formSubscriberTable.loadPage(1); //this will take care of all the criteria itself
    }
    
    public void loadLists() {
        Client client = clientCont.getClient();
        List<SubscriptionList> lists = listService.getAllListForClient(client.getOBJECTID());
        
        setOwnedLists(lists);
    }
}
