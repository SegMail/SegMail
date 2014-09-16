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
import seca2.component.navigation.CreateMenuItemException;
import seca2.component.navigation.NavigationService;
import seca2.entity.navigation.MenuItem;
import seca2.program.messenger.FacesMessenger;

/**
 *
 * @author KH
 */
@Named("FormTestNavigation")
@RequestScoped
public class FormTestNavigation implements Serializable{
    
    @EJB private NavigationService navigationService;
    
    private List<MenuItem> allMenuItems = new ArrayList<MenuItem>();
    private long selectedParentMenuItemId;
    private String menuItemName;
    private String menuItemURL;
    private String menuItemXHTML;
    
    private final String setupNavigationFormName = "setupNavigationForm";
    
    @PostConstruct
    public void init(){
        initializeAllMenuItems();
    }
    
    public void initializeAllMenuItems(){
        try{
            allMenuItems = navigationService.getAllMenuItems();
        }
        catch(DBConnectionException dbex){
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        }
        catch(CreateMenuItemException crex){
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_ERROR, crex.getClass().getSimpleName(), crex.getMessage());
        }
        catch(Exception ex){
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void createNewMenuItem(){
        
        try{
            //Thread.sleep(5000);//for testing ajax loader
            navigationService.createMenuItem(menuItemName, menuItemURL, menuItemXHTML, selectedParentMenuItemId);
            //if successful, reload the page
            this.initializeAllMenuItems();
        }
        catch(DBConnectionException dbex){
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        }
        catch(CreateMenuItemException crmex){
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_ERROR, "Create menu exception.", crmex.getMessage());
        }
        catch(Exception ex){
            FacesMessenger.setFacesMessage(setupNavigationFormName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void buildMenu(){
        
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

    
}
