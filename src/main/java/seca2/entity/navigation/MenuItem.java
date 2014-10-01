/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.entity.navigation;

import EDS.Entity.EnterpriseObject;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

/**
 *
 * @author KH
 */
@Entity
@Table(name="MENUITEM")
public class MenuItem extends EnterpriseObject {

    private String MENU_ITEM_NAME; //display name
    private String MENU_ITEM_URL; //request URL
    private String MENU_ITEM_XHTML; //actual layout.xhtml path
    private MenuItem PARENT_MENU_ITEM;

    public String getMENU_ITEM_NAME() {
        return MENU_ITEM_NAME;
    }

    public void setMENU_ITEM_NAME(String MENU_ITEM_NAME) {
        this.MENU_ITEM_NAME = MENU_ITEM_NAME;
    }

    public String getMENU_ITEM_URL() {
        return MENU_ITEM_URL;
    }

    public void setMENU_ITEM_URL(String MENU_ITEM_URL) {
        this.MENU_ITEM_URL = MENU_ITEM_URL;
    }

    public String getMENU_ITEM_XHTML() {
        return MENU_ITEM_XHTML;
    }

    public void setMENU_ITEM_XHTML(String MENU_ITEM_XHTML) {
        this.MENU_ITEM_XHTML = MENU_ITEM_XHTML;
    }

    @ManyToOne
    public MenuItem getPARENT_MENU_ITEM() {
        return PARENT_MENU_ITEM;
    }

    public void setPARENT_MENU_ITEM(MenuItem PARENT_MENU_ITEM) {
        this.PARENT_MENU_ITEM = PARENT_MENU_ITEM;
    }

    
    
    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @PrePersist
    @PreUpdate
    public void PrePersist(){
        this.OBJECT_NAME = this.MENU_ITEM_NAME;
    }
    
    
}
