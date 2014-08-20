/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.test;

import seca2.program.file.*;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.entity.file.FileEntity;
import seca2.entity.program.Program;

/**
 *
 * @author vincent.a.lee
 */
@Named("ProgramTest")
@SessionScoped
public class ProgramTest extends Program {
    
    @Inject private FormTestDB formTestDB;
    
    @PostConstruct
    public void init(){
        
    }

    public FormTestDB getFormTestDB() {
        return formTestDB;
    }

    public void setFormTestDB(FormTestDB formTestDB) {
        this.formTestDB = formTestDB;
    }
    
    
}
