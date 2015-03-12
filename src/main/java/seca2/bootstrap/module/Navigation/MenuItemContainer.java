/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.bootstrap.module.Navigation;

import eds.entity.navigation.MenuItem;

/**
 * Aggregation class
 * @author vincent.a.lee
 */
public class MenuItemContainer {
    
    private MenuItem menuItem;
    private boolean active;
    
    public String getURL(){
        return menuItem.getMENU_ITEM_URL();
    }
    
    public String getName(){
        return menuItem.getMENU_ITEM_NAME();
    }
    
    public String getPrependTags(){
        return menuItem.getPREPEND_TAGS();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }
    
    
}
