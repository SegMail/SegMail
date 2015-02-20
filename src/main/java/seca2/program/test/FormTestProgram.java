/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.test;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import eds.component.data.DBConnectionException;
import eds.component.program.ProgramService;
import eds.component.user.UserService;
import eds.entity.program.Program;
import eds.entity.user.UserType;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author KH
 */
@RequestScoped
@Named("FormTestProgram")
public class FormTestProgram implements Serializable {
    
    @EJB private ProgramService programService;
    @EJB private UserService userService;
    
    private final String TestCreateProgramFormName = "createProgramForm";
        
    //Create Program Form input variables
    private String programName;
    private String programDirectory;
    private String programViewRoot;
    private String programBeanLocation;
    
    private final String TestAssignProgramToUserTypeName = "assignProgramToUsertypeForm";
    
    //Assign Program to UserType Form input variables
    private List<Program> allPrograms;
    private long selectedProgramId;
    private List<UserType> allUserTypes;
    private long selectedUserTypeId;
    
    @PostConstruct
    public void init(){
        this.initializeAllProgram();
        this.initializeAllUserTypes();
    }
    
    public void createProgram(){
        try{
            programService.registerProgram(programName, programDirectory, programViewRoot, programBeanLocation);
            FacesMessenger.setFacesMessage(TestCreateProgramFormName, FacesMessage.SEVERITY_FATAL, "Program "+programName+" successfully created!", null);
        } catch(DBConnectionException dbex){
            FacesMessenger.setFacesMessage(TestCreateProgramFormName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        }
        catch(Exception ex){
            FacesMessenger.setFacesMessage(TestCreateProgramFormName, FacesMessage.SEVERITY_ERROR, 
                    ex.getCause().getClass().getSimpleName(), 
                    ex.getCause().getMessage());
        }
    }
    
    public void assignProgramAccess(){
        try{
            programService.assignProgramToUserType(this.selectedProgramId, this.selectedUserTypeId);
            FacesMessenger.setFacesMessage(TestAssignProgramToUserTypeName, FacesMessage.SEVERITY_FATAL, "Program "+selectedProgramId+" successfully assigned to UserType "+this.selectedUserTypeId+"!", null);
        } catch(DBConnectionException dbex){
            FacesMessenger.setFacesMessage(TestAssignProgramToUserTypeName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        }
        catch(Exception ex){
            FacesMessenger.setFacesMessage(TestAssignProgramToUserTypeName, FacesMessage.SEVERITY_ERROR, 
                    ex.getCause().getClass().getSimpleName(), 
                    ex.getCause().getMessage());
        }
    }
    
    public void initializeAllProgram(){
        try{
            this.allPrograms = programService.getAllPrograms();
            
        } catch(DBConnectionException dbex){
            FacesMessenger.setFacesMessage(TestCreateProgramFormName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        }
        catch(Exception ex){
            FacesMessenger.setFacesMessage(TestCreateProgramFormName, FacesMessage.SEVERITY_ERROR, 
                    ex.getCause().getClass().getSimpleName(), 
                    ex.getCause().getMessage());
        }
    }
    
    public void initializeAllUserTypes(){
        try{
            this.allUserTypes = userService.getAllUserTypes();
            
        } catch(DBConnectionException dbex){
            FacesMessenger.setFacesMessage(TestCreateProgramFormName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        }
        catch(Exception ex){
            FacesMessenger.setFacesMessage(TestCreateProgramFormName, FacesMessage.SEVERITY_ERROR, 
                    ex.getCause().getClass().getSimpleName(), 
                    ex.getCause().getMessage());
        }
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramDirectory() {
        return programDirectory;
    }

    public void setProgramDirectory(String programDirectory) {
        this.programDirectory = programDirectory;
    }

    public String getProgramViewRoot() {
        return programViewRoot;
    }

    public void setProgramViewRoot(String programViewRoot) {
        this.programViewRoot = programViewRoot;
    }

    public String getProgramBeanLocation() {
        return programBeanLocation;
    }

    public void setProgramBeanLocation(String programBeanLocation) {
        this.programBeanLocation = programBeanLocation;
    }

    public List<Program> getAllPrograms() {
        return allPrograms;
    }

    public void setAllPrograms(List<Program> allPrograms) {
        this.allPrograms = allPrograms;
    }

    public long getSelectedProgramId() {
        return selectedProgramId;
    }

    public void setSelectedProgramId(long selectedProgramId) {
        this.selectedProgramId = selectedProgramId;
    }

    public List<UserType> getAllUserTypes() {
        return allUserTypes;
    }

    public void setAllUserTypes(List<UserType> allUserTypes) {
        this.allUserTypes = allUserTypes;
    }

    public long getSelectedUserTypeId() {
        return selectedUserTypeId;
    }

    public void setSelectedUserTypeId(long selectedUserTypeId) {
        this.selectedUserTypeId = selectedUserTypeId;
    }
    
    
}
