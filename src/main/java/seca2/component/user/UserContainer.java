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
public class UserContainer implements Serializable, UserContainerInterface {
    
    private User user;
    private List<UserPreferenceSet> preferences;
    private UserType userType;
    private String lastURL;
    private boolean loggedIn; //default is always false
    private String sessionId;
    
    @Override
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

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public UserType getUserType() {
        return userType;
    }

    @Override
    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Override
    public List<UserPreferenceSet> getPreferences() {
        return preferences;
    }

    @Override
    public void setPreferences(List<UserPreferenceSet> preferences) {
        this.preferences = preferences;
    }

    @Override
    public String getLastURL() {
        return lastURL;
    }

    @Override
    public void setLastURL(String lastURL) {
        this.lastURL = lastURL;
    }

    @Override
    public boolean isLoggedIn() {
        return loggedIn;
    }

    @Override
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    
}
