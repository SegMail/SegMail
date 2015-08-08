/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.program.orgchart;

import GraphAPI.EntityGraph;
import eds.component.data.DBConnectionException;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import talent.component.organization.OrgService;
import talent.entity.organization.BelongsTo;
import talent.entity.organization.OrgUnit;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("OrgChartAdaptor")
public class OrgChartAdaptor {
    
    @EJB private OrgService orgService;
    
    @Inject private ClientContainer clientContainer;
    
    @Inject private ProgramOrgChart program;
    
    private final String formName = "OrgChartAdaptor";
    
}
