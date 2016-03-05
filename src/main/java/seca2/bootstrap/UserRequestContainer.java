/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import eds.component.link.LogicalPathParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author LeeKiatHaw
 */
@Named("UserRequestContainer")
@RequestScoped
public class UserRequestContainer {

    private String programName;
    
    private String viewLocation;
    
    private String templateLocation;
    
    private String menuLocation;
    
    private boolean error = false; //default value
    
    private String errorMessage;
    
    private StackTraceElement[] errorStackTrace;
    
    private LogicalPathParser pathParser;
    
    private Map<String,String[]> pogramParamsNamed;
    private List<String> programParamsOrdered;

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getViewLocation() {
        return viewLocation;
    }

    public void setViewLocation(String viewLocation) {
        this.viewLocation = viewLocation;
    }

    public String getTemplateLocation() {
        return templateLocation;
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMenuLocation() {
        return menuLocation;
    }

    public void setMenuLocation(String menuLocation) {
        this.menuLocation = menuLocation;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public StackTraceElement[] getErrorStackTrace() {
        return errorStackTrace;
    }

    public void setErrorStackTrace(StackTraceElement[] errorStackTrace) {
        this.errorStackTrace = errorStackTrace;
    }

    public LogicalPathParser getPathParser() {
        return pathParser;
    }

    public void setPathParser(LogicalPathParser pathParser) {
        this.pathParser = pathParser;
    }

    public Map<String, String[]> getPogramParamsNamed() {
        return pogramParamsNamed;
    }

    public void setPogramParamsNamed(Map<String, String[]> pogramParamsNamed) {
        this.pogramParamsNamed = pogramParamsNamed;
    }

    public List<String> getProgramParamsOrdered() {
        return programParamsOrdered;
    }

    public void setProgramParamsOrdered(List<String> programParamsOrdered) {
        this.programParamsOrdered = programParamsOrdered;
    }
}
