/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.user;

import eds.component.data.IncompleteDataException;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import eds.component.user.UserAccountLockedException;
import seca2.bootstrap.UserSessionContainer;
import eds.component.user.UserLoginException;
import eds.component.user.UserService;
import eds.entity.user.User;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import seca2.component.landing.LandingServerGenerationStrategy;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;
import seca2.entity.landing.ServerInstance;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author vincent.a.lee
 */
@Named("FormUserLogin")
@RequestScoped
public class FormUserLogin {

    @EJB UserService userService;
    @EJB LandingService landingService;
    @Inject UserSessionContainer userContainer;

    private String username;
    private String password;

    private final String messageBoxId = "form-user-login";
    
    private String webserverURL;
    
    /**
     * This can work only because the variables in this bean was assessed in the xhtml page.
     * Always remember this!
     */
    @PostConstruct
    public void init(){
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        if(!fc.isPostback()) { /* && must have some other ways to know this is a timeout*/
            if( !userContainer.isLoggedIn()
                && (userContainer.getLastProgram() != null && !userContainer.getLastProgram().isEmpty())
                ){
                FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "Your session has expired. Please login again.", null);
            }
            
            if(webserverURL == null) {
                try {
                    ServerInstance webserver = landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN, ServerNodeType.WEB);
                    webserverURL = webserver.getURI();
                } catch (IncompleteDataException ex) {
                    Logger.getLogger(FormUserLogin.class.getName()).log(Level.SEVERE, null, ex);
                    webserverURL = ""; //No big deal, just don't try to set it anymore
                }
            }
        }
    }

    public void login() {
        try {
            Map<String,Object> userValues = new HashMap<String,Object>();
            User authenticatedUser = userService.login(this.username, this.password);
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Login successful!", null);
            
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            //Initialize userValues into userContainer
            this.userContainer.setSessionId(ec.getSessionId(true));
            this.userContainer.setLoggedIn(true);
            this.userContainer.setUser(authenticatedUser);
            this.userContainer.setUserType(authenticatedUser.getUSERTYPE());
            
            
            //do a redirect to refresh the view
            //Something is faulty here after a redirect
            String previousURI = this.userContainer.getLastProgram();
            
            if (previousURI != null && !previousURI.isEmpty()) {
                /*ec.redirect(ec.getRequestContextPath()
                        +ec.getRequestServletPath()
                        +"/"
                        +previousURI
                        +"/");*/ //calling "test" -> "/SegMail/program/test/test"
                String lastProgram = "/".concat((userContainer.getLastProgram() == null) ? "" : userContainer.getLastProgram());
                String contextPath = (ec.getRequestContextPath() == null) ? "" : ec.getRequestContextPath();
                ec.redirect(contextPath+lastProgram);
                //we need an adaptor pattern for redirection!
                //this should be in the navigation module
                
            } else {
                ec.redirect(ec.getRequestContextPath());//go to home
            }
        } catch (UserLoginException esliex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, esliex.getMessage(), null);
        } catch (UserAccountLockedException ualex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, "Your account has been locked. Please contact admin.", null);
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWebserverURL() {
        return webserverURL;
    }

    public void setWebserverURL(String webserverURL) {
        this.webserverURL = webserverURL;
    }
}
