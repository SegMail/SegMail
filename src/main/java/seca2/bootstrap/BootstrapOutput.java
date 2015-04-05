/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ViewScoped;

/**
 * This class has to be SessionScoped, because it holds the view locations
 * of the different parts of the application. Is it a bad idea? I'm still figuring
 * out...
 * 
 * @author LeeKiatHaw
 */
@SessionScoped
public class BootstrapOutput implements Serializable {
    
    private String pageRoot;
    private String templateName;
    private String templateRoot;
    private String menuRoot;
    private String errorMessage;
    private String errorStackTrace;
    private String programTitle;
    private String programDescription;
    
    private Map<String,Object> nonCoreValues = new HashMap<String,Object>();

    public String getPageRoot() {
        return pageRoot;
    }

    public void setPageRoot(String pageRoot) {
        this.pageRoot = pageRoot;
    }

    public String getTemplateRoot() {
        return templateRoot;
    }

    public void setTemplateRoot(String templateRoot) {
        this.templateRoot = templateRoot;
    }

    public Map<String, Object> getNonCoreValues() {
        return nonCoreValues;
    }

    public void setNonCoreValues(Map<String, Object> nonCoreValues) {
        this.nonCoreValues = nonCoreValues;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorStackTrace() {
        return errorStackTrace;
    }

    public void setErrorStackTrace(String errorStackTrace) {
        this.errorStackTrace = errorStackTrace;
    }

    public String getMenuRoot() {
        return menuRoot;
    }

    public void setMenuRoot(String menuRoot) {
        this.menuRoot = menuRoot;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getProgramTitle() {
        return programTitle;
    }

    public void setProgramTitle(String programTitle) {
        this.programTitle = programTitle;
    }

    public String getProgramDescription() {
        return programDescription;
    }

    public void setProgramDescription(String programDescription) {
        this.programDescription = programDescription;
    }
    
    
}
