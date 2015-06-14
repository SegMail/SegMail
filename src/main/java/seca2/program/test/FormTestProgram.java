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
import eds.entity.user.User;
import eds.entity.user.UserType;
import javax.inject.Inject;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author KH
 */
@RequestScoped
@Named("FormTestProgram")
public class FormTestProgram implements Serializable {
    
    @Inject private ProgramTest programTest;
    
    @EJB private ProgramService programService;
    @EJB private UserService userService;
    
    private final String TestCreateProgramFormName = "createProgramForm";
        
    //Create Program Form input variables
    private String programName;
    private String displayName;
    private String programViewRoot;
    private String displayDesc;
    
    private final String TestAssignProgramToUserTypeName = "assignProgramToUsertypeForm";
    
    //Assign Program to UserType Form input variables
    //private List<Program> allPrograms;
    private long selectedProgramId;
    private List<UserType> allUserTypes;
    private long selectedUserTypeId;
    
    @PostConstruct
    public void init(){
        
    }
    
    public void createProgram(){
        try{
            programService.registerProgram(programName, programViewRoot, displayName, displayDesc);
            FacesMessenger.setFacesMessage(TestCreateProgramFormName, FacesMessage.SEVERITY_FATAL, "Program "+programName+" successfully created!", null);
        } catch(DBConnectionException dbex){
            FacesMessenger.setFacesMessage(TestCreateProgramFormName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        }
        catch(Exception ex){
            FacesMessenger.setFacesMessage(TestCreateProgramFormName, FacesMessage.SEVERITY_ERROR, 
                    ex.getClass().getSimpleName(), 
                    ex.getMessage());
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
                    ex.getClass().getSimpleName(), 
                    ex.getMessage());
        }
    }
   
    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProgramViewRoot() {
        return programViewRoot;
    }

    public void setProgramViewRoot(String programViewRoot) {
        this.programViewRoot = programViewRoot;
    }

    public String getDisplayDesc() {
        return displayDesc;
    }

    public void setDisplayDesc(String displayDesc) {
        this.displayDesc = displayDesc;
    }

    public List<Program> getAllPrograms() {
        return this.programTest.getAllPrograms();
    }

    public long getSelectedProgramId() {
        return selectedProgramId;
    }

    public void setSelectedProgramId(long selectedProgramId) {
        this.selectedProgramId = selectedProgramId;
    }

    public List<UserType> getAllUserTypes() {
        return this.programTest.getAllUserTypes();
    }

    public long getSelectedUserTypeId() {
        return selectedUserTypeId;
    }

    public void setSelectedUserTypeId(long selectedUserTypeId) {
        this.selectedUserTypeId = selectedUserTypeId;
    }
    
    /**
     * Private access only to other testing forms
    */
    void assignAllProgramsToUser(String username){
        // Refresh ProgramTest
        this.programTest.init();
        
        List<UserType> userTypes = this.userService.getUserTypeByName(username);
        if(userTypes == null || userTypes.isEmpty())
            throw new RuntimeException("No usertypes found.");
        
        UserType usertype = userTypes.get(0);
        this.setSelectedUserTypeId(usertype.getOBJECTID());
        for(Program p : this.getAllPrograms()){
            this.setSelectedProgramId(p.getOBJECTID());
            this.assignProgramAccess();
        }
        
    }
    
}
