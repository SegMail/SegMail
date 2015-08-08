/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.program.orgchart;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import talent.component.organization.OrgService;
import talent.entity.organization.OrgUnit;
import talent.entity.organization.Position;
import talent.entity.talent.Employee;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormCreateNewOrgPos")
public class FormCreateNewOrgPos {
    
    @EJB private OrgService orgService;
    @Inject private ClientContainer clientContainer;
    
    private String role;
    private String unit ;
    private long holder;
    
    private final String formName = "create_org_pos_form";
    
    @PostConstruct
    public void init(){
        role = "";
        unit = "";
        holder = 0;
    }
    
    public void createNewOrgPos(){
        try {
            orgService.createNewOrgPos(role, unit, holder, clientContainer.getClient().getOBJECTID());
        } catch (EJBException ex) { //Transaction did not go through
            Throwable cause = ex.getCause();
            String message = "Don't know what happened!";
            if(cause != null) message = cause.getMessage();
            
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, message, null);
            
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public long getHolder() {
        return holder;
    }

    public void setHolder(long holder) {
        this.holder = holder;
    }

    
    
}
