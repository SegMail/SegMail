/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.user;

import java.io.IOException;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.joda.time.DateTime;
import seca2.bootstrap.module.User.UserModule;
import seca2.component.data.DBConnectionException;
import seca2.component.user.UserAccountLockedException;
import seca2.component.user.UserContainer;
import seca2.component.user.UserService;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author vincent.a.lee
 */
@Named("FormUserLogin")
@RequestScoped
public class FormUserLogin {
    
    @EJB private UserService userService;
    /*@Inject private UserModule userModule; //to check if there was a previous URL to be redirected and set the sessionID
    
    */
    
    private String username;
    private String password;
    
    private final String messageBoxId = "form-user-login";
    
    public void login() throws IOException {

        //Check if username and password are present
        if (username == null || username.isEmpty()) {
            FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR,
                    "Please enter username", null);
            return;
        }
        if (password == null || password.isEmpty()) {
            FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR,
                    "Please enter password", null);
            return;
        }
        UserContainer uc = null;
        try {
            //use UserService to login
            uc = userService.login(username, password);
            
        } catch (UserAccountLockedException usalex) {
            FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR,
                    "Oops...Your account has been locked. Please contact administrator to unlock it.", null);
            return;
        } catch (DBConnectionException dbex) {
            FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR, dbex.getMessage(), null);
            return;
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
            return;
        }

        if (uc == null) {
            FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR,
                    "Wrong credentials. Are you sure you entered the correct credentials?",
                    "Alternatively, would you like to created a new account? ");
            return;
        }

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest req = (HttpServletRequest) ec.getRequest();
        HttpServletResponse resp = (HttpServletResponse) ec.getResponse();

        //HttpSession session = req.getSession(true);
        //session.setAttribute("user", 1);
        //userModule.setsSessionId(sessionId);
        //sSessionId = session.getId();
        DateTime sessionStarttime = new DateTime();
        //System.out.println("Session " + userModule.getsSessionId() + " started at " + sessionStarttime);
        password = "";
        username = "";
        
        //Regenerate session ID
        HttpSession session = req.getSession(true);
        //Set UserContainer with session ID
        String sessionId = session.getId();
        uc.setSessionId(sessionId);
        
        //do a redirect to refresh the view
        String previousURI = uc.getLastURL();
        if (previousURI != null && !previousURI.isEmpty()) {
            ec.redirect(previousURI);
        } else {
            ec.redirect(ec.getRequestContextPath());//go to home
        }

        //Remember to construct the UserSession before redirecting!
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
    
    
}
