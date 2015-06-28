/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.navigation;

import TreeAPI.TreeBranch;
import TreeAPI.TreeBuilder;
import TreeAPI.TreeNode;
import eds.component.GenericEnterpriseObjectService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.exception.GenericJDBCException;
import eds.component.data.DBConnectionException;
import eds.component.user.UserService;
import eds.entity.navigation.MenuItem;
import eds.entity.navigation.MenuItemAccess;
import eds.entity.navigation.MenuItemAccessComparator;
import eds.entity.navigation.MenuItemComparator;
import eds.entity.navigation.MenuItem_;
import eds.entity.user.UserType;
import java.util.Collections;

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
    private UserService userService;
    @EJB private GenericEnterpriseObjectService genericEnterpriseObjectService;
    
    @PersistenceContext(name="HIBERNATE")
    private EntityManager em;

    /**
     * 
     * Transitivity: Given a tree a->b->c, if userType has access to only a and b
     * but not b, then this function will not return c as part of the menu tree.
     * 
     * @param userType
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<TreeNode> buildMenuForUserType(long userTypeId) throws DBConnectionException {
        
        try{
            //1. Get all MenuItemAccess by userType ID.
            //This uses EnterpriseRelationship to pull both MenuItem and UserType objects instead of using MenuItem to pull
            //both MenuItemAccess and UserType. This is because there is no mapping for EnterpriseObject->EnterpriseRelationship
            /*CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<MenuItemAccess> criteria = builder.createQuery(MenuItemAccess.class);
            Root<MenuItemAccess> menuItem = criteria.from(MenuItemAccess.class);
            criteria.select(menuItem);
            Join<MenuItemAccess,EnterpriseObject> userType = menuItem.join(MenuItemAccess_.SOURCE);
            
            criteria.where(builder.equal(userType.get(EnterpriseObject_.OBJECTID), userTypeId));

            List<MenuItemAccess> results = em.createQuery(criteria)
                    //.setFirstResult(0)
                    //.setMaxResults(GlobalValues.MAX_RESULT_SIZE_DB) //not necessary yet!
                    .getResultList();*/
            
            List<MenuItemAccess> results = this.genericEnterpriseObjectService.getRelationshipsForSourceObject(userTypeId, MenuItemAccess.class);
            
            /**
             * Aborted as it is too complex to query all accessible MenuItems first then build the
             * tree from there. Instead, why not just select
             */
            //2. Iterate through MenuItemAccess list in ascending order of parent and build the tree from root
            List<TreeBranch> menuItems = new ArrayList<>();
            System.out.println("Before sort...");
            for(MenuItemAccess mia:results){
                menuItems.add(mia.getSOURCE());
                System.out.println("MenuItem: "+mia.getSOURCE()+"    Parent: "+mia.getSOURCE().getPARENT_MENU_ITEM());
            }
            
            List<TreeNode> sortedRoots = TreeBuilder.buildTreeByParentBruteForce(menuItems);
            
            //2a. Decide which node to be the root...
            // Ideally, each menu can only have 1 root.
            // How about a menu object as the root?
            System.out.println();
            return sortedRoots;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
        
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
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public MenuItem createMenuItem(String name, String requestUrl, long parentMenuItemId, String prependHTMLTags)
            throws CreateMenuItemException, DBConnectionException {
        
        try {
            if(name == null || name.length() <= 0)
                throw new CreateMenuItemException("MenuItem name cannot be empty!");
            if(requestUrl == null || requestUrl.length() <= 0)
                throw new CreateMenuItemException("MenuItem URL cannot be empty!");
            
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
            newMenuItem.setPREPEND_TAGS(prependHTMLTags);
            //em.getTransaction().begin();
            em.persist(newMenuItem);
            //em.getTransaction().commit();
            
            return newMenuItem;
            
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

    /**
     * Do we want to assign all relationships as bidirectional?
     * 
     * Returns 
     * 
     * @param userTypeId
     * @param menuItemId
     * @return Bidirectional EnterpriseRelationship
     * 
     * @throws AssignMenuItemAccessException
     * @throws DBConnectionException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<MenuItemAccess> assignMenuItemAccess(long userTypeId, long menuItemId, int order) throws AssignMenuItemAccessException, DBConnectionException {
        
        try{
            /*//check if userType already has access to menuItem
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<MenuItemAccess> criteria = builder.createQuery(MenuItemAccess.class); //AS criteria
            Root<MenuItemAccess> menuItemAccess = criteria.from(MenuItemAccess.class); //FROM MenuItemAccess
            
            Join<MenuItemAccess,EnterpriseObject> menuItem = menuItemAccess.join(MenuItemAccess_.SOURCE); //FROM EnterpriseObject
            //criteria.where(builder.equal(menuItem.get(EnterpriseObject_.OBJECTID), menuItemId)); //WHERE MenuItemAccess.SOURCE.OBJECTID = menuItemId
            Join<MenuItemAccess,EnterpriseObject> userType = menuItemAccess.join(MenuItemAccess_.TARGET); //FROM EnterpriseObject
            //criteria.where(builder.equal(userType.get(EnterpriseObject_.OBJECTID), userTypeId)); //WHERE MenuItemAccess.TARGET.OBJECTID = userTypeId
            criteria.where(builder.and(builder.equal(menuItem.get(EnterpriseObject_.OBJECTID), menuItemId),//WHERE MenuItemAccess.SOURCE.OBJECTID = menuItemId
                           builder.equal(userType.get(EnterpriseObject_.OBJECTID), userTypeId)));  // AND MenuItemAccess.TARGET.OBJECTID = userTypeId
            
            List<MenuItemAccess> results = em.createQuery(criteria)
                    .getResultList();*/
            List<MenuItemAccess> results = this.genericEnterpriseObjectService.getRelationshipsForObject(userTypeId, menuItemId, MenuItemAccess.class);
            
            if(results != null && results.size() > 0){
                MenuItemAccess first = results.get(0);
                throw new AssignMenuItemAccessException("Menu Item "+first.getSOURCE().getMENU_ITEM_NAME()+" is already assigned "
                        + "to UserType "+first.getTARGET().getUSERTYPENAME());
            }
            
            //get menuItem first
            MenuItem assignMenuItem = this.getMenuItemById(menuItemId);
            if(assignMenuItem == null)
                throw new AssignMenuItemAccessException("MenuItem with ID "+menuItemId+" could not be found!");
            
            //get userType
            UserType assignUserType = userService.getUserTypeById(userTypeId);
            if(assignUserType == null)
                throw new AssignMenuItemAccessException("UserType with ID "+userTypeId+" could not be found!");
            
            //Create the MenuItemAccess objects (bi-directional)
            //MenuItemAccess assignment1 = new MenuItemAccess();
            //assignment1.setSOURCE(assignUserType);
            //assignment1.setTARGET(assignMenuItem);
            
            MenuItemAccess assignment2 = new MenuItemAccess(assignMenuItem,assignUserType,order);
            //assignment2.setSOURCE(assignMenuItem);
            //assignment2.setTARGET(assignUserType);
            
            //em.getTransaction().begin();
            //em.persist(assignment1);
            em.persist(assignment2);
            //em.getTransaction().commit();
            
            List<MenuItemAccess> biRel = new ArrayList<MenuItemAccess>();
            //biRel.add(assignment1);
            biRel.add(assignment2);
            
            return biRel;
            
        }catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public MenuItem getMenuItemById(long menuItemId) throws DBConnectionException{
        
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<MenuItem> criteria = builder.createQuery(MenuItem.class);
            Root<MenuItem> sourceEntity = criteria.from(MenuItem.class);
            criteria.select(sourceEntity);
            criteria.where(builder.equal(sourceEntity.get(MenuItem_.OBJECTID), menuItemId));

            MenuItem result = em.createQuery(criteria)
                    .getSingleResult();
            return result;
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            } 
            if (pex instanceof NoResultException){
                return null;
            }
            throw pex;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<MenuItem> getAllMenuItems() throws DBConnectionException {
        
        try {
            /*CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<MenuItem> criteria = builder.createQuery(MenuItem.class);
            Root<MenuItem> sourceEntity = criteria.from(MenuItem.class);
            criteria.select(sourceEntity);

            List<MenuItem> results = em.createQuery(criteria)
                    //.setFirstResult(0)
                    //.setMaxResults(GlobalValues.MAX_RESULT_SIZE_DB) //not necessary yet!
                    .getResultList();
            */
            List<MenuItem> results = this.genericEnterpriseObjectService.getAllEnterpriseObjects(MenuItem.class);
            Collections.sort(results, new MenuItemComparator());
            
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
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public TreeBranch<MenuItem> buildMenuTree(long rootMenuItemId) throws DBConnectionException{
        
        
        List<MenuItem> allMenuItems = this.getAllMenuItems();
        return null;
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<MenuItem> getAllMenuItemsForUsertype(long usertypeid) throws DBConnectionException {
        
        try {
            //List<MenuItem> results = this.genericEnterpriseObjectService.getAllSourceObjectsFromTarget(usertypeid, MenuItemAccess.class, MenuItem.class);
            List<MenuItemAccess> access = this.genericEnterpriseObjectService.getRelationshipsForTargetObject(usertypeid, MenuItemAccess.class);
            
            Collections.sort(access, new MenuItemAccessComparator());
            
            List<MenuItem> results = new ArrayList<MenuItem>();
            for(MenuItemAccess a : access){
                results.add(a.getSOURCE());
            }
            
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
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<MenuItem> getAllMenuItemsByName(String menuitemname) throws DBConnectionException {
        
        try {
            List<MenuItem> results = this.genericEnterpriseObjectService.getEnterpriseObjectsByName(menuitemname, MenuItem.class);
            
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
    
}
