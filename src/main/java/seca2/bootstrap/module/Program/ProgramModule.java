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
import java.util.logging.Level;
import java.util.logging.Logger;
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

        try {
            //Very coupled to JSF
            FacesContext fc = FacesContext.getCurrentInstance();
            //1. Find the program object
            //- If empty, assign the default program
            //- If not empty, continue.
            if (inputContext.getProgram() == null || inputContext.getProgram().isEmpty()) {
                String defaultProgram = "";
                //FacesContext fc = (FacesContext) inputContext.getFacesContext();
                
                
                //Rightfully, we should check if the user has a default program configured, if yes, we
                //should get it before we move on to the default program.
                //- User
                //- UserType
                //- Site/Client

                //Actually, even in the production environment, there can also be a default program set.
                //[20150328] There is no need to restrict this feature to the development stage
                //if (fc.getApplication().getProjectStage().equals(ProjectStage.Development)) {
                    defaultProgram = fc.getExternalContext().getInitParameter("GLOBAL_DEFAULT_PROGRAM");
                    inputContext.setProgram(defaultProgram);
                //}
                
                //If totally no default program is found, throw exception
                if(defaultProgram.isEmpty())
                    throw new Exception("No default program found.");
                
                //Assign default program
                inputContext.setProgram(defaultProgram);
            }
            String program = inputContext.getProgram();

            //2. Find the viewRoot of the program, which cannot be empty by now
            //- If not found, throw exception.
            //- If not found but project is in development stage, get the GLOBAL_DEFAULT_VIEWROOT
            //- If found, continue.
            String viewRoot = this.programService.getViewRootFromProgramName(program);
            String defaultViewRoot = fc.getExternalContext().getInitParameter("GLOBAL_DEFAULT_VIEWROOT");
            
            if(viewRoot == null || viewRoot.isEmpty()){
                if(defaultViewRoot == null || defaultViewRoot.isEmpty())
                    throw new Exception("View root location is not found for program "+program+".");
                
                viewRoot = defaultViewRoot;
            }
            
            //3. Check the user's authorization.
            //By this time, the program name and viewRoot cannot be empty
            //- If not authorized, show the not authorized view root.
            //- If found, continue.
            
            //To check authorization, you need to first know if the person is authorized
            //By passing through the UserModule, the user should already been authorized
            boolean authorized = false;
            if(this.userContainer.getUserType() != null){
                authorized = this.programService.checkProgramAuthForUserType(userContainer.getUserType().getOBJECTID(), program);
            }
            
            //4. Decide what to show
            if(!authorized && 
                    //If the SETUP flag is set in web.xml but user is not authorized, show the default viewroot anyway
                    fc.getExternalContext().getInitParameter("SETUP").compareToIgnoreCase("true") != 0){
                
                String noAuthView = fc.getExternalContext().getInitParameter("NO_AUTHORIZATION_VIEWROOT");
                outputContext.setPageRoot(noAuthView);
                return true;
            }
            
            //authenticated
            outputContext.setPageRoot(viewRoot);
            return true;
                
        } catch (DBConnectionException dbex) {
            outputContext.setErrorMessage(dbex.getMessage());
            StringWriter sw = new StringWriter();
            dbex.printStackTrace(new PrintWriter(sw));
            outputContext.setErrorStackTrace(sw.toString());
            outputContext.setTemplateRoot(defaultSites.ERROR_PAGE_TEMPLATE);
            outputContext.setPageRoot(defaultSites.ERROR_PAGE);

            return false;
            
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

    //@Override
    protected boolean execute1(BootstrapInput inputContext, BootstrapOutput outputContext) {

        /*
         Actual processing
         1) Check the program requested by retrieving it from inputContext.
         */
        //If there is no program entered in the URL, set the global default program
        //[20150314] First, we should get the user's own default program, but we
        // de-prioritize this at the moment.
        if (inputContext.getProgram() == null || inputContext.getProgram().isEmpty()) {
            //FacesContext fc = (FacesContext) inputContext.getFacesContext();
            FacesContext fc = FacesContext.getCurrentInstance();

            //Actually, even in the production environment, there can also be a default program set.
            if (!fc.getApplication().getProjectStage().equals(ProjectStage.Production)) {
                String defaultProgram = fc.getExternalContext().getInitParameter("GLOBAL_DEFAULT_PROGRAM");
                inputContext.setProgram(defaultProgram);
            }
        }
        String program = inputContext.getProgram();

        try {
            /*
             * 2) Authorization checks for program access by calling ProgramServices.
             * - If user is authorized, then call ProgramService to check for authorization.
             * - If user is not, then treat user as unauthorized, as it does not matter.
             */
            boolean authorized = false;
            if (userContainer.isLoggedIn()) {
                authorized = this.programService.checkProgramAuthForUserType(userContainer.getUserType().getOBJECTID(), program);
            }

            /*
             3) Retrieve the program from database by calling ProgramServices.
             */
            String viewRoot = programService.getViewRootFromProgramName(program);
            //FacesContext fc = (FacesContext) inputContext.getFacesContext();
            FacesContext fc = FacesContext.getCurrentInstance();

            if (fc.getApplication().getProjectStage().equals(ProjectStage.Development)) {
                System.out.println(viewRoot); //debug
            }
            //If there are no results returned and it is in the development stage,
            //show the user the default viewroot
            //Note that it is necessary to have both a GLOBAL_DEFAULT_PROGRAM and GLOBAL_DEFAULT_VIEWROOT
            //because they could be different when setting up the database. 
            if ((viewRoot == null || viewRoot.isEmpty())
                    && !fc.getApplication().getProjectStage().equals(ProjectStage.Production)) {
                viewRoot = fc.getExternalContext().getInitParameter("GLOBAL_DEFAULT_VIEWROOT");;
                //Don't end this execute() yet! Need to check if user is authorized later.
                //outputContext.setPageRoot(testing);
                //return true;
            }

            if ((viewRoot == null || viewRoot.isEmpty())) {
                throw new Exception("No default program set. Please contact administrator to set it for you so "
                        + "that you can get started! "
                        + "Alternatively, you can type in the name of the program in the URL above like this: "
                        + "\"[site url]/program/[program name]/");
            }

            //Netbeans tell me that the viewRoot null and empty check is actually redundant because it would 
            //already have been filtered off in the above IF statement, but what the hack, let's just put it 
            //here for clarity purpose.
            if (authorized && (viewRoot != null && !viewRoot.isEmpty())) {
                //We use web.xml at this moment, but in the future this should be 
                viewRoot = fc.getExternalContext().getInitParameter("NO_AUTHORIZATION_VIEWROOT");
                outputContext.setPageRoot(viewRoot);
                return true;
            }

            //If there is a viewroot found, but user is not authorized, show the unathorized page and continue bootstrapping chain
            /*if(     (viewRoot != null && !viewRoot.isEmpty()) &&
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
             }*/
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

    /*=================Helper methods=====================*/
}
