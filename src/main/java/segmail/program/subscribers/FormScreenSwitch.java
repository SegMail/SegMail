/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormScreenSwitch")
public class FormScreenSwitch {
    
    @Inject ProgramSubscribers program;
    @Inject UserRequestContainer reqCont;
    
    @PostConstruct
    public void init(){
        if(!FacesContext.getCurrentInstance().isPostback()) {
            loadScreen();
        }
    }
    
    public String getCurrentScreen() {
        return program.getCurrentScreen();
    }

    public void setCurrentScreen(String currentScreen) {
        program.setCurrentScreen(currentScreen);
    }

    public String[] getSCREENS() {
        return program.getSCREENS();
    }
    
    public Map<Long,Map<String,Object>> getSubscriberTable() {
        return program.getSubscriberTable();
    }

    public void setSubscriberTable(Map<Long,Map<String,Object>> subscriberTable) {
        program.setSubscriberTable(subscriberTable);
    }
    
    public boolean isSetup() {
        return program.isSetup();
    }

    public void setSetup(boolean setup) {
        program.setSetup(setup);
    }
    
    public void loadScreen() {
        int params = reqCont.getProgramParamsOrdered().size();
        if (params == 1) {
            setCurrentScreen(getSCREENS()[2]);
            reqCont.setRenderPageToolbar(false);
            reqCont.setRenderPageBreadCrumbs(false);
            return;
        }
        
        //Check if setup screen should be loaded instead of the normal screen
        // This should be checked only once when it is not a postback
        // to prevent the whole screen not to render when a selection criteria
        // change reloads subscriberTable
        if(getSubscriberTable().isEmpty()) {
            setCurrentScreen(getSCREENS()[0]);
        } else {
            setCurrentScreen(getSCREENS()[1]);
        }
        
    }
    
}
