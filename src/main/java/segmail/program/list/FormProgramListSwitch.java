/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import eds.component.GenericObjectService;
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
import seca2.program.Program;
import segmail.component.subscription.ListService;
import segmail.entity.subscription.SubscriptionList;
import segmail.program.list.ProgramList;

/**
 * Forget this! Can't set the client values back to the server so this is currentlyy
 * useless.
 * 
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormProgramListSwitch")
public class FormProgramListSwitch {
    
    @Inject private ProgramList program;
    
    @Inject private UserRequestContainer reqContainer;
    
    @EJB ListService listService;
    @EJB GenericObjectService objService;
    
    @PostConstruct
    public void init(){
        if(!FacesContext.getCurrentInstance().isPostback()) {
            loadListOrLists();
        }
        
    }
    
    public boolean isEditListMode() {
        return program.getListEditing() != null;
    }
    
    public Map<String, Boolean> getShowActiveTabs() {
        return program.getShowActiveTabs();
    }

    public void setShowActiveTabs(Map<String, Boolean> showActiveTabs) {
        program.setShowActiveTabs(showActiveTabs);
    }

    public void loadListOrLists(){
        List<String> params = reqContainer.getProgramParamsOrdered();
        if (params == null || params.isEmpty()) {
            program.setListEditing(null);
            return;
        }
        long newId = Long.parseLong(params.get(0));
        
        SubscriptionList listEditing = objService.getEnterpriseObjectById(newId, SubscriptionList.class);
        program.setListEditing(listEditing);
        
    }
    
}
