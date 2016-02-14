/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.install;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.CoreModule;
import seca2.bootstrap.DefaultKeys;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.UserSessionContainer;

/**
 *
 * @author LeeKiatHaw
 */
@CoreModule
public class InstallationModule extends BootstrapModule implements Serializable{

    @Inject DefaultKeys defaults;
    @Inject private UserRequestContainer requestContainer;
    @Inject private UserSessionContainer userContainer;
    
    @Override
    protected boolean execute(ServletRequest request, ServletResponse response) {
        
        //1) Check if the database has been initialized by calling a few DB services
        //2) If app is considered installed, return true to continue the chain processing
        //3) If app is not installed, display the installation page.
        //If system in installation mode
        boolean install = Boolean.parseBoolean(request.getServletContext().getInitParameter(defaults.INSTALL));
        if(install){
            requestContainer.setProgramName(request.getServletContext().getInitParameter(defaults.INSTALLATION_PROGRAM_NAME));
            requestContainer.setViewLocation(request.getServletContext().getInitParameter(defaults.INSTALLATION_VIEWROOT));
            requestContainer.setTemplateLocation(request.getServletContext().getInitParameter(defaults.INSTALLATION_TEMPLATE_LOCATION));
        }
            
        
        return true;
    }

    @Override
    protected int executionSequence() {
        return Integer.MIN_VALUE; //How to inject this value from web.xml?
    }

    @Override
    protected boolean inService() {
        return true;
    }

    @Override
    public String getName() {
        return "InstallationModule";
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    protected void ifFail(ServletRequest request, ServletResponse response) {
        
    }

    @Override
    protected String urlPattern() {
        return "/";
    }

    @Override
    protected List<DispatcherType> getDispatchTypes() {
        List<DispatcherType> dispatchTypes = new ArrayList<DispatcherType>();
        dispatchTypes.add(DispatcherType.REQUEST);
        dispatchTypes.add(DispatcherType.FORWARD);

        return dispatchTypes;
    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
