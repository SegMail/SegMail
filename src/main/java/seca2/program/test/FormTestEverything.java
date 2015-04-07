/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.test;

import eds.component.user.UserService;
import eds.entity.user.UserType;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.program.test.layout.FormAssignLayoutProgram;
import seca2.program.test.layout.FormAssignLayoutUserType;
import seca2.program.test.layout.FormAssignLayoutUsername;
import seca2.program.test.layout.FormCreateLayout;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormTestEverything")
@RequestScoped
public class FormTestEverything {
    
    @Inject private ProgramTest programTest;
    @Inject private FormTestDB formTestDB;
    @Inject private FormTestNavigation formTestNavigation;
    @Inject private FormTestUser formTestUser;
    @Inject private FormTestProgram formTestProgram;
    @Inject private FormCreateLayout formCreateLayout;
    @Inject private FormAssignLayoutUsername formAssignLayoutUsername;
    @Inject private FormAssignLayoutUserType formAssignLayoutUserType;
    @Inject private FormAssignLayoutProgram formAssignLayoutProgram;
    
    
    public void init(){
        
    }
    
    public void testEverything(){
        //Setup DB
        formTestDB.generateDB();
        
        //Create usertype
        this.formTestUser.setUserTypeName("Administrator");
        this.formTestUser.createUserType();
        
        programTest.init();
        UserType administrator = this.programTest.getAllUserTypes().get(0);
        
        //Create user
        this.formTestUser.setChosenUserType(administrator.getOBJECTID());
        this.formTestUser.setUsername("admin");
        this.formTestUser.setPassword("admin");
        this.formTestUser.createUser();
        
        //Create testing page
        this.formTestProgram.setProgramName("test");
        this.formTestProgram.setProgramViewRoot("/programs/test/layout.xhtml");
        this.formTestProgram.setDisplayName("Testing page");
        this.formTestProgram.setDisplayDesc("This is for you administrator to set up the DB.");
        this.formTestProgram.createProgram();
        
        this.formTestNavigation.setMenuItemName("Testing page");
        this.formTestNavigation.setMenuItemURL("/program/test/");
        this.formTestNavigation.setPrependHTMLTags("<i class=\"fa fa-dashboard\"></i>");
        this.formTestNavigation.createNewMenuItem();
        
        //Creat chartjs page
        this.formTestProgram.setProgramName("chartjs");
        this.formTestProgram.setProgramViewRoot("/programs/chartjs/layout.xhtml");
        this.formTestProgram.setDisplayName("ChartJS");
        this.formTestProgram.setDisplayDesc("This is a showcase of the ChartJS tool and using JS to call a Java web service.");
        this.formTestProgram.createProgram();
        
        this.formTestNavigation.setMenuItemName("ChartJS");
        this.formTestNavigation.setMenuItemURL("/program/chartjs/");
        this.formTestNavigation.setPrependHTMLTags("<i class=\"fa fa-signal\"></i>");
        this.formTestNavigation.createNewMenuItem();
        
        //Create list page
        this.formTestProgram.setProgramName("list");
        this.formTestProgram.setProgramViewRoot("/programs/list/layout.xhtml");
        this.formTestProgram.setDisplayName("Manage Lists");
        this.formTestProgram.setDisplayDesc("Manage your mailing lists here.");
        this.formTestProgram.createProgram();
        
        this.formTestNavigation.setMenuItemName("Manage Lists");
        this.formTestNavigation.setMenuItemURL("/program/list/");
        this.formTestNavigation.setPrependHTMLTags("<i class=\"fa fa-list\"></i>");
        this.formTestNavigation.createNewMenuItem();
        
        //Create manage signup page
        this.formTestProgram.setProgramName("signup");
        this.formTestProgram.setProgramViewRoot("/programs/signup/layout.xhtml");
        this.formTestProgram.setDisplayName("Signup Forms");
        this.formTestProgram.setDisplayDesc("Manage your signup forms for your lists.");
        this.formTestProgram.createProgram();
        
        this.formTestNavigation.setMenuItemName("Manage Signup Forms");
        this.formTestNavigation.setMenuItemURL("/program/signup/");
        this.formTestNavigation.setPrependHTMLTags("<i class=\"fa fa-code\"></i>");
        this.formTestNavigation.createNewMenuItem();
        
        //Create layouts
        this.formCreateLayout.setLayoutName("Beprobootstrap");
        this.formCreateLayout.setViewRoot("/templates/beprobootstrap/template-layout.xhtml");
        this.formCreateLayout.registerLayout();
        
        this.formCreateLayout.setLayoutName("Flat-ui");
        this.formCreateLayout.setViewRoot("/templates/mytemplate/template-layout.xhtml");
        this.formCreateLayout.registerLayout();
        
        programTest.init();
        
        
        
        
    }
    
}
