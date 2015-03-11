/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Program;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import seca2.bootstrap.BootstrapInput;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.BootstrapOutput;
import seca2.bootstrap.CoreModule;
import eds.component.data.DBConnectionException;
import eds.component.program.ProgramService;
import eds.entity.program.Program;

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
        String program = inputContext.getProgram();
        try {
            /*
             2) Retrieve the program from database by calling ProgramServices.
             */
            //List<Program> programs = programService.getProgramByName(program);
            String viewRoot = programService.getViewRootFromProgramName(program);
            System.out.println(viewRoot); //debug
            Program programObject;

            //If there are results returned, select only the first result
            if (viewRoot != null && !viewRoot.isEmpty()) {
                
                outputContext.setPageRoot(viewRoot);
                return true;
            } 
            
            //else {//if no results returned, show the error page
                //Hardcoded for testing
            outputContext.setPageRoot("/programs/test/layout.xhtml");
            
            //}

            /*
             3) Authorization checks for program access by calling ProgramServices.
             */
        } catch (DBConnectionException ex) {
            //Set error page and stop processing
            //return false;
            outputContext.setPageRoot("/programs/test/layout.xhtml");
            
        }

        return true;
    }

    @Override
    protected boolean inService() {
        return true;
    }

}
