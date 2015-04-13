/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.mysettings;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("MySettingsProgram")
public class MySettingsProgram implements Serializable {
    
    private final String pageName = "my_settings_program";
    
    private String testActivateInjectedBean = "";
    
    @PostConstruct
    public void init(){
        
    }

    public String getPageName() {
        return pageName;
    }

    public String getTestActivateInjectedBean() {
        return testActivateInjectedBean;
    }

    public void setTestActivateInjectedBean(String testActivateInjectedBean) {
        this.testActivateInjectedBean = testActivateInjectedBean;
    }
    
    
}
