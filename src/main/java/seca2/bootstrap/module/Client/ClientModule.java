/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Client;

import eds.component.GenericObjectService;
import eds.component.client.ClientService;
import eds.entity.client.Client;
import eds.entity.client.ContactInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.NonCoreModule;
import seca2.bootstrap.UserSessionContainer;

/**
 *
 * @author LeeKiatHaw
 */
@NonCoreModule
public class ClientModule extends BootstrapModule implements Serializable {
    
    @Inject ClientContainer clientContainer;
    @Inject UserSessionContainer sessionContainer;
    
    @EJB ClientService clientService;
    @EJB GenericObjectService objService;

    @Override
    protected boolean execute(ServletRequest request, ServletResponse response) {
        if(clientContainer.getClient() == null && sessionContainer.getUser() != null){
            Client client = clientService.getClientByAssignedUser(sessionContainer.getUser().getOBJECTID());
            
            clientContainer.setClient(client);
            
            List<ContactInfo> contacts = objService.getEnterpriseData(client.getOBJECTID(), ContactInfo.class);
            if(contacts != null && !contacts.isEmpty())
                clientContainer.setContact(contacts.get(0));
        }
        
        return true;
    }

    @Override
    protected void ifFail(ServletRequest request, ServletResponse response) {
        
    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response, Exception ex) {
        
    }

    @Override
    protected int executionSequence() {
        return Integer.MIN_VALUE;
    }

    @Override
    protected boolean inService() {
        return true;
    }

    /**
     * WS calls can be initiated by System users so it is best not to handle for 
     * "/*"
     * @return 
     */
    @Override
    protected String urlPattern() {
        return "/program/*";
    }

    @Override
    protected List<DispatcherType> getDispatchTypes() {
        List<DispatcherType> dispatchTypes = new ArrayList<DispatcherType>();
        dispatchTypes.add(DispatcherType.REQUEST);
        dispatchTypes.add(DispatcherType.FORWARD);

        return dispatchTypes;
    }

    @Override
    public String getName() {
        return "ClientModule";
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    protected boolean bypassDuringInstall() {
        return true;
    }

    @Override
    protected boolean bypassDuringNormal() {
        return false;
    }
    
    @Override
    protected boolean bypassDuringWeb() {
        return true;
    }

}
