/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.test;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import seca2.component.data.DBConnectionException;
import seca2.component.user.UserService;
import seca2.component.user.UserTypeException;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author vincent.a.lee
 */
@RequestScoped
public class FormTestUser implements Serializable {
    
    private String userTypeName;
    private String description;
    
    private final String formName = "createUsertypeForm";
    
    @EJB private UserService userService;
    
    @PostConstruct
    public void init(){
        
    }
    
    public void createUserType(){
        try{
            userService.createUserType(userTypeName, description);
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_FATAL, "Usertype "+userTypeName+" created!", null);
        } 
        catch (UserTypeException utex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, utex.getClass().getSimpleName(), utex.getMessage());
        } 
        catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } 
        catch(Exception ex){
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
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
    
    
}
