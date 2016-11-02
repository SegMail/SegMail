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
import eds.component.user.UserService;
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

/**
 *
 * @author vincent.a.lee
 */
@CoreModule
public class UserModule extends BootstrapModule implements Serializable {

    @EJB
    private UserService userService;

    /**
     * Should we inject or should we put it in InputContext?
     *
     * Here, because this is a SessionScoped while InputContext is
     * RequestScoped.
     */
    @Inject
    private UserRequestContainer requestContainer;
    @Inject
    private UserSessionContainer userContainer;

    @Override
    protected boolean inService() {
        return true;
    }

    @Override
    protected boolean execute(ServletRequest request, ServletResponse response)
            throws ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String contextPath = req.getContextPath();
        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();

        //IMO, a bug in the 3 above methods
        contextPath = (contextPath == null) ? "" : contextPath;
        servletPath = (servletPath == null) ? "" : servletPath;
        pathInfo = (pathInfo == null) ? "" : pathInfo;

        //Initialize the LogicalPathParser
        //This parser is totally separated from the RewriteModule's parser, because UserModule is supposed to be 
        //independent of other modules.
        String loginPath = request.getServletContext().getInitParameter(defaults.LOGIN_PATH);

        //During postback of the form submission, the servlet path will be /login
        //and if the xhtml values are not set, the form methods will not be processed.
        //it has nothing to do with URL rewriting
        //Regardless pass or fail, just populate first, the next module should correct it
        requestContainer.setViewLocation(defaults.LOGIN_PAGE); //I'm not sure what will happen here, since we have already fowarded the request to this page below
        requestContainer.setTemplateLocation(defaults.LOGIN_PAGE_TEMPLATE);

        //If on login page, forward the request to viewId index.xhtml
        //String loginPath = request.getServletContext().getInitParameter(defaults.LOGIN_PATH);
        if (servletPath.equalsIgnoreCase(loginPath)) {
            return true;
        }

        //Authenticate a webservice call
        if (requestContainer.isWebservice()) {
            return true;
        }

        //For all other requests, if the user session is logged in, let other modules decide the view
        if (!(userContainer == null
                || userContainer.getSessionId() == null
                || userContainer.getSessionId().isEmpty()
                || !userContainer.isLoggedIn())) {
            return true;
        }

        //If request is for a file resource
        if (requestContainer.getPathParser().containsFileResource()) {
            return true;
        }

        //For everything else not discovered, don't continue the filterchain
        String lastProgram = requestContainer.getPathParser().getProgram();
        userContainer.setLastProgram(lastProgram);

        return false;
    }

    @Override
    protected int executionSequence() {
        return Integer.MIN_VALUE + 200;
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
     * The sole purpose of the UserSessionContainer is to see if it's necessary
     * to forward users to the login page.
     *
     * @param request
     * @param response
     */
    @Override
    protected void ifFail(ServletRequest request, ServletResponse response) {
        try {
            if (requestContainer.getPathParser().containsFileResource()) {
                return;
            }

            String loginPath = request.getServletContext().getInitParameter(defaults.LOGIN_PATH);
            
            //http://stackoverflow.com/questions/9305144/using-jsf-2-0-facelets-is-there-a-way-to-attach-a-global-listener-to-all-ajax/9311920#9311920
            if ("partial/ajax".equals(((HttpServletRequest) request).getHeader("Faces-Request"))) {
                // It's a JSF ajax request.
                response.setContentType("text/xml");
                response.getWriter()
                        .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                        .printf("<partial-response><redirect url=\"%s\"></redirect></partial-response>", ((HttpServletRequest) request).getContextPath() + loginPath);
            } else { //If it is a normal request
                ((HttpServletResponse) response).sendRedirect(((HttpServletRequest) request).getContextPath() + loginPath);//No trailing slash before and now so why it doesn't work now?
            }

        } catch (Exception ex) {
            Logger.getLogger(UserModule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response, Exception ex) {
        HttpServletResponse res = (HttpServletResponse) response;
        res.addHeader("AUTHENTICATION_RESULT", ex.getClass() + ": " + ex.getMessage());
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
}
