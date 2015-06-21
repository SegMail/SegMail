/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.navigation;

import java.util.Comparator;

/**
 *
 * @author LeeKiatHaw
 */
public class MenuItemAccessComparator implements Comparator<MenuItemAccess>{

    @Override
    public int compare(MenuItemAccess o1, MenuItemAccess o2) {
        return o1.getMENU_ORDER()- o2.getMENU_ORDER();
    }
    
}
