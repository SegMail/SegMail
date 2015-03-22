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
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.faces.application.ProjectStage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import seca2.bootstrap.module.User.UserContainer;

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
    
    @Inject
    private UserContainer userContainer;

    @PostConstruct
    public void init() {
        
    }
    
    @Override
    protected int executionSequence() {
        return -98;
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
            //FacesContext fc = (FacesContext) inputContext.getFacesContext();
            FacesContext fc = FacesContext.getCurrentInstance();
            if(!fc.getApplication().getProjectStage().equals(ProjectStage.Production)){
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
            boolean authorized = this.programService.checkProgramAuthForUser(userContainer.getUserType().getOBJECTID(), program);
            
            /*
             3) Retrieve the program from database by calling ProgramServices.
             */
            String viewRoot = programService.getViewRootFromProgramName(program);
            //FacesContext fc = (FacesContext) inputContext.getFacesContext();
            FacesContext fc = FacesContext.getCurrentInstance();
            
            if(fc.getApplication().getProjectStage().equals(ProjectStage.Development))
                System.out.println(viewRoot); //debug

            //If there are no results returned and it is in the development stage,
            //show the user the default viewroot
            //Note that it is necessary to have both a GLOBAL_DEFAULT_PROGRAM and GLOBAL_DEFAULT_VIEWROOT
            //because they could be different when setting up the database. 
            if (    (viewRoot == null || viewRoot.isEmpty()) &&
                    !fc.getApplication().getProjectStage().equals(ProjectStage.Production)) {
                String testing = fc.getExternalContext().getInitParameter("GLOBAL_DEFAULT_VIEWROOT");
                outputContext.setPageRoot(testing);
                return true;
            } 
            
            //If there is a viewroot found, but user is not authorized, show the unathorized page and continue bootstrapping chain
            if(     (viewRoot != null && !viewRoot.isEmpty()) &&
                    !authorized){
                String noAuthView = fc.getExternalContext().getInitParameter("NO_AUTHORIZATION_VIEWROOT");
                outputContext.setPageRoot(noAuthView);
                return true;
            }
            
            //If there is a viewroot found, and user is authorized, show the viewroot.
            if(     (viewRoot != null && !viewRoot.isEmpty()) &&
                    authorized){
                outputContext.setPageRoot(viewRoot);
                return true;
            }
            
            throw new Exception("Program request processing error.");
            
            
        } catch (DBConnectionException dbex) {
            //Set error page and stop processing
            //return false;
            outputContext.setErrorMessage(dbex.getMessage());
            StringWriter sw = new StringWriter();
            dbex.printStackTrace(new PrintWriter(sw));
            outputContext.setErrorStackTrace(sw.toString());
            outputContext.setTemplateRoot(defaultSites.ERROR_PAGE_TEMPLATE);
            outputContext.setPageRoot(defaultSites.ERROR_PAGE);
                
            return false;
            //throw ex; //It's not a programming error, so 
        } catch (Exception ex) {
            outputContext.setErrorMessage(ex.getMessage());
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            outputContext.setErrorStackTrace(sw.toString());
            outputContext.setTemplateRoot(defaultSites.ERROR_PAGE_TEMPLATE);
            outputContext.setPageRoot(defaultSites.ERROR_PAGE);
            
            return false;
        }
    }

    @Override
    protected boolean inService() {
        return true;
    }

}
