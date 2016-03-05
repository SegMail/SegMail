/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.bootstrap.module.Navigation;

import eds.entity.navigation.MenuItem;
import java.util.Objects;
import segurl.filter.SegURLResolver;

/**
 * Wrapper class
 * @author vincent.a.lee
 */
public class MenuItemContainer {
    
    private String contextPath;
    
    private MenuItem menuItem;
    private boolean active;
    
    public String getURL(){
        //If the URL of the MenuItem has a "/" prepended or context path has 
        //a "/" appended, remove them first
        String contextPathTrimmed = this.contextPath;
        if(contextPathTrimmed.endsWith("/")){
            contextPathTrimmed = contextPathTrimmed.substring(0,contextPathTrimmed.length()-1);
        }
        String url = menuItem.getMENU_ITEM_URL();
        if(url.startsWith("/")){
            url = url.substring(1, url.length());
        }
        
        return contextPathTrimmed+"/"+url;
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

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
    
    public boolean containsProgram(String programName){
        return (SegURLResolver.getResolver().resolveProgramName(this.menuItem.getMENU_ITEM_URL())
                .equalsIgnoreCase(programName));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.menuItem);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MenuItemContainer other = (MenuItemContainer) obj;
        if (!Objects.equals(this.menuItem, other.menuItem)) {
            return false;
        }
        return true;
    }
    
    
}
