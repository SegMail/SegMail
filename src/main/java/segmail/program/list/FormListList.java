/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import eds.component.GenericObjectService;
import eds.entity.client.Client;
import segmail.entity.subscription.SubscriptionList;
import eds.entity.user.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.joda.time.DateTime;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.bootstrap.UserSessionContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.ListService;
import segmail.entity.subscription.SubscriberCount;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormListList")
@RequestScoped
public class FormListList {

    @Inject
    private UserSessionContainer userContainer;
    @Inject
    private ClientContainer clientContainer;

    @Inject
    private ProgramList program;
    
    @EJB
    private ListService listService;
    @EJB
    private GenericObjectService objService;
    
    @PostConstruct
    public void init() {
        //Only if it is not a postback, reload everything
        if (!FacesContext.getCurrentInstance().isPostback()) {
            loadAllLists();
            loadSubscriberCount();
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

            List<SubscriptionList> allLists = listService.getAllListForClient(client.getOBJECTID());

            this.program.setAllLists(allLists);
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.program.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }

    }

    public void loadList(SubscriptionList list) {
        program.setListEditing(list);
        program.refresh();
    }

    /**
     * If there is an editing list, reload that existing list so that when it is being
     * edited, the List panel will also be updated.
     */
    public void resetEditingList() {
        SubscriptionList listEditing = program.getListEditing();
        if(listEditing == null) return;
        
        if(program.getAllLists() == null || program.getAllLists().isEmpty())
            loadAllLists();
        
        for(SubscriptionList l : program.getAllLists()){
            if(l.equals(listEditing)){
                program.setListEditing(l);
                return;
            }
        }
        this.program.setListEditing(null);
    }

    public ProgramList getProgram() {
        return program;
    }

    public void setProgram(ProgramList program) {
        this.program = program;
    }
    
    public List<SubscriptionList> getAllLists() {
        return program.getAllLists();
    }

    public void setAllLists(List<SubscriptionList> allLists) {
        program.setAllLists(allLists);
    }
    
    public Map<Long,SubscriberCount> getListSubscriberCounts() {
        return program.getListSubscriberCounts();
    }

    public void setListSubscriberCounts(Map<Long,SubscriberCount> listSubscriberCounts) {
        program.setListSubscriberCounts(listSubscriberCounts);
    }

    public void loadSubscriberCount() {
        List<Long> listIds = this.getAllLists().stream().map(l -> l.getOBJECTID()).collect(Collectors.toList());
        DateTime now = DateTime.now();
        List<SubscriberCount> counts = objService.getEnterpriseDataByIds(listIds, SubscriberCount.class, now, now);
        
        setListSubscriberCounts(new HashMap<>());
        for(SubscriberCount count : counts) {
            getListSubscriberCounts().put(count.getOWNER().getOBJECTID(), count);
        }
    }
}
