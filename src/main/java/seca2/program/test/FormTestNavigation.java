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
import eds.component.data.DBConnectionException;
import eds.component.navigation.AssignMenuItemAccessException;
import eds.component.navigation.CreateMenuItemException;
import eds.component.navigation.NavigationService;
import eds.component.user.UserService;
import eds.entity.navigation.MenuItem;
import eds.entity.navigation.MenuItemAccess;
import eds.entity.user.UserType;
import javax.inject.Inject;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author KH
 */
@Named("FormTestNavigation")
@RequestScoped
public class FormTestNavigation implements Serializable{
    
    @EJB private NavigationService navigationService;
    @EJB private UserService userService;
    
    @Inject private ProgramTest programTest;
    
    //Create MenuItem
    private long selectedParentMenuItemId;
    private String menuItemName;
    private String menuItemURL;
    private String prependHTMLTags;
    
    //Assign MenuItemAccess
    private long selectedAssignedMenuItemId;
    private long selectedUserTypeId;
    
    //Test custom selectonemenu tag
    private List<String> selectOneMenuTest;
    
    //Build Menu
    private long selectedUserTypeIdToBuildMenu;
    
    private final String setupNavigationFormName = "setupNavigationForm";
    
    @PostConstruct
    public void init(){
        
    }
    
    public void initializeSelectOneMenuTest(){
        selectOneMenuTest = new ArrayList();
        selectOneMenuTest.add("first");
        selectOneMenuTest.add("second");
        selectOneMenuTest.add("third");
    }
    
    public void createNewMenuItem(){
        
        try{
            //Thread.sleep(5000);//for testing ajax loader
            MenuItem newMenuItem = navigationService.createMenuItem(menuItemName, menuItemURL, selectedParentMenuItemId,prependHTMLTags);
            //if successful, reload the page
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_FATAL, "MenuItem "+newMenuItem.getMENU_ITEM_NAME()+" created successfully!", null);
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
        return this.programTest.getAllMenuItems();
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

    public long getSelectedAssignedMenuItemId() {
        return selectedAssignedMenuItemId;
    }

    public void setSelectedAssignedMenuItemId(long selectedAssignedMenuItemId) {
        this.selectedAssignedMenuItemId = selectedAssignedMenuItemId;
    }

    public List<UserType> getAllUserTypes() {
        return this.programTest.getAllUserTypes();
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

    public List<String> getSelectOneMenuTest() {
        return selectOneMenuTest;
    }

    public void setSelectOneMenuTest(List<String> selectOneMenuTest) {
        this.selectOneMenuTest = selectOneMenuTest;
    }

    public String getPrependHTMLTags() {
        return prependHTMLTags;
    }

    public void setPrependHTMLTags(String prependHTMLTags) {
        this.prependHTMLTags = prependHTMLTags;
    }

    
}
