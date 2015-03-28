/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.navigation;

import eds.entity.EnterpriseObject_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import eds.entity.program.Program;

/**
 *
 * @author KH
 */
@StaticMetamodel(MenuItem.class)
public class MenuItem_ extends EnterpriseObject_{
    public static volatile SingularAttribute<MenuItem,String> MENU_ITEM_NAME;
    public static volatile SingularAttribute<MenuItem,Program> LINKED_PROGRAM;
}
