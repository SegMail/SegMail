/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.navigation;

import eds.entity.data.EnterpriseObject;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 *
 * @author KH
 */
@Entity
@Table(name="MENUITEM")
public class MenuItem extends EnterpriseObject{// implements TreeBranch<MenuItem> {

    public static enum TARGET_TYPE{
        URL,
        PROGRAM
    }

    private TARGET_TYPE MENU_ITEM_TYPE;
    private String MENU_ITEM_NAME; //display name
    private String MENU_ITEM_URL; //request URL
    private String PREPEND_TAGS;
    private int WEIGHT; // Optional for sorting
    private boolean PUBLIC;

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

    @Enumerated(EnumType.STRING)
    public TARGET_TYPE getMENU_ITEM_TYPE() {
        return MENU_ITEM_TYPE;
    }

    public void setMENU_ITEM_TYPE(TARGET_TYPE MENU_ITEM_TYPE) {
        this.MENU_ITEM_TYPE = MENU_ITEM_TYPE;
    }

    public String getPREPEND_TAGS() {
        return PREPEND_TAGS;
    }

    public void setPREPEND_TAGS(String PREPEND_TAGS) {
        this.PREPEND_TAGS = PREPEND_TAGS;
    }

    public int getWEIGHT() {
        return WEIGHT;
    }

    public void setWEIGHT(int WEIGHT) {
        this.WEIGHT = WEIGHT;
    }

    public boolean isPUBLIC() {
        return PUBLIC;
    }

    public void setPUBLIC(boolean PUBLIC) {
        this.PUBLIC = PUBLIC;
    }

    @Override
    public String alias() {
        return this.MENU_ITEM_NAME;
    }
    
    
    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
