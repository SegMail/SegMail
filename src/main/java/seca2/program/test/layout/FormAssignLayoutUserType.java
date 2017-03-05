/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.test.layout;

import eds.component.layout.LayoutService;
import eds.component.user.UserService;
import eds.entity.layout.Layout;
import eds.entity.user.UserType;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.Form;
import seca2.program.test.ProgramTest;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormAssignLayoutUserType")
public class FormAssignLayoutUserType extends Form {

    @Inject
    private ProgramTest programTest;

    @EJB
    private LayoutService layoutService;
    @EJB
    private UserService userService;

    private long usertypeid;
    private long layoutId;

    private final String formName = "assignLayoutToUsertypeForm";

    @PostConstruct
    @Override
    protected void init() {
        this.FORM_NAME = "assignLayoutToUsername";
    }

    public void assignLayoutToUserType() {
        try {
            this.layoutService.assignLayout(usertypeid, layoutId);
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_FATAL, "Layout has been assigned!", null);
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void assignLayoutToUserType(String layoutName, String usertypeName){
        Layout layout = this.layoutService.getLayoutByName(layoutName);
        UserType usertype = this.userService.getSingleUserTypeByName(usertypeName);
        
        this.setLayoutId(layout.getOBJECTID());
        this.setUsertypeid(usertype.getOBJECTID());
        this.assignLayoutToUserType();
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public List<Layout> getAllLayouts() {
        return this.programTest.getAllLayouts();
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
        return this.programTest.getAllUserTypes();
    }


}
