/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.bootstrap.module.Navigation;

import General.TreeNode;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.component.data.HibernateDBServices;
import seca2.entity.navigation.MenuItem;

/**
 *
 * @author KH
 */
@Named("NavigationModule")
@SessionScoped
public class NavigationModule implements Serializable {
    
    @Inject private HibernateDBServices hibernateDB;
    
    private TreeNode<MenuItem> menuTree; //
    
    @PostConstruct
    public void init() {
        
    }
    
}
