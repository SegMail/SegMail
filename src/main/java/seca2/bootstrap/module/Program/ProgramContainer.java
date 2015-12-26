/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Program;

import java.io.Serializable;
import java.util.Stack;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * This class is different from UserContainer.getLastURL(). This stores the 
 * entire program history within the session which can be tracked or traced.
 * 
 * @author LeeKiatHaw
 */
@SessionScoped
public class ProgramContainer implements Serializable{
    
    private String currentProgram;
    private String contextPath;
    private String servletPath;
    
    private Stack<String> programHistory = new Stack<String>();
    
    @PostConstruct
    public void init(){
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        contextPath = ec.getRequestContextPath();
        servletPath = ec.getRequestServletPath();
    }
    
    public void visitNewProgram(String newProgram){
        programHistory.push(currentProgram);
        currentProgram = newProgram;
    }

    public String getCurrentProgram() {
        return currentProgram;
    }
    
    public String getLastProgram(){
        return (programHistory.isEmpty()) ? null : programHistory.peek();
    }
    
    @Deprecated
    public String getCurrentURL(){
        return this.contextPath + this.servletPath + "/"+ this.currentProgram + "/";
    }
    
    @Deprecated
    public String getLastURL(){
        return this.contextPath + this.servletPath + "/"+ this.getLastProgram() + "/";
    }
}
