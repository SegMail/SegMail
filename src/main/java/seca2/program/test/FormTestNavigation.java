/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import seca2.component.data.DBConnectionException;
import seca2.component.navigation.AssignMenuItemAccessException;
import seca2.component.navigation.CreateMenuItemException;
import seca2.component.navigation.NavigationService;
import seca2.component.user.UserService;
import seca2.entity.navigation.MenuItem;
import seca2.entity.navigation.MenuItemAccess;
import seca2.entity.user.UserType;
import seca2.program.messenger.FacesMessenger;

/**
 *
 * @author KH
 */
@Named("FormTestNavigation")
@RequestScoped
public class FormTestNavigation implements Serializable{
    
    @EJB private NavigationService navigationService;
    @EJB private UserService userService;
    
    private List<MenuItem> allMenuItems = new ArrayList<MenuItem>();
    private List<UserType> allUserTypes = new ArrayList<UserType>();
    
    //Create MenuItem
    private long selectedParentMenuItemId;
    private String menuItemName;
    private String menuItemURL;
    private String menuItemXHTML;
    
    //Assign MenuItemAccess
    private long selectedAssignedMenuItemId;
    private long selectedUserTypeId;
    
    //Build Menu
    private long selectedUserTypeIdToBuildMenu;
    
    private final String setupNavigationFormName = "setupNavigationForm";
    
    @PostConstruct
    public void init(){
        initializeAllMenuItems();
        initializeAllUserTypes();
    }
    
    public void initializeAllMenuItems(){
        try{
            allMenuItems = navigationService.getAllMenuItems();
            //who knows whether there is empty list or not?
        }
        catch(DBConnectionException dbex){
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        }
        catch(Exception ex){
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_ERROR, 
                    ex.getCause().getClass().getSimpleName(), 
                    ex.getCause().getMessage());
        }
    }
    
    public void initializeAllUserTypes(){
         try{
            allUserTypes = userService.getAllUserTypes();
            //who knows whether there is empty list or not?
        }
        catch(DBConnectionException dbex){
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        }
        catch(Exception ex){
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_ERROR, 
                    ex.getCause().getClass().getSimpleName(), 
                    ex.getCause().getMessage());
        }
    }
    
    public void createNewMenuItem(){
        
        try{
            //Thread.sleep(5000);//for testing ajax loader
            MenuItem newMenuItem = navigationService.createMenuItem(menuItemName, menuItemURL, menuItemXHTML, selectedParentMenuItemId);
            //if successful, reload the page
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_FATAL, "MenuItem "+newMenuItem.getMENU_ITEM_NAME()+" created successfully!", null);
            this.initializeAllMenuItems();
        }
        catch(DBConnectionException dbex){
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        }
        catch(CreateMenuItemException crmex){
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_ERROR, "Create menu exception.", crmex.getMessage());
        }
        catch(Exception ex){
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_ERROR,
                    ex.getCause().getClass().getSimpleName(), 
                    ex.getCause().getMessage());
        }
    }
    
    public void assignMenuAccess(){
        try{
            List<MenuItemAccess> biRel = navigationService.assignMenuItemAccess(selectedUserTypeId, selectedAssignedMenuItemId);
            
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_FATAL, "MenuItem "+selectedAssignedMenuItemId+" is assigned to user type "+selectedUserTypeId+"!", null);
        }
        catch(DBConnectionException dbex){
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        }
        catch(AssignMenuItemAccessException crmex){
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_ERROR, "Create menu exception.", crmex.getMessage());
        }
        catch(Exception ex){
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_ERROR,
                    ex.getCause().getClass().getSimpleName(), 
                    ex.getCause().getMessage());
        }
    }
    
    public void buildMenu(){
        try{
            navigationService.buildMenuForUserType(selectedUserTypeIdToBuildMenu);
        }
        catch(DBConnectionException dbex){
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        }
        catch(Exception ex){
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_ERROR,
                    ex.getCause().getClass().getSimpleName(), 
                    ex.getCause().getMessage());
        }
    }

    public List<MenuItem> getAllMenuItems() {
        return allMenuItems;
    }

    public void setAllMenuItems(List<MenuItem> allMenuItems) {
        this.allMenuItems = allMenuItems;
    }

    public long getSelectedParentMenuItemId() {
        return selectedParentMenuItemId;
    }

    public void setSelectedParentMenuItemId(long selectedParentMenuItemId) {
        this.selectedParentMenuItemId = selectedParentMenuItemId;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public String getMenuItemURL() {
        return menuItemURL;
    }

    public void setMenuItemURL(String menuItemURL) {
        this.menuItemURL = menuItemURL;
    }

    public String getMenuItemXHTML() {
        return menuItemXHTML;
    }

    public void setMenuItemXHTML(String menuItemXHTML) {
        this.menuItemXHTML = menuItemXHTML;
    }

    public long getSelectedAssignedMenuItemId() {
        return selectedAssignedMenuItemId;
    }

    public void setSelectedAssignedMenuItemId(long selectedAssignedMenuItemId) {
        this.selectedAssignedMenuItemId = selectedAssignedMenuItemId;
    }

    public List<UserType> getAllUserTypes() {
        return allUserTypes;
    }

    public void setAllUserTypes(List<UserType> allUserTypes) {
        this.allUserTypes = allUserTypes;
    }

    public long getSelectedUserTypeId() {
        return selectedUserTypeId;
    }

    public void setSelectedUserTypeId(long selectedUserTypeId) {
        this.selectedUserTypeId = selectedUserTypeId;
    }

    public long getSelectedUserTypeIdToBuildMenu() {
        return selectedUserTypeIdToBuildMenu;
    }

    public void setSelectedUserTypeIdToBuildMenu(long selectedUserTypeIdToBuildMenu) {
        this.selectedUserTypeIdToBuildMenu = selectedUserTypeIdToBuildMenu;
    }

    
}
