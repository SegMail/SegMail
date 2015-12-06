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
        return true;
    }

    @Override
    protected boolean execute(ServletRequest request, ServletResponse response) {

        //If on login page, don't do anything and continue the filterchain
        if(((HttpServletRequest)request).getServletPath().equalsIgnoreCase(LOGIN_PATH))
            return true;
        
        //For all other requests, if the user session is invalid or not logged in, send them to the login page
        if(
            userContainer == null ||
            userContainer.getSessionId() == null || userContainer.getSessionId().isEmpty() ||
            !userContainer.isLoggedIn()
        ){
            userContainer.setLastProgram(requestContainer.getProgramName());
            return false;
        }
                
        //For everything else not discovered, continue the filterchain
        return true;
    }


    @Override
    protected int executionSequence() {
        return Integer.MIN_VALUE + 1;
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
            if(SegURLResolver.containsFile(((HttpServletRequest)request).getRequestURI()))
                return;
            
            //We'll try this old method first
            requestContainer.setViewLocation(LOGIN_PAGE); //I'm not sure what will happen here, since we have already fowarded the request to this page below
            requestContainer.setTemplateLocation(LOGIN_PAGE_TEMPLATE);
            
            //((HttpServletResponse)response).sendRedirect(LOGIN_PATH);
            ((HttpServletRequest)request).getRequestDispatcher("/login/index.xhtml").forward(request, response);
            
            
        } catch (Exception ex) {
            Logger.getLogger(UserModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
