/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import eds.entity.audit.ActiveUser;
import eds.entity.layout.Layout;
import eds.entity.program.Program;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import eds.entity.user.User;
import eds.entity.user.UserType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import seca2.bootstrap.module.Navigation.MenuItemContainer;

/**
 *
 * @author LeeKiatHaw
 */
@Named("UserSessionContainer")
@SessionScoped
public class UserSessionContainer implements Serializable, ActiveUser {
    
    private User user;
    private UserType userType;
    private String lastProgram;
    private boolean loggedIn; //default is always false
    private String sessionId;
    
    private String contextPath;
    private String servletPath;
    
    private Program currentProgram;
    private Program overrideProgram;
    private Layout currentLayout;
    
    private List<MenuItemContainer> menu;
    
    @PostConstruct
    public void init(){
        user = null;
        userType = null;
        lastProgram = "";
        loggedIn = false;
        sessionId = "";
        contextPath = "";
        servletPath = "";
        currentProgram = null;
        overrideProgram = null;
        currentLayout = null;
        /**
         * Not a good practice but NavigationModule identifies new sessions by 
         * checking if menu is null. An empty List doesn't quite make it because
         * some users can have an empty menu.
         * 
         */
        menu = null;
        
    }
    
    public String getLastURL(){
        return this.contextPath + this.servletPath + "/"+ this.getLastProgram() + "/"; //Giving nullnull
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
        
        return (user != null) ? user.getOBJECT_NAME(): "";
    }
    
    public String getUsertypeName() {
        return (userType != null) ? userType.getUSERTYPENAME() : "";
    }

    public Program getCurrentProgram() {
        if(overrideProgram != null)
            return overrideProgram;
        return currentProgram;
    }

    public void setCurrentProgram(Program currentProgram) {
        this.currentProgram = currentProgram;
        revertProgramOverwrite();
    }

    public Layout getCurrentLayout() {
        return currentLayout;
    }

    public void setCurrentLayout(Layout currentLayout) {
        this.currentLayout = currentLayout;
    }

    public List<MenuItemContainer> getMenu() {
        return menu;
    }

    public void setMenu(List<MenuItemContainer> menu) {
        this.menu = menu;
    }
    
    public void overwriteProgramTitle(String overwriteTitle) {
        if(overrideProgram == null)
            overrideProgram = new Program(currentProgram);
        
        overrideProgram.setDISPLAY_TITLE(overwriteTitle);
    }
    
    public void overwriteProgramDescription(String overwriteDesc) {
        if(overrideProgram == null)
            overrideProgram = new Program(currentProgram);
        
        overrideProgram.setDISPLAY_DESCRIPTION(overwriteDesc);
    }
    
    public void revertProgramOverwrite() {
        overrideProgram = null;
    }
}
