/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.dashboard;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.UserSessionContainer;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormSwitcherDashboard")
@RequestScoped
public class FormSwitcherDashboard {
    
    @Inject ProgramDashboard program;
    
    @Inject UserRequestContainer reqCont;
    @Inject UserSessionContainer sessCont;
    
    @PostConstruct
    public void init(){
        initPageToolbar();
    }
    
    public void initPageToolbar() {
        reqCont.setRenderPageToolbar(false);
        reqCont.setRenderPageBreadCrumbs(false); 
        
    }
}
