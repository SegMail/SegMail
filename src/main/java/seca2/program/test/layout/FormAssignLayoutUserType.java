/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.test.layout;

import eds.component.data.DBConnectionException;
import eds.component.layout.LayoutAssignmentException;
import eds.component.layout.LayoutService;
import eds.component.user.UserService;
import eds.entity.layout.Layout;
import eds.entity.user.UserType;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.Form;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
public class FormAssignLayoutUserType extends Form {
    
    @EJB private LayoutService layoutService;
    @EJB private UserService userService;
    
    private List<Layout> allLayouts;
    private List<UserType> allUserTypes;
    
    private long usertypeid;
    private long layoutId;
    
    private final String formName = "assignLayoutToUsertypeForm";
    
    @PostConstruct
    @Override
    protected void init() {
        this.FORM_NAME = "assignLayoutToUsername";
        initializeAllLayout();
        initializeAllUserType();
    }
    
    public void initializeAllLayout(){
        try{
            this.allLayouts = this.layoutService.getAllLayouts();
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.FORM_NAME, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.FORM_NAME, FacesMessage.SEVERITY_ERROR, ex.getLocalizedMessage().getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void initializeAllUserType(){
        try{
            this.allUserTypes = this.userService.getAllUserTypes();
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.FORM_NAME, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.FORM_NAME, FacesMessage.SEVERITY_ERROR, ex.getLocalizedMessage().getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void assignLayoutToUserType(){
        try{
            this.layoutService.assignLayout(layoutId, usertypeid);
            FacesMessenger.setFacesMessage(this.FORM_NAME, FacesMessage.SEVERITY_FATAL, "Layout has been assigned!",null);
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.FORM_NAME, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } catch (LayoutAssignmentException ex) {
            FacesMessenger.setFacesMessage(this.FORM_NAME, FacesMessage.SEVERITY_ERROR, ex.getLocalizedMessage().getClass().getSimpleName(), ex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.FORM_NAME, FacesMessage.SEVERITY_ERROR, ex.getLocalizedMessage().getClass().getSimpleName(), ex.getMessage());
        }
    }

    public LayoutService getLayoutService() {
        return layoutService;
    }

    public void setLayoutService(LayoutService layoutService) {
        this.layoutService = layoutService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public List<Layout> getAllLayouts() {
        return allLayouts;
    }

    public void setAllLayouts(List<Layout> allLayouts) {
        this.allLayouts = allLayouts;
    }

    public long getUsertypeid() {
        return usertypeid;
    }

    public void setUsertypeid(long usertypeid) {
        this.usertypeid = usertypeid;
    }

    public long getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(long layoutId) {
        this.layoutId = layoutId;
    }

    public List<UserType> getAllUserTypes() {
        return allUserTypes;
    }

    public void setAllUserTypes(List<UserType> allUserTypes) {
        this.allUserTypes = allUserTypes;
    }
    
    
    
}
