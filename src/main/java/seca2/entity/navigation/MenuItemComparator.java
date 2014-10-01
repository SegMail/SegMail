/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.entity.navigation;

import java.util.Comparator;

/**
 *
 * @author KH
 */
public class MenuItemComparator implements Comparator<MenuItem> {

    @Override
    public int compare(MenuItem o1, MenuItem o2) {
        
        int compareOwnIds = o1.compareTo(o2);
        int compareParentIds = o1.getPARENT_MENU_ITEM().compareTo(o2.getPARENT_MENU_ITEM());
        
        if(compareParentIds != 0) return compareOwnIds;
        return compareOwnIds;
    }
    
}
