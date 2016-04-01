/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.chartjs;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.DispatcherType;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.CoreModule;

/**
 *
 * @author LeeKiatHaw
 */
@CoreModule
public class TestBootstrapModule extends BootstrapModule {

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
        
        String webservicePath = request.getServletContext().getInitParameter(defaults.WEBSERVICE_PATH);
        
        String contentType = request.getContentType();
        System.out.println("TestService: contenttype of request is: "+contentType);
        
        return true;
    }

    @Override
    protected void ifFail(ServletRequest request, ServletResponse response) throws Exception {
        
    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response, Exception ex) {
        
    }

    @Override
    protected int executionSequence() {
        return 0;
    }

    @Override
    protected boolean inService() {
        return false;
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
        return "TestBootstrapModule";
    }
    
}
