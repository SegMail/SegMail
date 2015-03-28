/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.layout;

import eds.entity.EnterpriseObject_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import eds.entity.program.Program;

/**
 *
 * @author KH
 */
@StaticMetamodel(Layout.class)
public class Layout_ extends EnterpriseObject_{
    public static volatile SingularAttribute<Layout,String> LAYOUT_NAME;
    public static volatile SingularAttribute<Layout,Program> VIEW_ROOT;
}
