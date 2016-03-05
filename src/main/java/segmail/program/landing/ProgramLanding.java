/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.landing;

import eds.entity.user.UserAccount;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import seca2.bootstrap.UserRequestContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.Program;
import segmail.entity.landing.ServerInstance;

/**
 *
 * @author LeeKiatHaw
 */
public class ProgramLanding extends Program {
    
    @Inject private UserRequestContainer requestContainer;
    
    private List<ServerInstance> servers;
    private List<UserAccount> userAccounts;
    private ServerInstance serverEditing;
    private String name;
    private String address;
    private long userId;
    
    @Override
    public void initProgramParams() {
        Map<String,String[]> namedParams = this.reqContainer.getPogramParamsNamed();
        List<String> orderedParams = this.reqContainer.getProgramParamsOrdered();
        
        //Assume that param 1 is the program command
        String command = (orderedParams != null && !orderedParams.isEmpty()) ? orderedParams.get(0).toString().toUpperCase() : "";
        
        if(command.equals("CONFIRM")) {
            confirmSubscription();
            return;
        }
        
        if(command.equals("UNSUBSCRIBE")) {
            unsubscribe();
            return;
        }
        
    }
    
    public void confirmSubscription() {
        
    }
    
    public void unsubscribe() {
        
    }

    @Override
    public void initProgram() {
        
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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
    
}
