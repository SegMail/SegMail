/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.install;

import java.io.Serializable;
import java.util.List;
import javax.servlet.DispatcherType;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.CoreModule;

/**
 *
 * @author LeeKiatHaw
 */
@CoreModule
public class InstallationModule extends BootstrapModule implements Serializable{

    @Override
    protected boolean execute(ServletRequest request, ServletResponse response) {
        
        //1) Check if the database has been initialized by calling a few DB services
        //2) If app is considered installed, return true to continue the chain processing
        //3) If app is not installed, display the installation page.
        return true;
    }

    @Override
    protected int executionSequence() {
        return -100; //How to inject this value from web.xml?
    }

    @Override
    protected boolean inService() {
        return false;
    }

    @Override
    public String getName() {
        return "InstallationModule";
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    protected void ifFail(ServletRequest request, ServletResponse response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String urlPattern() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<DispatcherType> getDispatchTypes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
