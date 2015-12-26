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
public class DefaultSites {
    
    public final String ERROR_PAGE = "/programs/error/error_page.xhtml";
    public final String ERROR_PAGE_TEMPLATE = "/programs/error/template/error-page-layout.xhtml";
    
    public final String LOGIN_PAGE = "/programs/user/login_page.xhtml";
    public final String LOGIN_PAGE_TEMPLATE = "/programs/user/templates/mylogintemplate/template-layout.xhtml";
    
    public final String DEFAULT_HOME = "/programs/test/layout.xhtml";
    
    private final String DEFAULT_TEMPLATE_NAME = "DEFAULT_TEMPLATE_LOCATION";
    
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
