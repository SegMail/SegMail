/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.component.user;

import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
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
