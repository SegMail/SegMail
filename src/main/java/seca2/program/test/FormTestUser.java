/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import eds.component.data.EntityExistsException;
import eds.component.data.IncompleteDataException;
import eds.component.user.UserAccountLockedException;
import seca2.bootstrap.UserSessionContainer;
import eds.component.user.UserLoginException;
import eds.component.user.UserService;
import eds.entity.user.UserType;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author vincent.a.lee
 */
@Named("FormTestUser")
@RequestScoped
public class FormTestUser implements Serializable {
    
    @Inject private ProgramTest programTest;
    
    //Create UserType
    private String userTypeName;
    private String description;
    private boolean portalAccess;
    private boolean wsAccess;
    
    private final String createUsertypeFormName = "createUsertypeForm";
    
    //Create User
    private List<UserType> allUserTypes = new ArrayList<UserType>();
    private long chosenUserType;
    private String username;
    private String password;
    private String contact;
    
    private final String createUserFormName = "createUserForm";
    
    //Login User
    private String loginUsername;
    private String loginPassword;
    @Inject
    private UserSessionContainer userContainer; //this is not resolved precisely [20150131]
    
    //Profile pic
    private String usernameProfilePic;
    private String profilePicLocation;
    private final String setProfilePicFormName = "setProfilePicForm";
    
    private final String loginUserFormName = "loginUserForm";
    
    @EJB private UserService userService;
    
    @PostConstruct
    public void init(){
        //this.initializeAllUserTypes();
    }
    
    public void createUserType(){
        try{
            userService.createUserType(userTypeName,description,portalAccess,wsAccess);
            FacesMessenger.setFacesMessage(createUsertypeFormName, FacesMessage.SEVERITY_FATAL, "Usertype "+userTypeName+" created!", null);
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(createUsertypeFormName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (EntityExistsException ex) {
            FacesMessenger.setFacesMessage(createUsertypeFormName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }
    
    public void createUser(){
        try{
            userService.registerUserByUserTypeId(chosenUserType, username, password, contact);
            FacesMessenger.setFacesMessage(createUserFormName, FacesMessage.SEVERITY_FATAL, "User "+username+" has been created!", null);
            
        } catch(Exception ex){
            FacesMessenger.setFacesMessage(createUserFormName, FacesMessage.SEVERITY_ERROR, 
                    ex.getClass().getSimpleName(), //why? i forgot why...
                    ex.getMessage());
        }
    }
    
    public void loginUser(){
        try{
            Map<String,Object> userValues = new HashMap<String,Object>();
            userService.login(this.loginUsername, this.loginPassword, userValues);
            FacesMessenger.setFacesMessage(loginUserFormName, FacesMessage.SEVERITY_FATAL, "Login successful!", null);
        } catch(UserLoginException esliex){
            FacesMessenger.setFacesMessage(loginUserFormName, FacesMessage.SEVERITY_ERROR, esliex.getLocalizedMessage(), null);
        } catch(UserAccountLockedException ualex){
            FacesMessenger.setFacesMessage(loginUserFormName, FacesMessage.SEVERITY_ERROR, ualex.getLocalizedMessage(), "Please contact admin.");
        } catch(Exception ex){
            FacesMessenger.setFacesMessage(loginUserFormName, FacesMessage.SEVERITY_ERROR, ex.getLocalizedMessage(), null);
        }
    }
    
    public void setProfilePicLocation(){
        try{
            userService.setProfilePicLocationForUsername(this.usernameProfilePic, this.profilePicLocation);
        } catch(Exception ex){
            FacesMessenger.setFacesMessage(this.setProfilePicFormName, FacesMessage.SEVERITY_ERROR, 
                    ex.getCause().getClass().getSimpleName(), 
                    ex.getCause().getMessage());
        }
    }

    public String getUserTypeName() {
        return userTypeName;
    }

    public void setUserTypeName(String userTypeName) {
        this.userTypeName = userTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getChosenUserType() {
        return chosenUserType;
    }

    public void setChosenUserType(long chosenUserType) {
        this.chosenUserType = chosenUserType;
    }

    public String getLoginUsername() {
        return loginUsername;
    }

    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getUsernameProfilePic() {
        return usernameProfilePic;
    }

    public void setUsernameProfilePic(String usernameProfilePic) {
        this.usernameProfilePic = usernameProfilePic;
    }

    public String getProfilePicLocation() {
        return profilePicLocation;
    }

    public void setProfilePicLocation(String profilePicLocation) {
        this.profilePicLocation = profilePicLocation;
    }

    public ProgramTest getProgramTest() {
        return programTest;
    }

    public void setProgramTest(ProgramTest programTest) {
        this.programTest = programTest;
    }

    public List<UserType> getAllUserTypes() {
        return this.programTest.getAllUserTypes();
    }

    public boolean isPortalAccess() {
        return portalAccess;
    }

    public void setPortalAccess(boolean portalAccess) {
        this.portalAccess = portalAccess;
    }

    public boolean isWsAccess() {
        return wsAccess;
    }

    public void setWsAccess(boolean wsAccess) {
        this.wsAccess = wsAccess;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
    
    public void createUserWithType(String usertypename, String username, String password, String contact){
        
        List<UserType> usertypeList = this.userService.getUserTypeByName(usertypename);
        if(usertypeList == null || usertypeList.isEmpty())
            throw new RuntimeException("No UserTypes created yet.");
        
        UserType type = usertypeList.get(0);
        this.setChosenUserType(type.getOBJECTID());
        this.setUsername(username);
        this.setPassword(password);
        this.setContact(contact);
        this.createUser();
        
    }
}
