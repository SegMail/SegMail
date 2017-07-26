/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.reset;

import seca2.program.Program;

/**
 *
 * @author LeeKiatHaw
 */
public class ProgramReset extends Program {
    
    private String form;
    
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    @Override
    public void clearVariables() {
        this.setToken("");
    }

    @Override
    public void initRequestParams() {
        
    }

    @Override
    public void initProgram() {
        
    }
    
}
