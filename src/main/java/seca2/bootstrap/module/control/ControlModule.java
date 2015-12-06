/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.CoreModule;
import seca2.bootstrap.UserRequestContainer;
import segurl.filter.SegURLResolver;

/**
 * This is the "BIOS" module that will resolve the very first program request 
 * value during request processing.
 * 
 * @author LeeKiatHaw
 */
@CoreModule
public class ControlModule extends BootstrapModule implements Serializable {

    @Inject UserRequestContainer userRequestContainer;
    
    
    @Override
    protected boolean execute(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
        //1. Resolve program name
        String program = SegURLResolver.resolveProgramName(req.getPathInfo());
        
        //2. Inject it into ControlContainer
        userRequestContainer.setProgramName(program);
        
        return true;
    }

    @Override
    protected void ifFail(ServletRequest request, ServletResponse response) {
        
    }

    @Override
    protected int executionSequence() {
        return Integer.MIN_VALUE;
    }

    @Override
    protected boolean inService() {
        return true;
    }

    @Override
    protected String urlPattern() {
        return "/*"; //Intercept all requests
    }

    @Override
    protected List<DispatcherType> getDispatchTypes() {
        List<DispatcherType> dispatchTypes = new ArrayList<DispatcherType>();
        dispatchTypes.add(DispatcherType.REQUEST);
        
        return dispatchTypes;
    }

    @Override
    public String getName() {
        return "ControlModule";
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response) {
        
    }
    
}
