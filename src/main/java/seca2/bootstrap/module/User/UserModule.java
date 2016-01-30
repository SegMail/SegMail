/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.User;

import seca2.bootstrap.UserSessionContainer;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Inject;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.CoreModule;
import seca2.bootstrap.GlobalValues;
import eds.component.user.UserService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.DispatcherType;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import seca2.bootstrap.UserRequestContainer;
import segurl.filter.SegURLResolver;

/**
 *
 * @author vincent.a.lee
 */
@CoreModule
public class UserModule extends BootstrapModule implements Serializable {
    
    @EJB private UserService userService; 
    @Inject private GlobalValues globalValues;
    /**
     * Should we inject or should we put it in InputContext?
     * 
     * Here, because this is a SessionScoped while InputContext is RequestScoped.
     */
    @Inject private UserRequestContainer requestContainer;
    @Inject private UserSessionContainer userContainer; //
    
    public final String LOGIN_PAGE = "/programs/user/login_page.xhtml";
    public final String LOGIN_PAGE_TEMPLATE = "/programs/user/templates/mylogintemplate/template-layout.xhtml";
    
    public final String LOGIN_PATH = "/login";
    

    @Override
    protected boolean inService() {
        return false;
    }

    @Override
    protected boolean execute(ServletRequest request, ServletResponse response) throws ServletException, IOException {

        String contextPath = ((HttpServletRequest)request).getContextPath();
        String servletPath = ((HttpServletRequest)request).getServletPath();
        String pathInfo = ((HttpServletRequest)request).getPathInfo();
        
        //IMO, a bug in the 3 above methods
        contextPath = (contextPath == null) ? "" : contextPath;
        servletPath = (servletPath == null) ? "" : servletPath;
        pathInfo = (pathInfo == null) ? "" : pathInfo;
        //During postback of the form submission, the servlet path will be /login
        //and if the xhtml values are not set, the form methods will not be processed.
        //it has nothing to do with URL rewriting
        //Regardless pass or fail, just populate first, the next module should correct it
        requestContainer.setViewLocation(LOGIN_PAGE); //I'm not sure what will happen here, since we have already fowarded the request to this page below
        requestContainer.setTemplateLocation(LOGIN_PAGE_TEMPLATE);
        
        //If on login page, forward the request to viewId index.xhtml
        if(servletPath.equalsIgnoreCase(LOGIN_PATH)){
            return true;
        }
        
        //For all other requests, if the user session is logged in, let other modules decide the view
        if(!(
            userContainer == null ||
            userContainer.getSessionId() == null || 
            userContainer.getSessionId().isEmpty() ||
            !userContainer.isLoggedIn()
                )){
            return true;
        }
        
        //If request is for a file resource
        if(SegURLResolver.getResolver().containsFile(servletPath))
            return true;
        //If the request has servlet path /program or /login, it would match to FacesServlet from
        //web.xml config and parsed as Faces request.
        if(SegURLResolver.getResolver().containsFile(pathInfo))//Separate out for debugging purposes
            return true;
                
        //For everything else not discovered, don't continue the filterchain
        //userContainer.setLastProgram(SegURLResolver.getResolver().resolveProgramName(pathInfo));
        userContainer.setLastProgram(SegURLResolver.getResolver().resolveProgramName(servletPath.concat(pathInfo)));
        return false;
    }


    @Override
    protected int executionSequence() {
        return Integer.MIN_VALUE + 2;
    }
    
    @Override
    public String getName() {
        return "UserModule";
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    protected String urlPattern() {
        return "/*";
    }

    @Override
    protected List<DispatcherType> getDispatchTypes() {
        List<DispatcherType> dispatchTypes = new ArrayList<>();
        dispatchTypes.add(DispatcherType.REQUEST);
        //dispatchTypes.add(DispatcherType.FORWARD);
        
        return dispatchTypes;
    }

    /**
     * The sole purpose of the UserSessionContainer is to see if it's necessary to forward
 users to the login page.
     * 
     * @param request
     * @param response 
     */
    @Override
    protected void ifFail(ServletRequest request, ServletResponse response) {
        try {
            //If it's a file request, don't do anything
            if(SegURLResolver.getResolver().containsFile(((HttpServletRequest)request).getRequestURI()))
                return;
            
            ((HttpServletResponse)response).sendRedirect(((HttpServletRequest)request).getContextPath()+LOGIN_PATH);
            //((HttpServletRequest)request).getRequestDispatcher("/login").forward(request, response);
            
        } catch (Exception ex) {
            Logger.getLogger(UserModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
