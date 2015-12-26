/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.rewrite;

import java.io.IOException;
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
public class RewriteModule extends BootstrapModule implements Serializable {

    @Inject UserRequestContainer userRequestContainer;
    
    //@Inject SegURLResolver urlResolver;
    
    @Override
    protected boolean execute(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
        String contextPath = req.getContextPath();
        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();
        //IMO, a bug in the 3 above methods
        contextPath = (contextPath == null) ? "" : contextPath;
        servletPath = (servletPath == null) ? "" : servletPath;
        pathInfo = (pathInfo == null) ? "" : pathInfo;
        
        //No actual viewId is known before this module, this module processes all viewId mappings
        if(SegURLResolver.getResolver().containsFile(((HttpServletRequest)request).getRequestURI()))
            return true;
        
        //1. Resolve program name
        String program = SegURLResolver.getResolver().resolveProgramName(servletPath.concat(pathInfo));
        
        //2. Inject it into ControlContainer
        userRequestContainer.setProgramName(program);
        String forwardViewId = "/index.xhtml";
        
        //The mapping!
        //Can be outsourced to a service
        
        //everything comes down to checking servlet path
        //If the request is to the "/" path or it is not for "/login",
        //meaning it is any other program except login, which is a special type 
        //of program, then forward to "/program/index.xhtml"
        //The program name will be passed by UserRequestContainer
        ///SegMail/autoemail -> /SegMail/program/autoemail/
        if(servletPath == null || "/".equals(servletPath)
                || !servletPath.contains("login")){
            //Default servletPath
            servletPath = "/program";
        }
        
        //forward don't need contextpath because it's done at the server side
        ((HttpServletRequest)request).getRequestDispatcher(servletPath.concat(forwardViewId)).forward(request, response);
        
        return false; //No need to do anything after forwarding
    }

    @Override
    protected void ifFail(ServletRequest request, ServletResponse response) {
        
    }

    @Override
    protected int executionSequence() {
        return Integer.MIN_VALUE + 1;
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
        //dispatchTypes.add(DispatcherType.FORWARD);
        
        return dispatchTypes;
    }

    @Override
    public String getName() {
        return "RewriteModule";
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response) {
        
    }
    
}
