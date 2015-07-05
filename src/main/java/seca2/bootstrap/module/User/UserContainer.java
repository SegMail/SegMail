/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.User;

import eds.entity.audit.ActiveUser;
import eds.entity.audit.ActiveUserAnnotation;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import eds.entity.user.User;
import eds.entity.user.UserType;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

/**
 *
 * @author LeeKiatHaw
 */
@SessionScoped
@ActiveUserAnnotation
public class UserContainer implements Serializable, ActiveUser {
    
    private User user;
    private UserType userType;
    private String lastProgram;
    private boolean loggedIn; //default is always false
    private String sessionId;
    
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

    @Override
    public String getUsername() {
        return user.getOBJECT_NAME();
    }

}
