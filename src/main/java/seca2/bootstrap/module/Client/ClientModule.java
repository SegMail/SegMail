/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Client;

import eds.component.client.ClientService;
import eds.entity.client.Client;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Inject;
import seca2.bootstrap.BootstrapInput;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.BootstrapOutput;
import seca2.bootstrap.NonCoreModule;
import seca2.bootstrap.module.User.UserContainer;

/**
 *
 * @author LeeKiatHaw
 */
@NonCoreModule
public class ClientModule extends BootstrapModule implements Serializable {

    @Inject UserContainer userContainer;
    @Inject ClientContainer clientContainer;
    
    @EJB ClientService clientService;
    
    @Override
    protected boolean execute(BootstrapInput inputContext, BootstrapOutput outputContext) {
        if(userContainer != null && userContainer.getUser() != null &&
                clientContainer == null & clientContainer.getClient() == null){
            Client client = clientService.getClientByAssignedUser(userContainer.getUser().getOBJECTID());
        }
        
        return true;
    }

    @Override
    protected int executionSequence() {
        return 0;
    }

    @Override
    protected boolean inService() {
        return true;
    }
    
}
