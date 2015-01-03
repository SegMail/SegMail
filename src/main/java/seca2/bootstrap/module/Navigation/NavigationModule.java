/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Navigation;

import TreeAPI.TreeNode;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import seca2.bootstrap.BootstrapInput;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.BootstrapOutput;
import seca2.bootstrap.CoreModule;
import seca2.bootstrap.DefaultSites;
import seca2.bootstrap.module.User.UserSession;
import seca2.component.data.DBConnectionException;
import seca2.component.navigation.MenuContainer;
import seca2.component.navigation.NavigationService;
import seca2.entity.navigation.MenuItem;

/**
 * Builds the navigation structure for the user.
 *
 * @author KH
 */
//@Named("NavigationModule")
//@SessionScoped
//@BootstrapSession
//@BootstrapType(postback=false)
@CoreModule
public class NavigationModule extends BootstrapModule implements Serializable {

    @EJB
    private NavigationService navigationService;
    //A MenuContainer should be inside a UserContainer, so there's no need for so many containers.
    //@Inject private MenuContainer menuContainer; //This module initializes the menu so that other programs and components can use it
    @Inject
    private UserSession userContainer;

    @PostConstruct
    public void init() {
        //Construct menuTree from DB

    }

    public List<MenuItem> getAllMenuList() throws DBConnectionException {
        return navigationService.getAllMenuItems();
    }

    @Override
    protected int executionSequence() {
        return -98;
    }

    @Override
    protected boolean execute(BootstrapInput inputContext, BootstrapOutput outputContext) {
        //BootstrapModules should not access database, but let individual forms do it 
        /*if(menuContainer.getAllMenuItems() == null){
         try {
         menuContainer.setAllMenuItems(this.getAllMenuList());
         } catch (DBConnectionException ex) {
         outputContext.setPageRoot(this.defaultSites.ERROR_PAGE);
         return false;
         }
         }*/
        if (userContainer != null && userContainer.isLoggedIn()) {
            //What else should I do here?
            
        }
        
        //As of 20141210, I can't find any reason why this module should stop the
        //entire chain processing, so we'll just return true for now.
        return true;
    }

    @Override
    protected boolean inService() {
        return false;
    }

}
