/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Program;

import eds.component.navigation.NavigationService;
import eds.component.program.ProgramService;
import eds.entity.program.Program;
import java.io.Serializable;
import java.util.ArrayList;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.CoreModule;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import seca2.bootstrap.DefaultSites;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.UserSessionContainer;
import segurl.filter.SegURLResolver;

/**
 *
 * @author vincent.a.lee
 */
//@Named("ProgramModule")
//@RequestScoped
//@BootstrapRequest
//@BootstrapType(postback=false)
@CoreModule
public class ProgramModule extends BootstrapModule implements Serializable {

    @Inject
    UserSessionContainer sessionContainer;
    @Inject
    UserRequestContainer requestContainer;

    @Inject
    DefaultSites defaults;

    @EJB
    ProgramService programService;

    @Override
    protected boolean execute(ServletRequest request, ServletResponse response) {

        /**
         * Bypass if it's a file request
         * Cannot, because /program/index.xhtml is also a file.
         * We can either
         * 1) enhance SeqURLResolver to recognize /index.xhtml or
         * 2) hardcode for now...
         */
        if (SegURLResolver.containsFile(((HttpServletRequest) request).getRequestURI())) {
            return true;
        }

        long userTypeId = sessionContainer.getUserType().getOBJECTID();

        String programName = requestContainer.getProgramName();
        Program program = sessionContainer.getCurrentProgram();

        if (programName == null || program == null
                || !programName.equalsIgnoreCase(program.getPROGRAM_NAME())) {
            program = programService.getProgramForUserType(programName, userTypeId);
        }

        //If no matching program is found and no default program, stop processing and 
        //show the error page
        if (program == null) {
            requestContainer.setViewLocation(defaults.ERROR_PAGE);
            requestContainer.setError(true);
            return true;
        }
        //If found, set the viewRoot location
        requestContainer.setViewLocation(program.getVIEW_ROOT());
        //Must return true no matter what, else FacesServlet will not get called
        return true;

    }

    @Override
    protected void ifFail(ServletRequest request, ServletResponse response) {
        //This module doesn't require failing, so no need to code this 
    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response) {

    }

    @Override
    protected int executionSequence() {
        return Integer.MIN_VALUE + 2;
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
        return "ProgramModule";
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

}
