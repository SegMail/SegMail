/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.test;

import eds.component.GenericObjectService;
import eds.component.data.DBConnectionException;
import eds.entity.client.ClientType;
import eds.entity.layout.Layout;
import eds.entity.navigation.MenuItem;
import eds.entity.program.Program;
import eds.entity.user.UserType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.FormGroup;

/**
 *
 * @author vincent.a.lee
 */
@Named("ProgramTest")
@RequestScoped
public class ProgramTest extends FormGroup implements Serializable {
    
    private final String PROGRAM_NAME = "ProgramTest";
    
    //@Inject private FormTestDB formTestDB;
    //@Inject private FormTestNavigation formTestNavigation;
    //@Inject private FormTestUser formTestUser;
    //@Inject private FormTestProgram formTestProgram;
    //@Inject private FormCreateLayout formCreateLayout;
    //@Inject private FormAssignLayoutUsername formAssignLayoutUsername;
    //@Inject private FormAssignLayoutUserType formAssignLayoutUserType;
    //@Inject private FormAssignLayoutProgram formAssignLayoutProgram;
    
    @EJB private GenericObjectService genericDBService;
    //@EJB private UserService userService;
    //@EJB private LayoutService layoutService;
    //@EJB private ProgramService programService;
    //@EJB private NavigationService navigationService;
    //@EJB private ClientService clientService;
    
    
    private List<UserType> allUserTypes = new ArrayList<UserType>();
    private List<Layout> allLayouts;
    private List<Program> allPrograms;
    private List<MenuItem> allMenuItems;
    private List<ClientType> allClientTypes;
    
    @PostConstruct
    public void init(){
        initializeAllUserTypes();
        initializeAllLayout();
        initializeAllProgram();
        initializeAllMenuItems();
        initializeAllClientTypes();
    }
    
    public void initializeAllUserTypes(){
         try{
            allUserTypes = genericDBService.getAllEnterpriseObjects(UserType.class);
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
            this.allLayouts = this.genericDBService.getAllEnterpriseObjects(Layout.class);
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.PROGRAM_NAME, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.PROGRAM_NAME, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void initializeAllProgram(){
        try{
            this.allPrograms = this.genericDBService.getAllEnterpriseObjects(Program.class);
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.PROGRAM_NAME, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.PROGRAM_NAME, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void initializeAllMenuItems(){
        try{
            allMenuItems = genericDBService.getAllEnterpriseObjects(MenuItem.class);
        }
        catch(DBConnectionException dbex){
            FacesMessenger.setFacesMessage(this.PROGRAM_NAME, FacesMessage.SEVERITY_INFO, "Could not connect to database!", "Please contact admin.");
            
        }
        catch(Exception ex){
            FacesMessenger.setFacesMessage(this.PROGRAM_NAME, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void initializeAllClientTypes(){
        try{
            allClientTypes = genericDBService.getAllEnterpriseObjects(ClientType.class);
        }
        catch(DBConnectionException dbex){
            FacesMessenger.setFacesMessage(this.PROGRAM_NAME, FacesMessage.SEVERITY_INFO, "Could not connect to database!", "Please contact admin.");
            
        }
        catch(Exception ex){
            FacesMessenger.setFacesMessage(this.PROGRAM_NAME, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
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

    public List<ClientType> getAllClientTypes() {
        return allClientTypes;
    }

    public void setAllClientTypes(List<ClientType> allClientTypes) {
        this.allClientTypes = allClientTypes;
    }
    
    
}
