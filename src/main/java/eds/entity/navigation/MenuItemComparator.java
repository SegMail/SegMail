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
        return o1.getWEIGHT() - o1.getWEIGHT();
    }
}
