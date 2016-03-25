/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Path;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.CoreModule;
import seca2.bootstrap.UserRequestContainer;

/**
 *
 * @author LeeKiatHaw
 */
@CoreModule
public class PathModule extends BootstrapModule {
    
    @Inject private UserRequestContainer requestContainer;

    @Override
    protected boolean execute(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
        
        String contextPath = req.getContextPath();
        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();
        
        //IMO, a bug in the 3 above methods
        contextPath = (contextPath == null) ? "" : contextPath;
        servletPath = (servletPath == null) ? "" : servletPath;
        pathInfo = (pathInfo == null) ? "" : pathInfo;
        
        String loginPath = request.getServletContext().getInitParameter(defaults.LOGIN_PATH);
        String programPath = request.getServletContext().getInitParameter(defaults.PROGRAM_PATH);
        String wsPath = request.getServletContext().getInitParameter(defaults.WEBSERVICE_PATH);
        String globalViewRoot = request.getServletContext().getInitParameter(defaults.GLOBAL_VIEWROOT);
        
        String availableServletPath = "";
        if(servletPath.startsWith(loginPath) 
                || servletPath.startsWith(programPath)
                || servletPath.startsWith(wsPath) )
            availableServletPath = servletPath;
        
        LogicalPathParser newParser = new LogicalPathParser(servletPath.concat(pathInfo),globalViewRoot, availableServletPath);
        requestContainer.setPathParser(newParser);
        
        return true;
    }

    @Override
    protected void ifFail(ServletRequest request, ServletResponse response) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected int executionSequence() {
        return Integer.MIN_VALUE + 150;
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
        return false;
    }

    @Override
    protected String urlPattern() {
        return "/*";
    }

    @Override
    protected List<DispatcherType> getDispatchTypes() {
        List<DispatcherType> dispatchTypes = new ArrayList<>();
        dispatchTypes.add(DispatcherType.REQUEST);

        return dispatchTypes;
    }

    @Override
    public String getName() {
        return "PathModule";
    }
    
}
