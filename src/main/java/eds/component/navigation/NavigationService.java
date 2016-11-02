/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.navigation;

import TreeAPI.TreeBranch;
import eds.component.GenericObjectService;
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
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
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

    @EJB private UserService userService;
    @EJB private GenericObjectService objectService;
    
    @PersistenceContext(name="HIBERNATE")
    private EntityManager em;

    /**
     * If parentMenunItemId is negative, not found, create the menu item as root.
     * 
     * @param name
     * @param requestUrl
     * @param xhtml
     * @param prependHTMLTags
     * @param isPublic
     * @param parentMenuItemId
     * @return
     * @throws eds.component.data.IncompleteDataException
     * @throws eds.component.data.EntityExistsException
     * @throws CreateMenuItemException
     * @throws DBConnectionException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public MenuItem createMenuItem(String name, String requestUrl, long parentMenuItemId, String prependHTMLTags, boolean isPublic) 
            throws IncompleteDataException, EntityExistsException {
        
        try {
            if(name == null || name.length() <= 0)
                throw new IncompleteDataException("MenuItem name cannot be empty!");
            if(requestUrl == null || requestUrl.length() <= 0)
                throw new IncompleteDataException("MenuItem URL cannot be empty!");
            
            List<MenuItem> existingMenuItems = getAllMenuItemsByName(name);
            if(existingMenuItems != null && !existingMenuItems.isEmpty())
                throw new EntityExistsException(existingMenuItems.get(0));
            
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
            newMenuItem.setMENU_ITEM_URL(requestUrl);
            newMenuItem.setPREPEND_TAGS(prependHTMLTags);
            newMenuItem.setPUBLIC(isPublic);
            //em.getTransaction().begin();
            em.persist(newMenuItem);
            //em.getTransaction().commit();
            
            return newMenuItem;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                //throw new DBConnectionException(pex.getCause().getMessage());
                throw new DBConnectionException();
            }
            throw pex;
        } 

        
    }

    /**
     * Do we want to assign all relationships as bidirectional?
     * 
     * Returns 
     * 
     * @param userTypeId
     * @param menuItemId
     * @param order
     * @return Bidirectional EnterpriseRelationship
     * @throws eds.component.data.RelationshipExistsException
     * @throws eds.component.data.EntityNotFoundException
     * 
     * @throws AssignMenuItemAccessException
     * @throws DBConnectionException 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<MenuItemAccess> assignMenuItemAccess(long userTypeId, long menuItemId, int order) 
            throws RelationshipExistsException, EntityNotFoundException {
        
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
            List<MenuItemAccess> results = this.objectService.getRelationshipsForObject(menuItemId, userTypeId, MenuItemAccess.class);
            
            if(results != null && results.size() > 0){
                MenuItemAccess first = results.get(0);
                throw new RelationshipExistsException(first);
            }
            
            //get menuItem first
            MenuItem assignMenuItem = this.getMenuItemById(menuItemId);
            if(assignMenuItem == null)
                throw new EntityNotFoundException(MenuItem.class,menuItemId);
            
            //get userType
            UserType assignUserType = userService.getUserTypeById(userTypeId);
            if(assignUserType == null)
                throw new EntityNotFoundException(UserType.class,menuItemId);
            
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
        } 
    }
    
    
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

    
    public List<MenuItem> getAllMenuItems() throws DBConnectionException {
        
        try {
            List<MenuItem> results = this.objectService.getAllEnterpriseObjects(MenuItem.class);
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
    
    
    public TreeBranch<MenuItem> buildMenuTree(long rootMenuItemId) {
        
        
        List<MenuItem> allMenuItems = this.getAllMenuItems();
        return null;
    }
    
    /**
     * 
     * @param usertypeid
     * @return a list of all MenuItems assigned to the user type unordered, or 
     * an empty list if there is none found.
     */
    
    public List<MenuItem> getAllMenuItemsForUsertype(long usertypeid) {
        
        //List<MenuItem> results = this.objectService.getAllSourceObjectsFromTarget(usertypeid, MenuItemAccess.class, MenuItem.class);
        List<MenuItemAccess> access = this.objectService.getRelationshipsForTargetObject(usertypeid, MenuItemAccess.class);

        Collections.sort(access, new MenuItemAccessComparator());

        List<MenuItem> results = new ArrayList<MenuItem>();
        for(MenuItemAccess a : access){
            results.add(a.getSOURCE());
        }

        return results;
    }
    
    
    public List<MenuItem> getAllMenuItemsByName(String menuitemname) {
        
        try {
            List<MenuItem> results = this.objectService.getEnterpriseObjectsByName(menuitemname, MenuItem.class);
            
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
    
    
    public List<MenuItem> getAllPublicMenuItems(){
        try {
            CriteriaBuilder builder = objectService.getEm().getCriteriaBuilder();
            CriteriaQuery<MenuItem> criteria = builder.createQuery(MenuItem.class);
            Root<MenuItem> fromMenuItem = criteria.from(MenuItem.class);
            
            criteria.where(builder.isTrue(fromMenuItem.get(MenuItem_.PUBLIC)));
            
            List<MenuItem> results = objectService.getEm().createQuery(criteria)
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
    
}
