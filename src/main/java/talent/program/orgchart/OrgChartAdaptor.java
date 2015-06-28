/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.program.orgchart;

import MapAPI.EntityMap;
import eds.component.data.DBConnectionException;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.bootstrap.module.User.UserContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import talent.component.organization.OrgService;
import talent.entity.organization.BelongsTo;
import talent.entity.organization.BusinessUnit;

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
    
    public final String formName = "OrgChartAdaptor";
    
    @PostConstruct
    public void init(){
        if(!FacesContext.getCurrentInstance().isPostback()){
            initOrgChartMap();
        }
    }
    
    public void initOrgChartMap(){
        try {
            program.setOrgChartMap(orgService.buildOrgChartForClient(clientContainer.getClient().getOBJECTID()));
        } catch(DBConnectionException ex){
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }
    
}
