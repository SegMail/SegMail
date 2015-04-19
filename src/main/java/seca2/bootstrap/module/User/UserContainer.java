/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.User;

import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import eds.entity.user.User;
import eds.entity.user.UserPreferenceSet;
import eds.entity.user.UserType;
import javax.annotation.PostConstruct;

/**
 *
 * @author LeeKiatHaw
 */
@SessionScoped
public class UserContainer implements Serializable {
    
    private User user;
    //private List<UserPreferenceSet> preferences;
    private UserType userType;
    private String lastProgram;
    private boolean loggedIn; //default is always false
    private String sessionId;
    
    private String username;
    private String lastname;
    private String contextPath;
    private String servletPath;
    
    @PostConstruct
    public void init(){
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        contextPath = ec.getRequestContextPath();
        servletPath = ec.getRequestServletPath();
    }
    
    public String getLastURL(){
        return this.contextPath + this.servletPath + "/"+ this.getLastProgram() + "/";
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

    public String getLastProgram() {
        return lastProgram;
    }

    public void setLastProgram(String lastProgram) {
        this.lastProgram = lastProgram;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    
}
