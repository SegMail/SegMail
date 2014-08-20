/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.bootstrap.module.Navigation;

import General.TreeNode;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.component.data.HibernateEMServices;
import seca2.component.navigation.NavigationService;
import seca2.entity.navigation.MenuItem;

/**
 * Builds the navigation menu.
 * @author KH
 */
@Named("NavigationModule")
@SessionScoped
public class NavigationModule implements Serializable {
    
    @EJB private NavigationService navigationService;
    
    private TreeNode<MenuItem> menuTree; //
    
    @PostConstruct
    public void init() {
        //Construct menuTree from DB
        
    }

    public TreeNode<MenuItem> getMenuTree() {
        return menuTree;
    }

    public void setMenuTree(TreeNode<MenuItem> menuTree) {
        this.menuTree = menuTree;
    }
    
    
}
