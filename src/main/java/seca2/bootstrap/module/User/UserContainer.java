/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.User;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import seca2.entity.user.UserType;

/**
 * Container for retrieving user info and setting user parameters.
 * Does not reveal the underlying User object.
 * 
 * @author LeeKiatHaw
 */
@SessionScoped
public class UserContainer implements Serializable{
    
    private String userId;
    private String sessionId;
    private String previousURL;
    
    private UserType userType;
    
    private boolean loggedIn = false; //default is always false

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPreviousURL() {
        return previousURL;
    }

    public void setPreviousURL(String previousURL) {
        this.previousURL = previousURL;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
    
    
    
}
