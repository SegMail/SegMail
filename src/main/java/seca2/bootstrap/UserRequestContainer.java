/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import seca2.bootstrap.module.Path.LogicalPathParser;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
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
    
    @Deprecated
    private String menuLocation;
    private Map<String,String> menuLocations;
    
    private boolean error = false; //default value
    
    private String errorMessage;
    
    private StackTraceElement[] errorStackTrace;
    
    private LogicalPathParser pathParser;
    
    private Map<String,String[]> pogramParamsNamed;
    private List<String> programParamsOrdered;
    
    //Test webservice
    private boolean webservice;
    
    private boolean renderPageToolbar;
    private boolean renderPageBreadCrumbs;
    
    private boolean renderPreloader;
    
    @PostConstruct
    public void init() {
        //default values
        renderPageToolbar = true;
        renderPageBreadCrumbs = true;
        renderPreloader = true;
    }

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

    public boolean isWebservice() {
        return webservice;
    }

    public void setWebservice(boolean webservice) {
        this.webservice = webservice;
    }

    public boolean isRenderPageToolbar() {
        return renderPageToolbar;
    }

    public void setRenderPageToolbar(boolean renderPageToolbar) {
        this.renderPageToolbar = renderPageToolbar;
    }

    public boolean isRenderPageBreadCrumbs() {
        return renderPageBreadCrumbs;
    }

    public void setRenderPageBreadCrumbs(boolean renderPageBreadCrumbs) {
        this.renderPageBreadCrumbs = renderPageBreadCrumbs;
    }

    public Map<String, String> getMenuLocations() {
        return menuLocations;
    }

    public void setMenuLocations(Map<String, String> menuLocations) {
        this.menuLocations = menuLocations;
    }

    public boolean isRenderPreloader() {
        return renderPreloader;
    }

    public void setRenderPreloader(boolean renderPreloader) {
        this.renderPreloader = renderPreloader;
    }
    
    public String printFullPathWithoutContext() {
        String fullPath = getProgramName();
        for(String orderedParam : programParamsOrdered) {
            fullPath += "/" + orderedParam;
        }
        if(!fullPath.startsWith("/"))
            fullPath = "/"+fullPath;
        
        return fullPath;
    }
}
