/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.BootstrapRequest;
import seca2.bootstrap.module.Program.ProgramModule;
import seca2.entity.program.Program;

/**
 *
 * @author vincent.a.lee
 */
@Named("TopMenu")
public class TopMenu {
    
    @Inject @BootstrapRequest private ProgramModule programModule;
    
    @PostConstruct
    public void init(){
        
    }
    
    public List<Program> getPrograms(){
        return programModule.getPrograms();
    }
    
    public int getCurrentProgramIndex(){
        return programModule.getCurrentProgramIndex();
    }
    
    
}
