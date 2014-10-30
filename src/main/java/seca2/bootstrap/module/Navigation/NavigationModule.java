/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.bootstrap.module.Navigation;

import TreeAPI.TreeBranch;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.BootstrapModule;
import seca2.component.data.HibernateEMServices;
import seca2.component.navigation.NavigationService;
import seca2.entity.navigation.MenuItem;

/**
 * Builds the navigation menu.
 * @author KH
 */
@Named("NavigationModule")
@SessionScoped
public class NavigationModule extends BootstrapModule implements Serializable  {
    
    @EJB private NavigationService navigationService;
    
    @PostConstruct
    public void init() {
        //Construct menuTree from DB
        
    }

    @Override
    protected void doStuff(Map<String, Object> input, Map<String, Object> output) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
