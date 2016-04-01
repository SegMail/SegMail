/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Program;

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
import seca2.bootstrap.DefaultKeys;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.UserSessionContainer;

/**
 * A Program in SegERP is a single end-point interface of the system, or a "page"
 * that a user goes to for a set of similar functionalities. For example, the 
 * User program is a page that contains all user CRUD operations like maintaining
 * usernames, passwords, user groups, etc. 
 * <br>
 * In SegERP, each Program has its own page, which is easily accessed by a URL
 * /[program name]. In the page, there can be many forms that are used for 
 * processing data. 
 * <br>
 * You can also pass initial parameters to a Program and have it initialize its 
 * own page or form data elements. For example /shop?categories=phones&item=iphone6s&country=sg
 * will bring you to the "shop" program and initializes the program parameters
 * "categories", "item" and "country". How each Program deals with these parameters
 * can be customized.
 * 
 *  
 * 
 * @author vincent.a.lee
 */
@CoreModule
public class ProgramModule extends BootstrapModule implements Serializable {
    
    @Inject UserSessionContainer sessionContainer;
    @Inject UserRequestContainer requestContainer;
    @Inject DefaultKeys defaults;

    @EJB ProgramService programService;

    /**
     * The request variable programName is the "dirty" indicator of whether to
     * load the current program or not. Only under the following circumstances
     * should it reload the current program:
     * <ol>
     * <li>Access to the root directory without indicating any program name / -> programName is null</li>
     * <li>Access to a specific program name that is different from the last request
     * -> programName <> program.PROGRAM_NAME</li>
     *  </ol>
     * 
     * If it is a file request, just return true for the bootstrapping chain to 
     * proceed immediately, do not proceed further.
     * 
     * @param request
     * @param response
     * @return 
     */
    @Override
    protected boolean execute(ServletRequest request, ServletResponse response) {

        /**
         * Bypass if it's a file request
         * Cannot, because /program/index.xhtml is also a file.
         * We can either
         * 1) enhance SeqURLResolver to recognize /index.xhtml or
         * 2) hardcode for now...
         */
        /*if (SegURLResolver.getResolver().addExclude("index.xhtml").containsFile(((HttpServletRequest) request).getRequestURI())) {
            return true;
        }*/
        if(requestContainer.getPathParser().containsFileResource())
            return true;
        
        //If it is a webservice call, bypass processing
        if(requestContainer.isWebservice())
            return true;
        
        long userTypeId = (sessionContainer.getUserType() == null) ? 
                -1 : sessionContainer.getUserType().getOBJECTID();

        String programName = requestContainer.getProgramName();
        Program program = sessionContainer.getCurrentProgram();
        
        //Get the default program if it is an access to / 
        if(programName == null || programName.isEmpty()) 
            programName = request.getServletContext().getInitParameter(defaults.GLOBAL_DEFAULT_PROGRAM);
        
        //Instead of deciding when to reload, why not decide when not to?
        //1) When it's a specific program but the existing program in session is 
        // the same
        if(programName == null || programName.isEmpty()
                || program == null
                || !programName.equalsIgnoreCase(program.getPROGRAM_NAME()))
            program = reloadProgram(programName,userTypeId);
        
        //If no matching program is found and no default program, stop processing and 
        //show the error page
        if (program == null) {
            requestContainer.setViewLocation(defaults.ERROR_PAGE);
            requestContainer.setErrorMessage(this.getName()+": No programs found");
            requestContainer.setError(true);
            return true;
        }
        
        //If found, set the viewRoot location
        requestContainer.setProgramName(program.getPROGRAM_NAME());//This is still needed for other modules to acces!
        sessionContainer.setCurrentProgram(program);
        requestContainer.setViewLocation(program.getVIEW_ROOT());
        
        //Set params
        requestContainer.setProgramParamsOrdered(requestContainer.getPathParser().getOrderedParams());
        //requestContainer.setPogramParamsNamed(request.getParameterMap());
        //Must return true no matter what, else FacesServlet will not get called
        return true;
    }

    @Override
    protected void ifFail(ServletRequest request, ServletResponse response) {
        //This module doesn't require failing, so no need to code this 
    }

    @Override
    protected void ifException(ServletRequest request, ServletResponse response, Exception ex) {

    }

    @Override
    protected int executionSequence() {
        return Integer.MIN_VALUE + 400;
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

    /**
     * This is called during deployment, not at the start of every request so 
     * it is possible to set inService() here.
     * 
     * @param filterConfig
     * @throws ServletException 
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
    
    /**
     * Helper method for reloading program
     */
    private Program reloadProgram(String programName, long userTypeId){
         
        //The authorized programs come first 
        //Try to retrieve the authorized programs first
        Program newProgram = programService.getProgramForUserType(programName, userTypeId);
        
        //If it's a found public program
        if(newProgram == null){
            Program publicProgram = programService.getSingleProgramByName(programName);
            if(publicProgram != null && publicProgram.isIS_PUBLIC())
                newProgram = publicProgram;
        }
        
        return newProgram;
    }
    

    @Override
    protected boolean bypassDuringInstall() {
        return true; //To change body of generated methods, choose Tools | Templates.
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
