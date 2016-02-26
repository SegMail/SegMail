/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.rewrite;

import eds.component.link.LogicalPathParser;
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
import seca2.bootstrap.DefaultKeys;
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
    @Inject DefaultKeys defaults;
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
        /*if(SegURLResolver.getResolver().containsFile(((HttpServletRequest)request).getRequestURI()))
            return true;*/
        String globalViewRoot = request.getServletContext().getInitParameter(defaults.GLOBAL_VIEWROOT);
        //LogicalPathParser newParser = new LogicalPathParser(servletPath.concat(pathInfo),globalViewRoot);
        //LogicalPathParser newParser = new LogicalPathParser(pathInfo,globalViewRoot);
        //userRequestContainer.setPathParser(newParser);
        //No need the above initialization, UserModule has already done it
        if(userRequestContainer.getPathParser().containsFileResource())
            return true;
        
        //1. Resolve program name
        //String program = SegURLResolver.getResolver().resolveProgramName(servletPath.concat(pathInfo));
        String program = userRequestContainer.getPathParser().getProgram();
        
        //2. Inject it into ControlContainer
        userRequestContainer.setProgramName(program);
        String forwardViewId = "/".concat(globalViewRoot);
        
        //The mapping!
        //Can be outsourced to a service
        
        //everything comes down to checking servlet path
        //If the request is to the "/" path or it is not for "/login",
        //meaning it is any other program except login, which is a special type 
        //of program, then forward to "/program/index.xhtml"
        //The program name will be passed by UserRequestContainer
        ///SegMail/autoemail -> /SegMail/program/autoemail/
        String loginPath = request.getServletContext().getInitParameter(defaults.LOGIN_PATH);
        String programPath = request.getServletContext().getInitParameter(defaults.PROGRAM_PATH);
        if(servletPath == null || "/".equals(servletPath)
                || !servletPath.equalsIgnoreCase(loginPath)){
            //Default servletPath
            servletPath = programPath;
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
        return Integer.MIN_VALUE + 2;
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

    @Override
    protected boolean bypassDuringInstall() {
        return false; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected boolean bypassDuringNormal() {
        return false;
    }

    @Override
    protected boolean bypassDuringWeb() {
        return false;
    }
    
    
}
