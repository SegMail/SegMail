/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.program.orgchart;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import talent.component.organization.OrgService;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("OrgChartAdaptor")
public class OrgChartAdaptor {
    
    @EJB private OrgService orgService;
    
    
}
