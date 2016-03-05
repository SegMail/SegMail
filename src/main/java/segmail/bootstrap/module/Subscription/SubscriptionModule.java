/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.bootstrap.module.Subscription;

import java.io.Serializable;
import java.util.List;
import javax.servlet.DispatcherType;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.NonCoreModule;

/**
 *
 * @author LeeKiatHaw
 */
@NonCoreModule
public class SubscriptionModule extends BootstrapModule implements Serializable {

    @Override
    protected boolean execute(ServletRequest request, ServletResponse response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void ifFail(ServletRequest request, ServletResponse response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected int executionSequence() {
        return Integer.MIN_VALUE;
    }

    @Override
    protected boolean inService() {
        return false;
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
    public String getName() {
        return "SubscriptionModule";
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    protected boolean bypassDuringInstall() {
        return true;
    }

    @Override
    protected boolean bypassDuringNormal() {
        return true;
    }
    
    @Override
    protected boolean bypassDuringWeb() {
        return true;
    }
}
