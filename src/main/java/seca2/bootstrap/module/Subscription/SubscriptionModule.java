/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Subscription;

import java.io.Serializable;
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
public class SubscriptionModule extends BootstrapModule implements Serializable {
    
    @Inject private UserContainer userContainer;

    @Override
    protected boolean execute(BootstrapInput inputContext, BootstrapOutput outputContext) {
        return true;
    }

    @Override
    protected int executionSequence() {
        return 0;
    }

    @Override
    protected boolean inService() {
        return false;
    }
    
}
