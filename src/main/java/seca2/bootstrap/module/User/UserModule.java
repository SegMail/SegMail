/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.User;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import seca2.bootstrap.BootstrapInput;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.BootstrapOutput;
import seca2.bootstrap.CoreModule;
import seca2.bootstrap.GlobalValues;
import eds.component.user.UserService;
import java.io.IOException;
import javax.faces.context.ExternalContext;

/**
 *
 * @author vincent.a.lee
 */
//@SessionScoped //Should not be a SessionScoped object
@CoreModule
public class UserModule extends BootstrapModule implements Serializable {
    
    @EJB private UserService userService;
    @Inject private GlobalValues globalValues;

    @Inject private UserContainer userContainer; //Should we inject or should we put it in InputContext?
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
        //FacesContext fc = (FacesContext) inputContext.getFacesContext();
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
        
        //allow bypass authentication if application is not in PRODUCTION stage
        //if(!fc.getApplication().getProjectStage().equals(ProjectStage.Production)){
        //[20150328] allow bypass if application is in SETUP
        String bypassInURL = ec.getRequestParameterMap().get("SETUP");
        String bypassInWebXML = ec.getInitParameter("SETUP");
        if((bypassInWebXML != null && bypassInWebXML.compareToIgnoreCase("true") == 0) ||
                (bypassInURL != null && bypassInURL.compareToIgnoreCase("true") == 0)
                ){
            //String bypass = fc.getExternalContext().getInitParameter("BYPASS_AUTHENTICATION");
            //if not in PRODUCTION and BYPASS_AUTHENTICATION flag is set
            //if(bypass.equalsIgnoreCase("true")){
                return true;
            //}
            /**
             * If the database has not been set up yet, bypass authentication step
             * and direct to setup page(production)/testing page(development).
             * The following criteria can be used:
             * - no user has been created
             * - (other checks to be implemented...)
             * 
             * [20150322] This should be handled in the ProgramModule, not here.
             * 
             * Notes:
             * - Calling this check for every request is not feasible as it will 
             * incur lots of DB reads each time. There must be some ApplicationScoped
             * object that can be maintained to keep the status of installation.
             * 
             */
            
        }
            
        boolean sameSession = this.sameSession(session,this.userContainer);
        boolean isAuthenticated = this.isAuthenticated(this.userContainer);
        
        String program = inputContext.getProgram();
        this.userContainer.setLastProgram(program);
        
        //If it's not the same session, meaning it could be the first vist, or 
        //the previous session has timed out, load the login page.
        if(!sameSession){
            //If it's a postback, then the entire page must be refreshed.
            //System.out.println(fc.isPostback());
            if(fc.isPostback()){
                try {
                    ec.redirect(ec.getRequestContextPath()
                            +ec.getRequestServletPath()
                            +"/"
                            +program
                            +"/");
                    return true;
                } catch (IOException ex) {
                    return false;
                }
            }
            //Else, 
            outputContext.setPageRoot(this.defaultSites.LOGIN_PAGE);
            outputContext.setTemplateRoot(this.defaultSites.LOGIN_PAGE_TEMPLATE);
            
            //Regenerate a session object and store the session ID.
            session = (HttpSession) fc.getExternalContext().getSession(true);
            this.userContainer.setSessionId(session.getId());
            //Set the last URL, because user is redirected to the login page now
            //this.userContainer.setLastURL(program);
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
        return true;
    }


}
