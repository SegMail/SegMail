/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.navigation;

import eds.entity.data.EnterpriseRelationship;
import eds.entity.user.UserType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

/**
 *
 * @author KH
 */
@Entity
@Table(name="MENUITEM_ACCESS")
@EntityListeners({})
public class MenuItemAccess extends EnterpriseRelationship<MenuItem,UserType> {
    
    private int MENU_ORDER;
    
    /**
     * Optional grouping such that you can define many different type of menu
     * eg. "Top menu", "Left sidebar", "Footer menu", "Social media menu", etc.
     */
    private String MENU_GROUP;

    public MenuItemAccess() {
    }

    public MenuItemAccess(MenuItem s, UserType t, int order,String group) {
        super(s, t);
        this.MENU_ORDER = order;
        this.MENU_GROUP = group;
    }
    
    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getMENU_ORDER() {
        return MENU_ORDER;
    }

    public void setMENU_ORDER(int MENU_ORDER) {
        this.MENU_ORDER = MENU_ORDER;
    }

    public String getMENU_GROUP() {
        return MENU_GROUP;
    }

    public void setMENU_GROUP(String MENU_GROUP) {
        this.MENU_GROUP = MENU_GROUP;
    }
}
