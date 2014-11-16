/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.bootstrap.module.Navigation;

import java.io.Serializable;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import seca2.bootstrap.BootstrapModule;
import seca2.component.navigation.NavigationService;

/**
 * Builds the navigation structure for the user.
 * 
 * @author KH
 */
//@Named("NavigationModule")
//@SessionScoped
//@BootstrapSession
//@BootstrapType(postback=false)
public class NavigationModule extends BootstrapModule implements Serializable  {
    
    @EJB private NavigationService navigationService;
    
    @PostConstruct
    public void init() {
        //Construct menuTree from DB
        
    }

    @Override
    protected int executionSequence() {
        return -100;
    }

    @Override
    protected boolean execute(Map<String, Object> inputContext, Map<String, Object> outputContext) {
        
        
        
        
        return true;
    }

    

}
