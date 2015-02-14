/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Program;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import seca2.bootstrap.BootstrapInput;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.BootstrapOutput;
import seca2.bootstrap.CoreModule;
import seca2.bootstrap.module.User.UserModule;
import seca2.component.data.DBConnectionException;
import seca2.component.program.ProgramService;
import seca2.entity.program.Program;

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

    private List<String> programNames; //stud at this moment
    private List<Program> programs2;
    private int currentProgramIndex;
    public static final int DEFAULT_PROGRAM = 0;
    
    @EJB private ProgramService programService;

    @PostConstruct
    public void init() {
        //create a stub first, next time then we'll implement the actual thing
        programNames = new ArrayList<String>();

        programNames.add("test");
        programNames.add("sendmail");
        programNames.add("signupforms");
        programNames.add("lists");
        programNames.add("subscribers");
        programNames.add("campaigns");
        programNames.add("mysettings");

        programs2 = new ArrayList<Program>();

        for (int i = 0; i < programNames.size(); i++) {
            Program program = new Program();
            program.setPROGRAM_NAME(programNames.get(i));

            String dir = programNames.get(i).toLowerCase();
            program.setOBJECTID(i);
            program.setBEAN_DIRECTORY("seca2.program." + dir);
            program.setVIEW_DIRECTORY("/program/" + dir + "/");
            program.setVIEW_ROOT("/programs/" + dir + "/layout.xhtml");
            //program.setPROGRAM_ID(i);//not correct, just for the time being

            programs2.add(program);
        }
    }

    public Program getCurrentProgram() {
        return this.programs2.get(this.currentProgramIndex);
    }

    public List<String> getProgramNames() {
        return programNames;
    }

    public void setProgramNames(List<String> programs) {
        this.programNames = programs;
    }

    public int getCurrentProgramIndex() {
        return currentProgramIndex;
    }

    public void setCurrentProgramIndex(int currentProgramIndex) {
        this.currentProgramIndex = currentProgramIndex;
    }

    public List<Program> getPrograms2() {
        return programs2;
    }

    public void setPrograms2(List<Program> programs) {
        this.programs2 = programs;
    }

    @Override
    protected int executionSequence() {
        return -97;
    }

    @Override
    protected boolean execute(BootstrapInput inputContext, BootstrapOutput outputContext) {
        //Hardcoded for testing
        outputContext.setPageRoot("/programs/test/layout.xhtml");
        outputContext.getNonCoreValues().put("TEST_MENU", this.programs2);

        /*
         Actual processing
         1) Check the program requested by retrieving it from inputContext.
         */
        String program = inputContext.getProgram();
        try {
            /*
            2) Retrieve the program from database by calling ProgramServices.
            */
            List<Program> programs = programService.getProgramByName(program);
            System.out.println(programs); //debug
            
            //select only the first result
            Program programObject = programs.get(0);
            outputContext.setPageRoot(programObject.getVIEW_ROOT());
            
            /*
            3) Authorization checks for program access by calling ProgramServices.
            */
        } catch (DBConnectionException ex) {
            //Set error page and stop processing
            return false;
        }

        return true;
    }

    @Override
    protected boolean inService() {
        return true;
    }

}
