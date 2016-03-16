/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.landing;

import eds.component.data.EntityNotFoundException;
import eds.entity.user.UserAccount;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.landing.LandingService;
import segmail.entity.landing.Assign_Server_User;
import segmail.entity.landing.ServerInstance;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormEditExistingServer")
public class FormEditExistingServer {
    
    @Inject ProgramLanding program;
    
    @EJB LandingService landingService;
    
    @PostConstruct
    public void init(){
        
    }
    
    public void saveTemplateAndContinue(){
        try {
            landingService.saveServer(this.getServerEditing());
            landingService.assignUserToServer(this.getUserId(), this.getServerEditing().getOBJECTID());
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Server saved.", "");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }
    
    public void saveTemplateAndClose(){
        try {
            landingService.saveServer(this.getServerEditing());
            landingService.assignUserToServer(this.getUserId(), this.getServerEditing().getOBJECTID());
            
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Server saved.", "");
            program.refresh();
            
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }
    
    public void closeWithoutSaving(){
        program.refresh();
    }
    
    public void deleteTemplate(){
        try {
            landingService.deleteServer(this.getServerEditing().getOBJECTID());
            
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Server deleted.", "");
            program.refresh();
            
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }
    
    public ServerInstance getServerEditing() {
        return program.getServerEditing();
    }

    public void setServerEditing(ServerInstance editingServer) {
        program.setServerEditing(editingServer);
    }
    
    public Assign_Server_User getAssignment() {
        return program.getAssignment();
    }

    public void setAssignment(Assign_Server_User assignment) {
        program.setAssignment(assignment);
    }
    
    public long getUserId() {
        return program.getUserIdExisting();
    }

    public void setUserId(long userId) {
        program.setUserIdExisting(userId);
    }
    
    public List<UserAccount> getUserAccounts() {
        return program.getUserAccounts();
    }

    public void setUserAccounts(List<UserAccount> userAccounts) {
        program.setUserAccounts(userAccounts);
    }
}
