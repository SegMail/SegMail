/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.test;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author vincent.a.lee
 */
@Named("ProgramTest")
@SessionScoped
public class ProgramTest implements Serializable {
    
    @Inject private FormTestDB formTestDB;
    @Inject private FormTestNavigation formTestNavigation;
    @Inject private FormTestUser formTestUser;
    @Inject private FormTestProgram formTestProgram;
    
    @PostConstruct
    public void init(){
        
    }

    public FormTestDB getFormTestDB() {
        return formTestDB;
    }

    public void setFormTestDB(FormTestDB formTestDB) {
        this.formTestDB = formTestDB;
    }

    public FormTestNavigation getFormTestNavigation() {
        return formTestNavigation;
    }

    public void setFormTestNavigation(FormTestNavigation formTestNavigation) {
        this.formTestNavigation = formTestNavigation;
    }

    public FormTestUser getFormTestUser() {
        return formTestUser;
    }

    public void setFormTestUser(FormTestUser formTestUser) {
        this.formTestUser = formTestUser;
    }

    public FormTestProgram getFormTestProgram() {
        return formTestProgram;
    }

    public void setFormTestProgram(FormTestProgram formTestProgram) {
        this.formTestProgram = formTestProgram;
    }
    
    
}
