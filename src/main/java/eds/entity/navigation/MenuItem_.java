/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.navigation;

import eds.entity.data.EnterpriseObject_;
import eds.entity.navigation.MenuItem.TARGET_TYPE;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author KH
 */
@StaticMetamodel(MenuItem.class)
public class MenuItem_ extends EnterpriseObject_{
    
    public static volatile SingularAttribute<MenuItem,TARGET_TYPE> MENU_ITEM_TYPE;
    public static volatile SingularAttribute<MenuItem,String> MENU_ITEM_NAME; //display name
    public static volatile SingularAttribute<MenuItem,String> MENU_ITEM_URL; //request URL
    public static volatile SingularAttribute<MenuItem,String> PREPEND_TAGS;
    public static volatile SingularAttribute<MenuItem,Integer> WEIGHT; 
    public static volatile SingularAttribute<MenuItem,MenuItem> PARENT_MENU_ITEM;
    public static volatile SingularAttribute<MenuItem,Boolean> PUBLIC;
}
