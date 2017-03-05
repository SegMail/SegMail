/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Preloader;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.NonCoreModule;
import seca2.bootstrap.UserRequestContainer;

/**
 *
 * @author LeeKiatHaw
 */
@NonCoreModule
public class PreloaderModule extends BootstrapModule {
    
    @Inject UserRequestContainer reqCont;
    @Inject PreloaderContainer layoutCont;

    @Override
    protected boolean execute(ServletRequest request, ServletResponse response) throws Exception {
        //Assign a random message of the day
        
        String messageOfTheDay = "Very soon you will see random messages here.";
        String subMessageOfTheDay = "But no advertisements, so don't worry.";
        
        layoutCont.setPreloadMessageMain(messageOfTheDay);
        layoutCont.setPreloadMessageSub(subMessageOfTheDay);
        
        if(!reqCont.getPathParser().getOrderedParams().isEmpty())
            layoutCont.setTimeout(0);
        
        return true;
    }

    @Override
    protected void ifFail(ServletRequest request, ServletResponse response) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response, Exception ex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected int executionSequence() {
        return 1000;
    }

    @Override
    protected boolean inService() {
        return true;
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
        return this.getClass().getSimpleName();
    }
    
}
