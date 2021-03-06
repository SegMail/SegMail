/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Layout;

import eds.component.layout.LayoutService;
import eds.entity.layout.Layout;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.CoreModule;
import seca2.bootstrap.DefaultKeys;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.UserSessionContainer;

/**
 *
 * @author vincent.a.lee
 */
@CoreModule
public class LayoutModule extends BootstrapModule implements Serializable {
    
    @Inject UserSessionContainer sessionContainer;
    @Inject UserRequestContainer requestContainer;
    
    @EJB private LayoutService layoutService;
    
    @Override
    protected boolean execute(ServletRequest request, ServletResponse response) {
        
        //Bypass if it's a file request
        if(requestContainer.getPathParser().containsFileResource())
            return true;
        
        if(requestContainer.isError()){
            String errorTemplate = request.getServletContext().getInitParameter(defaults.ERROR_TEMPLATE_LOCATION);
            requestContainer.setTemplateLocation(errorTemplate);
            return true;
        }
        
        /**
         * There is no way to keep the state of a user's layout from the request 
         * information, so it's best to keep retrieving from the DB. Unless it is 
         * delegated to the program forms to explicitly reload the layout object.
         * Performance vs correctness.
         */
        List<Layout> layouts = layoutService.getLayoutsByProgram(requestContainer.getProgramName());
        
        if(layouts == null || layouts.isEmpty())
            layouts = layoutService.getLayoutsByUserOrType(sessionContainer.getUser());
        
        if(layouts == null || layouts.isEmpty()) {
            String defaultTemplateLocation = request.getServletContext().getInitParameter(defaults.DEFAULT_TEMPLATE_LOCATION);
            requestContainer.setTemplateLocation(defaultTemplateLocation);
            return true;
        }
        
        requestContainer.setTemplateLocation(layouts.get(0).getVIEW_ROOT());
        
        return true;
    }

    @Override
    protected void ifFail(ServletRequest request, ServletResponse response) {
        
    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response, Exception ex) {
        
    }

    @Override
    protected int executionSequence() {
        return Integer.MIN_VALUE+500;
    }

    @Override
    protected boolean inService() {
        return true;
    }
    
    @Override
    protected String urlPattern() {
        return "/program/*";
    }

    @Override
    protected List<DispatcherType> getDispatchTypes() {
        List<DispatcherType> dispatchTypes = new ArrayList<DispatcherType>();
        dispatchTypes.add(DispatcherType.REQUEST);
        dispatchTypes.add(DispatcherType.FORWARD);
        
        return dispatchTypes;
    }

    @Override
    public String getName() {
        return "LayoutModule";
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
        return false;
    }


    @Override
    protected boolean bypassDuringWeb() {
        return false;
    }
}
