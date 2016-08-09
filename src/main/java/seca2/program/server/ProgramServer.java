/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.server;

import eds.component.user.UserService;
import eds.entity.user.UserAccount;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import seca2.bootstrap.UserRequestContainer;
import seca2.component.landing.ServerNodeType;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.Program;
import seca2.entity.landing.Assign_Server_User;
import seca2.entity.landing.ServerInstance;
import seca2.entity.landing.ServerResource;

/**
 *
 * @author LeeKiatHaw
 */
public class ProgramServer extends Program {
    
    @Inject private UserRequestContainer requestContainer;
    
    @EJB private UserService userService;
    
    //Shared variables
    private List<UserAccount> userAccounts;
    private List<ServerInstance> servers;
    private List<String> types = ServerNodeType.getNodeTypesString();
    
    //Add new form
    private String name;
    private String uri;
    private long userIdNew;
    private String serverNodeType;
    
    //Edit existing form
    private ServerInstance serverEditing;
    private Assign_Server_User assignment;
    private long userIdExisting;
    private ServerResource JMSConnection;
    
    //Edit full form Tabs
    private String tab;
    private boolean showEditingPanel;
    
    @Override
    public void initRequestParams() {
        this.setShowEditingPanel(false);
    }
    
    public void confirmSubscription() {
        
    }
    
    public void unsubscribe() {
        
    }

    @Override
    public void initProgram() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (!fc.isPostback()) {
            initUserAccounts();
        }
    }
    
    public void initUserAccounts() {
        try {
            setUserAccounts(userService.getWebServiceUserAccounts());
        } catch (EJBException ex) { 
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        }
    }

    public List<ServerInstance> getServers() {
        return servers;
    }

    public void setServers(List<ServerInstance> servers) {
        this.servers = servers;
    }

    public ServerInstance getServerEditing() {
        return serverEditing;
    }

    public void setServerEditing(ServerInstance serverEditing) {
        this.serverEditing = serverEditing;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getUserIdNew() {
        return userIdNew;
    }

    public void setUserIdNew(long userIdNew) {
        this.userIdNew = userIdNew;
    }

    public List<UserAccount> getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(List<UserAccount> userAccounts) {
        this.userAccounts = userAccounts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Assign_Server_User getAssignment() {
        return assignment;
    }

    public void setAssignment(Assign_Server_User assignment) {
        this.assignment = assignment;
    }

    public long getUserIdExisting() {
        return userIdExisting;
    }

    public void setUserIdExisting(long userIdExisting) {
        this.userIdExisting = userIdExisting;
    }

    public String getServerNodeType() {
        return serverNodeType;
    }

    public void setServerNodeType(String serverNodeType) {
        this.serverNodeType = serverNodeType;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public boolean isShowEditingPanel() {
        return showEditingPanel;
    }

    public void setShowEditingPanel(boolean showEditingPanel) {
        this.showEditingPanel = showEditingPanel;
    }

    public ServerResource getJMSConnection() {
        return JMSConnection;
    }

    public void setJMSConnection(ServerResource JMSConnection) {
        this.JMSConnection = JMSConnection;
    }

    @Override
    public void clearVariables() {
        
    }
    
    
}
