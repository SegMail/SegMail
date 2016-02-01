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
public class DefaultValues {
    
    public final String ERROR_PAGE = "/programs/error/error_page.xhtml";
    public final String ERROR_PAGE_TEMPLATE = "/programs/error/template/error-page-layout.xhtml";
    
    public final String LOGIN_PAGE = "/programs/user/login_page.xhtml";
    public final String LOGIN_PAGE_TEMPLATE = "/programs/user/templates/mylogintemplate/template-layout.xhtml";
    
    public final String DEFAULT_HOME = "/programs/test/layout.xhtml";
    
    private final String DEFAULT_TEMPLATE_NAME = "DEFAULT_TEMPLATE_LOCATION";
    
    /**
     * A flag to indicate if the application is in the installation mode. Some of 
     * the BootstrapModules would be turned off in this mode.
     */
    public final String INSTALL = "SETUP";
    
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
