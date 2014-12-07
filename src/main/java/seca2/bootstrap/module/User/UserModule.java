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
import seca2.bootstrap.CoreModule;

/**
 *
 * @author vincent.a.lee
 */
//@SessionScoped //Should not be a SessionScoped object
@CoreModule
public class UserModule extends BootstrapModule implements Serializable{
    
    @Inject private UserContainer userContainer; //this is not resolved precisely
    private final LoginMode loginMode = LoginMode.BLOCK;
    
    private String sSessionId;
    private String previousURI;
    private final String loginContainerName = "form-user-login:loginbox-container"; // should not be here!
    
    
    
    /**
     * Checks if a user is authenticated from a HttpSession object.
     * 
     * @param session
     * @return 
     */
    public boolean checkSessionActive(HttpSession session) {
        
        if (session == null) { //If user is visiting site for the first time
            
            return false;
        } else { //If HTTP session has been created, ie not the first visit.
            System.out.println(userContainer.getSessionId());
            
            //If there is an HTTP session, compare the sessionId with the existing user sessionId
            if (userContainer.getSessionId() == null || 
                    !userContainer.getSessionId().equals(session.getId())) {
                
                return false;
            } else {
                
                return true;
            }
        }
    }

    public String getsSessionId() {
        return sSessionId;
    }

    public void setsSessionId(String sSessionId) {
        this.sSessionId = sSessionId;
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
        FacesContext fc = (FacesContext)inputContext.getFacesContext();
        HttpSession session = (HttpSession) fc.getExternalContext().getSession(true);
        
        boolean sessionActive = this.checkSessionActive(session);
        
        //Because all requests are handled here, so even a login form submission will
        //also be stopped here because there is not yet an authenticated session.
        //Solution? 
        
        if(!sessionActive){//if session is not active
            //These 2 attributes should be taken from a "container", preferably something like UserContainer
            outputContext.setPageRoot(this.defaultSites.LOGIN_PAGE);
            outputContext.setTemplateRoot(this.defaultSites.LOGIN_PAGE_TEMPLATE);
            return false;
        }
        
        
        outputContext.setPageRoot(this.defaultSites.DEFAULT_HOME);
        
        return true;
    }

    @Override
    protected int executionSequence() {
        return -99;
    }
    
    
}

