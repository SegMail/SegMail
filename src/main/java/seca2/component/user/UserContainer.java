/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.component.user;

import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import seca2.entity.user.User;
import seca2.entity.user.UserPreferenceSet;
import seca2.entity.user.UserType;

/**
 *
 * @author LeeKiatHaw
 */
@SessionScoped
public class UserContainer implements Serializable {
    
    private User user;
    private List<UserPreferenceSet> preferences;
    private UserType userType;
    private String lastURL;
    private boolean loggedIn; //default is always false
    private String sessionId;
    
    public String regenerateSessionId(){
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest req = (HttpServletRequest) ec.getRequest();
        HttpServletResponse resp = (HttpServletResponse) ec.getResponse();
        
        HttpSession session = req.getSession(true);
        session.invalidate();
        
        HttpSession newSession = req.getSession(true);
        this.sessionId = newSession.getId();
        return this.sessionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public List<UserPreferenceSet> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<UserPreferenceSet> preferences) {
        this.preferences = preferences;
    }

    public String getLastURL() {
        return lastURL;
    }

    public void setLastURL(String lastURL) {
        this.lastURL = lastURL;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    
}
