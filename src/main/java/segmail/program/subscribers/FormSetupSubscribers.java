/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import segmail.component.subscription.ListService;
import segmail.entity.subscription.SubscriptionList;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormSetupSubscribers")
public class FormSetupSubscribers {
    
    @Inject ProgramSubscribers program;
    
    @Inject ClientContainer clientCont;
    
    @EJB ListService listService;
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()) {
            loadOwnLists();
        }
        
    }
    
    public void loadOwnLists() {
        long clientId = clientCont.getClient().getOBJECTID();
        
        List<SubscriptionList> lists = listService.getAllListForClient(clientId);
        program.setOwnedLists(lists);
    }
}
