/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import eds.component.client.ClientFacade;
import eds.component.client.ClientService;
import eds.component.data.DBConnectionException;
import segmail.component.subscription.SubscriptionService;
import eds.entity.client.Client;
import segmail.entity.subscription.SubscriptionList;
import eds.entity.user.User;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.bootstrap.module.Program.ProgramContainer;
import seca2.bootstrap.module.User.UserContainer;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormListList")
@RequestScoped
public class FormListList {

    @Inject
    private UserContainer userContainer;
    @Inject
    private ProgramContainer programContainer;
    @Inject
    private ClientContainer clientContainer;

    @Inject
    private ProgramList programList;

    @EJB
    private ClientService clientService;
    @EJB
    private SubscriptionService subscriptionService;

    
    @PostConstruct
    public void init() {
        //Only if it is not a postback, reload everything
        if (!FacesContext.getCurrentInstance().isPostback()) {
            this.loadAllLists();
            this.resetEditingList();
        }
    }

    /**
     * Load lists to ProgramList so that other Forms can use it
     * Exceptions should be caught explicitly in every form action method instead
     * of the @PostConstruct method, because each single exception should be caught
     * and displayed separately in the main form page.
     */
    public void loadAllLists() {
        try {
            User user = userContainer.getUser();
            if (user == null) {
                throw new RuntimeException("No user object found for this session " + userContainer);
            }

            //Client client = clientService.getClientByAssignedUser(user.getOBJECTID());
            Client client = clientContainer.getClient();
            if (client == null) {
                throw new RuntimeException("No client object found for this user " + user);
            }

            List<SubscriptionList> allLists = subscriptionService.getAllListForClient(client.getOBJECTID());

            this.programList.setAllLists(allLists);
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.programList.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.programList.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }

    }

    public void loadList(SubscriptionList list) {
        this.programList.setListEditing(list);
    }

    /**
     * If there is an editing list, reload that existing list so that when it is being
     * edited, the List panel will also be updated.
     */
    public void resetEditingList() {
        SubscriptionList listEditing = programList.getListEditing();
        if(listEditing == null) return;
        
        if(programList.getAllLists() == null || programList.getAllLists().isEmpty())
            loadAllLists();
        
        for(SubscriptionList l : programList.getAllLists()){
            if(l.equals(listEditing)){
                programList.setListEditing(l);
                return;
            }
        }
        this.programList.setListEditing(null);
    }

    public ProgramList getProgramList() {
        return programList;
    }

    public void setProgramList(ProgramList programList) {
        this.programList = programList;
    }

}
