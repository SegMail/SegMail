/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.server;

import eds.component.data.EntityNotFoundException;
import eds.entity.user.UserAccount;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.component.landing.LandingService;
import seca2.entity.landing.Assign_Server_User;
import seca2.entity.landing.ServerInstance;
import seca2.entity.landing.ServerResource;
import seca2.program.FormEditEntity;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormEditExistingServer")
public class FormEditExistingServer implements FormEditEntity {
    
    @Inject ProgramServer program;
    
    @EJB LandingService landingService;
    
    @PostConstruct
    public void init(){
        
    }
    
    @Override
    public void saveAndContinue(){
        try {
            ServerInstance server = getServerEditing();
            server = landingService.saveServer(server);
            setServerEditing(server);
            //Save server to user relationship
            landingService.assignUserToServer(getUserId(), getServerEditing().getOBJECTID());
            
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Server saved.", "");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }
    
    @Override
    public void saveAndClose(){
        saveAndContinue();
        closeWithoutSaving();
    }
    
    @Override
    public void closeWithoutSaving(){
        program.refresh();
    }
    
    @Override
    public void delete(){
        try {
            landingService.deleteServer(this.getServerEditing().getOBJECTID());
            
            FacesMessenger.setFacesMessage(ProgramServer.class.getSimpleName(), FacesMessage.SEVERITY_FATAL, "Server deleted.", "");
            closeWithoutSaving();
            
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
    
    public String getServerNodeType() {
        return program.getServerNodeType();
    }

    public void setServerNodeType(String serverNodeType) {
        program.setServerNodeType(serverNodeType);
    }
    
    public List<String> getTypes() {
        return program.getTypes();
    }

    public void setTypes(List<String> types) {
        program.setTypes(types);
    }
    
    public boolean renderEditingPanel(){
        return program.isShowEditingPanel();
    }
    
    public String getTab() {
        return program.getTab();
    }

    public void setTab(String tab) {
        program.setTab(tab);
    }
    
    public String getTabPath(){
        String tab = getTab();
        return (tab == null || tab.isEmpty()) ? "" : "edit_panels/"+tab+".xhtml";
    }
    
    public void changeTab(String newTab){
        this.setTab(newTab);
    }
    
    public ServerResource getJMSConnection() {
        return program.getJMSConnection();
    }

    public void setJMSConnection(ServerResource JMSConnection) {
        program.setJMSConnection(JMSConnection);
    }
}
