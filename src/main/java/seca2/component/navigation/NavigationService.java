/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.component.navigation;

import EDS.Entity.EnterpriseObject;
import EDS.Entity.EnterpriseRelationship_;
import General.TreeNode;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import seca2.component.data.HibernateEMServices;
import seca2.component.user.UserService;
import seca2.entity.navigation.MenuItem;
import seca2.entity.navigation.MenuItemAccess;
import seca2.entity.navigation.MenuItemAccess_;
import seca2.entity.program.Program;
import seca2.entity.user.UserType;

/**
 * Handles the navigation of the application
 * 
 * Entities required:
 * - MenuItem
 * - UserAccount
 * - UserType
 * 
 * @author KH
 */
@Stateless
public class NavigationService {
    
    @Inject private HibernateEMServices hibernateDB;
    @EJB private UserService userService;
    
    EntityManager em;
    
    public TreeNode<MenuItem> buildMenuForUserType(UserType userType){
        if(em == null || !em.isOpen())
            em = hibernateDB.getEM();
        
        long userTypeId = userType.getOBJECTID();
        
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<MenuItemAccess> criteria = builder.createQuery(MenuItemAccess.class);
        Root<MenuItemAccess> sourceEntity = criteria.from(MenuItemAccess.class);
        
        criteria.where(builder.equal(sourceEntity.get(MenuItemAccess_.SOURCE), userType));
        List<MenuItemAccess> menuItemAccess = em.createQuery(criteria).getResultList();
        
        return null;
    }
    
    public long createMenuItem(String name, String requestUrl, String xhtml, long parentMenuItemId) 
            throws CreateMenuItemException{
        if(em == null || !em.isOpen())
            em = hibernateDB.getEM();
        
        //get parent MenuItem
        MenuItem parentMenuItem = em.find(MenuItem.class, parentMenuItemId);
        
        if(parentMenuItem == null)
            throw new CreateMenuItemException("Parent MenuItem Id "+parentMenuItemId+" does not exist.");
        
        MenuItem newMenuItem = new MenuItem();
        newMenuItem.setMENU_ITEM_NAME(name);
        newMenuItem.setPARENT_MENU_ITEM(parentMenuItem);
        newMenuItem.setMENU_ITEM_URL(requestUrl);
        newMenuItem.setMENU_ITEM_XHTML(xhtml);
        
        em.getTransaction().begin();
        em.persist(newMenuItem);
        em.getTransaction().commit();
        
        return newMenuItem.getOBJECTID();
    }
    
    public void assignMenuItemAccess(long userTypeId, long menuItemId){
        
    }
}
