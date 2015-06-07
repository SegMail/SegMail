/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.list;

import eds.component.client.ClientService;
import eds.component.subscription.SubscriptionService;
import eds.entity.client.Client;
import eds.entity.subscription.SubscriptionList;
import eds.entity.user.User;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Program.ProgramContainer;
import seca2.bootstrap.module.User.UserContainer;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormListList")
@RequestScoped
public class FormListList {
    
    @Inject private UserContainer userContainer;
    @Inject private ProgramContainer programContainer;
    
    @Inject private ProgramList programList;
    
    @EJB private ClientService clientService;
    @EJB private SubscriptionService subscriptionService;
    
    @PostConstruct
    public void init(){
        //Only if it is not a postback, reload everything
        if(!FacesContext.getCurrentInstance().isPostback()){
            this.loadAllLists();
            this.resetEditingList();
        }
        
    }
    
    /**
     * Load lists to ProgramList so that other Forms can use it
     */
    public void loadAllLists(){
        User user = userContainer.getUser();

        if (user == null) {
            throw new RuntimeException("No user object found for this session " + userContainer);
        }

        Client client = clientService.getClientByAssignedObjectId(user.getOBJECTID());
        if (client == null) {
            throw new RuntimeException("No client object found for this user " + user);
        }
        
        List<SubscriptionList> allLists = subscriptionService.getAllListForClient(client.getOBJECTID());
        
        this.programList.setAllLists(allLists);
            
    }
    
    public void loadList(SubscriptionList list){
        this.programList.setListEditing(list);
    }
    
    public void resetEditingList(){
        this.programList.setListEditing(null);
    }

    public ProgramList getProgramList() {
        return programList;
    }

    public void setProgramList(ProgramList programList) {
        this.programList = programList;
    }
    
    
}
