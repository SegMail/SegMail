/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * This is a temporary placeholder for all page locations until we figure a way
 * to build this entire module.
 * 
 * @author LeeKiatHaw
 */
public class DefaultKeys {
    
    /**
     * 
     */
    public final String ERROR_VIEWROOT = "ERROR_VIEWROOT";
    
    /**
     * 
     */
    public final String ERROR_TEMPLATE_LOCATION = "ERROR_TEMPLATE_LOCATION";
    
    public final String LOGIN_PAGE = "/programs/user/login_page.xhtml";
    public final String LOGIN_PAGE_TEMPLATE = "/programs/user/templates/mylogintemplate/template-layout.xhtml";
    
    public final String DEFAULT_HOME = "/programs/test/layout.xhtml";
    
    private final String DEFAULT_TEMPLATE_NAME = "DEFAULT_TEMPLATE_LOCATION";
    
    /**
     * A flag to indicate which mode the application is running on. Reference to 
     * the RunMode enumeration.
     */
    public final String RUN_MODE = "RUN_MODE";
    
    /**
     * The servlet path for login page
     */
    public final String LOGIN_PATH = "LOGIN_PATH";
    
    /**
     * The servlet path for programs
     */
    public final String PROGRAM_PATH = "PROGRAM_PATH";
    
    /**
     * The servlet path for webservice calls
     */
    public final String WEBSERVICE_PATH = "WEBSERVICE_PATH";
    
    /**
     * The name of the installation program.
     */
    public final String INSTALLATION_PROGRAM_NAME = "INSTALLATION_PROGRAM_NAME";
    
    /**
     * The location of the installation viewroot.
     */
    public final String INSTALLATION_VIEWROOT = "INSTALLATION_VIEWROOT";
    
    /**
     * The location of the installation template.
     */
    public final String INSTALLATION_TEMPLATE_LOCATION = "INSTALLATION_TEMPLATE_LOCATION";
    
    /**
     * The global default program in the normal operational mode.
     */
    public final String GLOBAL_DEFAULT_PROGRAM = "GLOBAL_DEFAULT_PROGRAM";
    
    /**
     * The global default template location.
     */
    public final String DEFAULT_TEMPLATE_LOCATION = "DEFAULT_TEMPLATE_LOCATION";
    
    public final String GLOBAL_VIEWROOT = "GLOBAL_VIEWROOT";
    
    private String DEFAULT_TEMPLATE;
    
    @PostConstruct
    public void init(){
        
        
    }

    public String getDEFAULT_TEMPLATE() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        DEFAULT_TEMPLATE = ec.getInitParameter(DEFAULT_TEMPLATE_NAME);
        return DEFAULT_TEMPLATE;
    }
    
    
}
