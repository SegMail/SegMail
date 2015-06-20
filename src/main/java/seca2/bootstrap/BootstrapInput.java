/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import seca2.bootstrap.module.User.UserContainer;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
public class BootstrapInput implements Serializable{
    
    //private FacesContext facesContext;
    @Inject private UserContainer userContainer;
    
    private String program;
    private String contextPath;
    // This is just a workaround before we build the installation module!
    private boolean setup;
    
    @PostConstruct
    public void init(){
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        String bypass = ec.getInitParameter("SETUP");
        setup = (bypass != null) && bypass.compareToIgnoreCase("TRUE") == 0;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public UserContainer getUserContainer() {
        return userContainer;
    }

    public void setUserContainer(UserContainer userContainer) {
        this.userContainer = userContainer;
    }

    public boolean isSetup() {
        return setup;
    }

    public void setSetup(boolean setup) {
        this.setup = setup;
    }
    
    
}
