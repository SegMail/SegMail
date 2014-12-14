/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.User;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import seca2.bootstrap.BootstrapInput;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.BootstrapOutput;
import seca2.bootstrap.CoreModule;

/**
 *
 * @author vincent.a.lee
 */
//@SessionScoped //Should not be a SessionScoped object
@CoreModule
public class UserModule extends BootstrapModule implements Serializable {

    @Inject
    private UserContainer userContainer; //this is not resolved precisely
    private final LoginMode loginMode = LoginMode.BLOCK;

    private String previousURI;
    private final String loginContainerName = "form-user-login:loginbox-container"; // should not be here!

    public boolean sameSession(HttpSession session, UserContainer uc){
        if(uc == null)
            return false;
        
        if(session == null)
            return false;
        
        return (session.getId().equals(uc.getSessionId()));
    }
    
    public boolean isAuthenticated(UserContainer uc){
        return uc.isLoggedIn();
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
        HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
        
        boolean sameSession = this.sameSession(session,this.userContainer);
        boolean isAuthenticated = this.isAuthenticated(this.userContainer);
        
        //If it's not the same session, meaning it could be the first vist, or 
        //the previous session has timed out, load the login page.
        if(!sameSession){
            outputContext.setPageRoot(this.defaultSites.LOGIN_PAGE);
            outputContext.setTemplateRoot(this.defaultSites.LOGIN_PAGE_TEMPLATE);
            
            //Regenerate a session object and store the session ID.
            session = (HttpSession) fc.getExternalContext().getSession(true);
            this.userContainer.setSessionId(session.getId());
            
            //Don't forget to return false to break the bootstrapping chain!
            return false;
        }
        
        //If it's the same session but it is not logged in, the user could be 
        //browsing non-secured pages or authenticating.
        if(sameSession && !isAuthenticated){
            return false;
        }
        
        //If session is still active and user is authenticated, continue the bootstrapping
        //chain
        if(sameSession && isAuthenticated){
            return true;
        }
        
        return true;
    }

    @Override
    protected int executionSequence() {
        return -99;
    }

    @Override
    protected boolean inService() {
        return false;
    }


}
