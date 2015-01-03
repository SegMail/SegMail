/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.User;

import java.io.Serializable;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import seca2.bootstrap.BootstrapInput;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.BootstrapOutput;

/**
 *
 * @author vincent.a.lee
 */
//@SessionScoped //Should not be a SessionScoped object
//@CoreModule
public abstract class UserModule_1 extends BootstrapModule implements Serializable {

    @Inject
    private UserSession userContainer; //this is not resolved precisely
    private final LoginMode loginMode = LoginMode.BLOCK;

    private String previousURI;
    private final String loginContainerName = "form-user-login:loginbox-container"; // should not be here!

    /**
     * Checks if a user is authenticated from a HttpSession object. A session is 
     * active if it has been "initiated", but it may not be authenticated.
     *
     * @param session
     * @return
     */
    public boolean checkSessionActive(HttpSession session) {

        if (session == null) { //If user is visiting site for the first time
            return false;
        }
        //If HTTP session has been created, ie not the first visit.
        System.out.println(userContainer.getSessionId());

        //If there is an HTTP session, compare the sessionId with the existing user sessionId
        if (userContainer.getSessionId() == null)
            return false;
        
        if (!userContainer.getSessionId().equals(session.getId()))
            return false;
        
        //is it safe to return true at the end? hmm...
        return true;
    }
    
    public boolean checkSessionAuthenticated(HttpSession session) {
        if (session == null) { //If user is visiting site for the first time
            return false;
        }
        //If HTTP session has been created, ie not the first visit.
        System.out.println(userContainer.getSessionId());
        
        if(!userContainer.isLoggedIn())
            return false;
        
        return true;
    }

    public String getPreviousURI() {
        return previousURI;
    }

    public void setPreviousURI(String previousURI) {
        this.previousURI = previousURI;
    }

    public LoginMode getLoginMode() {
        return loginMode;
    }

    @Override
    protected boolean execute(BootstrapInput inputContext, BootstrapOutput outputContext) {
        FacesContext fc = (FacesContext) inputContext.getFacesContext();
        HttpSession session = (HttpSession) fc.getExternalContext().getSession(true);
        
        System.out.println(fc.getExternalContext().getRequestContextPath());

        boolean sessionActive = this.checkSessionActive(session);
        boolean sessionAuthenticated = this.checkSessionAuthenticated(session);

        //Because all requests are handled here, so even a login form submission will
        //also be stopped here because there is not yet an authenticated session.
        //Solution? 
        
        //If session is active and authenticated
        if(sessionActive && sessionAuthenticated)
            return true;
        
        //If session is not active (1st time visit or timeout)
        if(!sessionActive){
            outputContext.setPageRoot(this.defaultSites.LOGIN_PAGE);
            outputContext.setTemplateRoot(this.defaultSites.LOGIN_PAGE_TEMPLATE);
            //fc.getExternalContext().redirect(
            //        fc.getExternalContext().getRequestContextPath() + 
            //                "/programs/user/login_page.xhtml");
            
            this.userContainer.setSessionId(session.getId());
            return false;
        }
        
        //If session is active but not authenticated (this could be an authenticating request)
        //Wrong! This has no difference with the 1st scenario and could be potential security lapse
        if(sessionActive && !userContainer.isLoggedIn())
            return true;
        

        //outputContext.setPageRoot(this.defaultSites.DEFAULT_HOME);
        //return false if no condition is fulfilled.
        return false;
    }

    @Override
    protected int executionSequence() {
        return -99;
    }

}
