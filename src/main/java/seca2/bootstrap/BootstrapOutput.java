/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.SessionScoped;

/**
 *
 * @author LeeKiatHaw
 */
@SessionScoped
public class BootstrapOutput implements Serializable {
    
    private String pageRoot;
    private String templateRoot;
    private String menuRoot;
    private String errorMessage;
    private String errorStackTrace;
    
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
    
    
}
