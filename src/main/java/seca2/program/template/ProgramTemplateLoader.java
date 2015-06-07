/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.template;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * This is a request scoped loader that solves 2 problems: 1) The SessionScoped
 * problem - You need a sessionscoped object to cache all that session-related
 * variables like UserContainers, MenuContainers, ClientContainers, but you
 * don't want to re-query for every single requests, especially postbacks,
 * except when you are loading the page again. This loader can help to load
 * these variables automatically only in the following scenarios: a) When the
 * page/program is loaded by clicking a link. b) When a request has been
 * completed and page/program is reloaded.
 *
 * 2) The RequestScoped problem - After you thought of using a RequestScoped
 * bean to solve problem 1), you realize that if you just create a bean in the
 * JSF context without any xhtml page to call that bean, it would not be
 * instantiated, even though it is injected, unless its variables or methods are
 * assessed. So in order for this bean to load its @PostConstuct methods. - We
 * may not need to associate the loader to all requestscoped beans.
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("ProgramTemplateLoader")
public class ProgramTemplateLoader {

    // Parent program bean
    @Inject
    private ProgramTemplate program;

    private final String formName = "ProgramTemplateLoader";

    @PostConstruct
    public void init() {
        if (FacesContext.getCurrentInstance().isPostback() && program != null) {
            
        } else {
            load();
        }
    }

    /**
     * Reminds the SessionScoped program bean to reload all its page variables 
     * in each non-postback request.
     */
    public void load() {
        this.program.init();

    }

    public String getFormName() {
        return formName;
    }

    public ProgramTemplate getProgram() {
        return program;
    }

    public void setProgram(ProgramTemplate program) {
        this.program = program;
    }

    
}
