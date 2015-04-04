/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
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
    
    
}
