/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.component.navigation;

import General.TreeNode;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.exception.GenericJDBCException;
import seca2.bootstrap.GlobalValues;
import seca2.component.data.DBConnectionException;
import seca2.component.data.HibernateEMServices;
import seca2.component.user.UserServiceHibernate;
import seca2.entity.navigation.MenuItem;
import seca2.entity.navigation.MenuItemAccess;
import seca2.entity.navigation.MenuItemAccess_;
import seca2.entity.user.UserType;

/**
 * Handles the navigation of the application
 *
 * Entities required: - MenuItem - UserAccount - UserType
 *
 * @author KH
 */
@Stateless
public class NavigationService implements Serializable {

    @EJB
    private HibernateEMServices hibernateDB;
    //@EJB
    //private UserService userService;

    private EntityManager em;

    /**
     * 
     * Transitivity: Given a tree a->b->c, if userType has access to only a and b
     * but not b, then this function will not return c as part of the menu tree.
     * 
     * @param userType
     * @return 
     */
    public TreeNode<MenuItem> buildMenuForUserType(UserType userType) throws DBConnectionException {
        if (em == null || !em.isOpen()) {
            em = hibernateDB.getEM();
        }
        
        try{
            //1. Get all MenuItemAccess by userType ID.
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<MenuItemAccess> criteria = builder.createQuery(MenuItemAccess.class);
            Root<MenuItemAccess> sourceEntity = criteria.from(MenuItemAccess.class);
            criteria.select(sourceEntity);
            criteria.where(builder.equal(sourceEntity.get(MenuItemAccess_.TARGET), userType));

            List<MenuItemAccess> results = em.createQuery(criteria)
                    .setFirstResult(0)
                    .setMaxResults(GlobalValues.MAX_RESULT_SIZE_DB)
                    .getResultList();
            
            //2. Iterate through MenuItemAccess list in ascending order of parent and build the tree from root
            
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
        
        

        return null;
    }

    /**
     * If parentMenunItemId is negative, not found, create the menu item as root.
     * 
     * @param name
     * @param requestUrl
     * @param xhtml
     * @param parentMenuItemId
     * @return
     * @throws CreateMenuItemException
     * @throws DBConnectionException 
     */
    public long createMenuItem(String name, String requestUrl, String xhtml, long parentMenuItemId)
            throws CreateMenuItemException, DBConnectionException {
        if (em == null || !em.isOpen()) {
            em = hibernateDB.getEM();
        }
        
        try {
            if(name == null || name.length() <= 0)
                throw new CreateMenuItemException("MenuItem name cannot be empty!");
            if(requestUrl == null || requestUrl.length() <= 0)
                throw new CreateMenuItemException("MenuItem URL cannot be empty!");
            if(xhtml == null || xhtml.length() <= 0)
                throw new CreateMenuItemException("MenuItem XHTML cannot be empty!");
            //get parent MenuItem
            MenuItem parentMenuItem = em.find(MenuItem.class, parentMenuItemId);

            //Assign root as default if no parent is inputted?
            /**
             * If no parent is found, create it as a root.
             
            if (parentMenuItem == null) {
                throw new CreateMenuItemException("Parent MenuItem Id " + parentMenuItemId + " does not exist.");
            }*/
            
            MenuItem newMenuItem = new MenuItem();
            newMenuItem.setMENU_ITEM_NAME(name);
            newMenuItem.setPARENT_MENU_ITEM(parentMenuItem);
            newMenuItem.setMENU_ITEM_URL(requestUrl);
            newMenuItem.setMENU_ITEM_XHTML(xhtml);
            
            em.getTransaction().begin();
            em.persist(newMenuItem);
            em.getTransaction().commit();
            
            return newMenuItem.getOBJECTID();
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                //throw new DBConnectionException(pex.getCause().getMessage());
                throw new DBConnectionException();
            }
            throw new CreateMenuItemException(pex.getMessage());
        } catch (Exception ex) {
            throw new CreateMenuItemException(ex.getMessage());
        }

        
    }

    public void assignMenuItemAccess(long userTypeId, long menuItemId) {

    }

    public List<MenuItem> getAllMenuItems() throws DBConnectionException {
        if (em == null || !em.isOpen()) {
            em = hibernateDB.getEM();
        }
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<MenuItem> criteria = builder.createQuery(MenuItem.class);
            Root<MenuItem> sourceEntity = criteria.from(MenuItem.class);
            criteria.select(sourceEntity);

            List<MenuItem> results = em.createQuery(criteria)
                    .setFirstResult(0)
                    .setMaxResults(GlobalValues.MAX_RESULT_SIZE_DB)
                    .getResultList();
            return results;
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    public TreeNode<MenuItem> buildMenuTree(long rootMenuItemId) throws DBConnectionException{
        //may not be necessary
        if (em == null || !em.isOpen()) {
            em = hibernateDB.getEM();
        }
        
        List<MenuItem> allMenuItems = this.getAllMenuItems();
        return null;
    }
}
