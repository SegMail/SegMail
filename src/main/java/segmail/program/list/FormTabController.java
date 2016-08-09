/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Forget this! Can't set the client values back to the server so this is currentlyy
 * useless.
 * 
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormTabController")
public class FormTabController {
    
    @Inject private ProgramList program;
    
    private final String[] tabs = {
        "settings_panel",
        "fieldset_panel",
        "email_panel",
        "signup_panel",
        "subscribers_panel",
    };
    
    private final String DEFAULT_TAB = "subscribers_panel";
    
    @PostConstruct
    public void init(){
        if(getShowActiveTabs() == null || getShowActiveTabs().isEmpty()) {
            Map<String,Boolean> tabsMap = new HashMap<>();
            for(String tab : tabs){
                tabsMap.put(tab, Boolean.FALSE);
            }
            
            if(!program.getSubscriberTable().isEmpty())
                tabsMap.put("subscribers_panel", Boolean.TRUE);
            else
                tabsMap.put("settings_panel", Boolean.TRUE);
            setShowActiveTabs(tabsMap);
        }
        
    }
    
    public Map<String, Boolean> getShowActiveTabs() {
        return program.getShowActiveTabs();
    }

    public void setShowActiveTabs(Map<String, Boolean> showActiveTabs) {
        program.setShowActiveTabs(showActiveTabs);
    }
    
    public void setActiveTab(String activeTabName) {
        for(String nonActive : getShowActiveTabs().keySet()) {
            getShowActiveTabs().put(nonActive, Boolean.FALSE);
        }
        
        getShowActiveTabs().put(activeTabName,Boolean.TRUE);
        
    }
}
