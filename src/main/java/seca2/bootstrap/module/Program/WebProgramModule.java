/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Program;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.CoreModule;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.UserSessionContainer;

/**
 * This is a WEB server version of ProgramModule. It focuses on program names and 
 * does not query the DB for authorization access - instead, it parses the view 
 * location by just looking at the program name and the parameters as well.
 * 
 * 
 * @author LeeKiatHaw
 */
@CoreModule
public class WebProgramModule extends BootstrapModule implements Serializable {
    
    @Inject UserSessionContainer sessionContainer;
    @Inject UserRequestContainer requestContainer;

    @Override
    protected boolean execute(ServletRequest request, ServletResponse response) throws Exception {
        if(requestContainer.getPathParser().containsFileResource())
            return true;
        
        String programName = requestContainer.getProgramName();
        String viewLocation = "/programs/"+programName+"/layout.xhtml";
        String realPath = request.getServletContext().getRealPath(viewLocation);
        
        //If the xhtml file is not found, point to the not found page.
        if(realPath == null || requestContainer.isError()) {
            viewLocation = "/programs/error/error.xhtml";
        }
        
        requestContainer.setViewLocation(viewLocation);
        
        requestContainer.setProgramParamsOrdered(requestContainer.getPathParser().getOrderedParams());
        
        return true;
    }

    @Override
    protected void ifFail(ServletRequest request, ServletResponse response) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response, Exception ex) {
        
    }

    @Override
    protected int executionSequence() {
        return Integer.MIN_VALUE + 401;
    }

    @Override
    protected boolean inService() {
        return true;
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
        return false;
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
        return "WebProgramModule";
    }
    
}
