/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.jsf.rewrite;

import seca2.bootstrap.module.Path.LogicalPathParser;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import seca2.bootstrap.DefaultKeys;
import seca2.bootstrap.UserRequestContainer;

/**
 *
 * @author LeeKiatHaw
 */
public class ControlViewHandler extends ViewHandlerWrapper {

    private ViewHandler wrapped;
    
    @Inject protected DefaultKeys defaults;
    @Inject UserRequestContainer reqContainer;

    public ControlViewHandler(ViewHandler wrapped) {
        this.wrapped = wrapped;
    }
    
    @Override
    public ViewHandler getWrapped() {
        return wrapped;
    }

    @Override
    public String getActionURL(FacesContext context, String viewId) {
        String superValue = super.getActionURL(context, viewId); //Change the url based on the mapping here!
        /*if(superValue.endsWith("index.xhtml")){
            superValue = superValue.substring(0, superValue.lastIndexOf("index.xhtml"));
        }*/
        String contextPath = context.getExternalContext().getRequestContextPath();
        String servletPath = context.getExternalContext().getRequestServletPath();
        String pathInfo = context.getExternalContext().getRequestPathInfo();
        
        String globalViewRoot = context.getExternalContext().getInitParameter(defaults.GLOBAL_VIEWROOT);
        LogicalPathParser newParser = new LogicalPathParser(servletPath.concat(pathInfo),globalViewRoot, servletPath);
        
        if(pathInfo.endsWith(globalViewRoot)){
            pathInfo = pathInfo.replace(globalViewRoot, "");
        }
        
        pathInfo = (reqContainer.getProgramName() == null) ? 
                pathInfo : "/".concat(reqContainer.getProgramName());
        
        //Add request parameters
        for(String param : reqContainer.getPathParser().getOrderedParams()) {
            pathInfo.concat("/"+param);
        }
            
        //return contextPath.concat("").concat(pathInfo); 
        return contextPath.concat(servletPath).concat(pathInfo); 
    }

    @Override
    public String deriveViewId(FacesContext context, String input) {
        String superValue = super.deriveViewId(context, input);
        return superValue; //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
