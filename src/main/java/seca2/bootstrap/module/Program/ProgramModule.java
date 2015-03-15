/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Program;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import seca2.bootstrap.BootstrapInput;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.BootstrapOutput;
import seca2.bootstrap.CoreModule;
import eds.component.data.DBConnectionException;
import eds.component.program.ProgramService;
import javax.faces.context.FacesContext;

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

    public static final int DEFAULT_PROGRAM = 0;

    @EJB
    private ProgramService programService;

    @PostConstruct
    public void init() {
        
    }
    
    @Override
    protected int executionSequence() {
        return -97;
    }

    @Override
    protected boolean execute(BootstrapInput inputContext, BootstrapOutput outputContext) {
        
        /*
         Actual processing
         1) Check the program requested by retrieving it from inputContext.
         */
        
        //If there is no program entered in the URL, set the global default program
        //[20150314] First, we should get the user's own default program, but we
        // de-prioritize this at the moment.
        if(inputContext.getProgram() == null || inputContext.getProgram().isEmpty()){
            FacesContext fc = (FacesContext) inputContext.getFacesContext();
            if(fc.getExternalContext().getInitParameter("javax.faces.PROJECT_STAGE")
                    .equalsIgnoreCase("Development")){
                String defaultProgram = fc.getExternalContext().getInitParameter("GLOBAL_DEFAULT_PROGRAM");
                inputContext.setProgram(defaultProgram);
            }
        }
        String program = inputContext.getProgram();
            
        try {
            /*
             * 2) Authorization checks for program access by calling ProgramServices.
             * [20150314] De-prioritize at the moment
             */
            /*
             3) Retrieve the program from database by calling ProgramServices.
             */
            String viewRoot = programService.getViewRootFromProgramName(program);
            System.out.println(viewRoot); //debug

            //If there are results returned, select only the first result
            if (viewRoot != null && !viewRoot.isEmpty()) {
                outputContext.setPageRoot(viewRoot);
                return true;
            } 
            
            //For development purpoose, remove after deployment!
            //To get the test page if no DB exist
            FacesContext fc = (FacesContext) inputContext.getFacesContext();
            String testing = fc.getExternalContext().getInitParameter("GLOBAL_DEFAULT_VIEWROOT");
            outputContext.setPageRoot(testing);
            return true;
            
        } catch (DBConnectionException ex) {
            //Set error page and stop processing
            //return false;
            outputContext.setPageRoot("/programs/error/error_page.xhtml");
            return false;
            //throw ex; //It's not a programming error, so 
        }
    }

    @Override
    protected boolean inService() {
        return true;
    }

}
