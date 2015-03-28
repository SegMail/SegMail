/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.navigation;

import java.util.Comparator;

/**
 *
 * @author KH
 */
public class MenuItemComparator implements Comparator<MenuItem> {

    @Override
    public int compare(MenuItem o1, MenuItem o2) {
        
        /**
         * Takes care of all null scenarios.
         */
        if(o1 == null) return (o2 == null ? 0 : -1);
        
        if(o2 == null) return 1;
        
        int compareOwnIds = o1.compareTo(o2);
        
        int compareParentIds = compare(o1.getPARENT_MENU_ITEM(),o2.getPARENT_MENU_ITEM());
        
        if(compareParentIds != 0){ 
            System.out.println("MenuItem: "+o1+" ==="+compareParentIds+"=== MenuItem: "+o2);
            return compareParentIds;
        } //sort by parents first
        System.out.println("MenuItem: "+o1+" ==="+compareOwnIds+"=== MenuItem: "+o2);
        return compareOwnIds; //sort by its own objectId
    }
    
}
