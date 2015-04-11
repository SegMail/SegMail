/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap.module.Program;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.enterprise.context.SessionScoped;

/**
 * This class is different from UserContainer.getLastURL(). This stores the 
 * entire program history within the session which can be tracked or traced.
 * 
 * @author LeeKiatHaw
 */
@SessionScoped
public class ProgramContainer implements Serializable{
    
    private Stack<String> programHistory = new Stack<String>();
    
    public void visitNewProgram(String newProgram){
        programHistory.push(newProgram);
    }

    public String getLastProgram(){
        return (programHistory.isEmpty()) ? null : programHistory.peek();
    }
    
}
