/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.server;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.component.landing.LandingService;
import seca2.entity.landing.ServerInstance;
import seca2.entity.landing.ServerResource;
import seca2.entity.landing.ServerResourceType;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormServerList")
public class FormServerList {
    
    @Inject private ProgramServer program;
    
    @EJB LandingService landingService;
    
    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (!fc.isPostback()) {
            initServerList();
        }
    }
    
    public void initServerList() {
        try {
            program.setServers(landingService.getServerInstances());
        } catch (EJBException ex) { //Transaction did not go through
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        }
    }
    
    public void loadServerAndUserAccount(long serverId) {
        try {
            program.setServerEditing(landingService.getServerInstance(serverId));
            program.setAssignment(landingService.getServerUserAssignment(serverId));
            program.setUserIdExisting(program.getAssignment().getTARGET().getOBJECTID());
        } catch (EJBException ex) { //Transaction did not go through
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        }
    }
    
    public void loadServer(long serverId) {
        try {
            program.setServerEditing(landingService.getServerInstance(serverId));
            program.setJMSConnection(landingService.getServerJMSConnection(serverId));
            //program.setShowEditingPanel(true);
        } catch (EJBException ex) { //Transaction did not go through
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        }
    }
    
    
    public List<ServerInstance> getServerList(){
        return program.getServers();
    }
    
    public ServerInstance getServerEditing() {
        return program.getServerEditing();
    }

    public void setServerEditing(ServerInstance serverEditing) {
        program.setServerEditing(serverEditing);
    }
    
    
}
