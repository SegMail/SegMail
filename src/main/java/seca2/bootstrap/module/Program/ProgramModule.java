/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Program;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.hibernate.Session;
import seca2.bootstrap.BootstrapModule;
import seca2.bootstrap.module.User.UserModule;
import seca2.component.data.HibernateEMServices;
import seca2.component.data.HibernateUtil;
import seca2.entity.program.Program;

/**
 *
 * @author vincent.a.lee
 */
@Named("ProgramModule")
@SessionScoped
public class ProgramModule extends BootstrapModule implements Serializable {

    private List<String> programNames; //stud at this moment
    private List<Program> programs;
    private int currentProgramIndex;
    public static final int DEFAULT_PROGRAM = 0;

    @Inject private UserModule userModule;
    

    @PostConstruct
    public void init() {
        //create a stub first, next time then we'll implement the actual thing
        programNames = new ArrayList<String>();
        if (userModule.checkSessionActive()) {
            programNames.add("test");
            programNames.add("sendmail");
            programNames.add("signupforms");
            programNames.add("lists");
            programNames.add("subscribers");
            programNames.add("campaigns");
            programNames.add("mysettings");

        }
        else{
            programNames.add("test");
            programNames.add("sendmail");
            programNames.add("signupforms");
            programNames.add("lists");
            programNames.add("subscribers");
            programNames.add("campaigns");
            programNames.add("mysettings");

        }

        programs = new ArrayList<Program>();

        for (int i = 0; i < programNames.size(); i++) {
            Program program = new Program();
            program.setPROGRAM_NAME(programNames.get(i));

            String dir = programNames.get(i).toLowerCase();
            program.setOBJECTID(i);
            program.setBEAN_DIRECTORY("seca2.program." + dir);
            program.setVIEW_DIRECTORY("/program/" + dir + "/");
            program.setVIEW_ROOT("/programs/" + dir + "/layout.xhtml");
            //program.setPROGRAM_ID(i);//not correct, just for the time being

            programs.add(program);
        }
    }

    public Program getCurrentProgram() {
        return this.programs.get(this.currentProgramIndex);
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

    public List<Program> getPrograms() {
        return programs;
    }

    public void setPrograms(List<Program> programs) {
        this.programs = programs;
    }

    @Override
    protected void doStuff(Map<String, Object> input, Map<String, Object> output) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    

}
