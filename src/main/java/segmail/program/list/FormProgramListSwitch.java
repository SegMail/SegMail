/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import eds.component.GenericObjectService;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import segmail.component.subscription.ListService;
import segmail.entity.subscription.SubscriptionList;

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
            initActiveTab();
            initPageToolbar();
            loadListOrLists();
        }
        
    }
    
    public String getActiveTab() {
        return program.getActiveTab();
    }

    public void setActiveTab(String activeTab) {
        program.setActiveTab(activeTab);
    }
    
    public boolean isEditListMode() {
        return program.getListEditing() != null;
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
    
    public void initActiveTab() {
        List<String> params = reqContainer.getProgramParamsOrdered();
        String currentTab = getActiveTab();
        String nextTab = program.TABS[0]; // default
        
        // If the tab name is provided in url
        if(params != null && params.size() >= 2) {
            String param = params.get(1);
            nextTab = (param != null && !param.isEmpty()) ? param : nextTab;
        }
        
        // If the current request is a refresh, get the current tab (Highest precedence)
        // If current request is empty, then nextTab should still be the default tab
        if(program.isLastReqRefresh()) {
            nextTab = (currentTab != null && !currentTab.isEmpty()) ? currentTab : nextTab;
        }
        
        // Only set activeTab to nextTab if it is found in the TABS array
        for(String TAB : program.TABS) {
            if(TAB.equals(nextTab)) {
                setActiveTab(TAB);
                return;
            }
        }
    }
    
    public void initPageToolbar() {
        List<String> params = reqContainer.getProgramParamsOrdered();
    
        // When in edit list page
        if(params != null && params.size() >= 1) {
            reqContainer.setRenderPageToolbar(false);
            reqContainer.setRenderPageBreadCrumbs(false);
            return;
        }
        
        // When no list available
        if(program.getAllLists() == null || program.getAllLists().isEmpty()) {
            reqContainer.setRenderPageToolbar(false);
            reqContainer.setRenderPageBreadCrumbs(false);
            return;
        }
    }
}
