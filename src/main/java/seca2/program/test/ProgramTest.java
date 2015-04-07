/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.test;

import eds.component.data.DBConnectionException;
import eds.component.layout.LayoutService;
import eds.component.navigation.NavigationService;
import eds.component.program.ProgramService;
import eds.component.user.UserService;
import eds.entity.layout.Layout;
import eds.entity.navigation.MenuItem;
import eds.entity.program.Program;
import eds.entity.user.UserType;
import seca2.program.test.layout.FormCreateLayout;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.FormGroup;
import seca2.program.test.layout.FormAssignLayoutProgram;
import seca2.program.test.layout.FormAssignLayoutUserType;
import seca2.program.test.layout.FormAssignLayoutUsername;

/**
 *
 * @author vincent.a.lee
 */
@Named("ProgramTest")
@RequestScoped
public class ProgramTest extends FormGroup implements Serializable {
    
    private final String PROGRAM_NAME = "ProgramTest";
    
    //@Inject private FormTestDB formTestDB;
    @Inject private FormTestNavigation formTestNavigation;
    //@Inject private FormTestUser formTestUser;
    //@Inject private FormTestProgram formTestProgram;
    //@Inject private FormCreateLayout formCreateLayout;
    //@Inject private FormAssignLayoutUsername formAssignLayoutUsername;
    //@Inject private FormAssignLayoutUserType formAssignLayoutUserType;
    //@Inject private FormAssignLayoutProgram formAssignLayoutProgram;
    
    
    @EJB private UserService userService;
    @EJB private LayoutService layoutService;
    @EJB private ProgramService programService;
    @EJB private NavigationService navigationService;
    
    private List<UserType> allUserTypes = new ArrayList<UserType>();
    private List<Layout> allLayouts;
    private List<Program> allPrograms;
    private List<MenuItem> allMenuItems;
    
    @PostConstruct
    public void init(){
        initializeAllUserTypes();
        initializeAllLayout();
        initializeAllProgram();
        initializeAllMenuItems();
    }
    
    public void initializeAllUserTypes(){
         try{
            allUserTypes = userService.getAllUserTypes();
            //who knows whether there is empty list or not?
            
        }
        catch(DBConnectionException dbex){
            //Actually this part does not matter, if there is no DB connection, the BootstrapModules would be activated to display the error page.
            FacesMessenger.setFacesMessage(PROGRAM_NAME, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        }
        catch(Exception ex){
            FacesMessenger.setFacesMessage(PROGRAM_NAME, FacesMessage.SEVERITY_ERROR, 
                    ex.getCause().getClass().getSimpleName(), 
                    ex.getCause().getMessage());
        }
    } 
    public void initializeAllLayout(){
        try{
            this.allLayouts = this.layoutService.getAllLayouts();
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.PROGRAM_NAME, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.PROGRAM_NAME, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void initializeAllProgram(){
        try{
            this.allPrograms = this.programService.getAllPrograms();
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.PROGRAM_NAME, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.PROGRAM_NAME, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void initializeAllMenuItems(){
        try{
            allMenuItems = navigationService.getAllMenuItems();
            //who knows whether there is empty list or not?
        }
        catch(DBConnectionException dbex){
            FacesMessenger.setFacesMessage(this.PROGRAM_NAME, FacesMessage.SEVERITY_INFO, "Could not connect to database!", "Please contact admin.");
            //FacesMessenger.constructBootstrapMessage(setupNavigationFormName).appendSummary("Could not connect to database! Click here: ")
            //        .appendSummaryLink("test", "/", "");
        }
        catch(Exception ex){
            FacesMessenger.setFacesMessage(this.PROGRAM_NAME, FacesMessage.SEVERITY_ERROR, 
                    ex.getCause().getClass().getSimpleName(), 
                    ex.getCause().getMessage());
        }
    }

    public FormTestNavigation getFormTestNavigation() {
        return formTestNavigation;
    }

    public void setFormTestNavigation(FormTestNavigation formTestNavigation) {
        this.formTestNavigation = formTestNavigation;
    }

    public List<UserType> getAllUserTypes() {
        return allUserTypes;
    }

    public List<Layout> getAllLayouts() {
        return allLayouts;
    }

    public void setAllLayouts(List<Layout> allLayouts) {
        this.allLayouts = allLayouts;
    }

    public List<Program> getAllPrograms() {
        return allPrograms;
    }

    public void setAllPrograms(List<Program> allPrograms) {
        this.allPrograms = allPrograms;
    }

    public List<MenuItem> getAllMenuItems() {
        return allMenuItems;
    }

    public void setAllMenuItems(List<MenuItem> allMenuItems) {
        this.allMenuItems = allMenuItems;
    }
    
    
}
