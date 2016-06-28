/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.batch;

import java.util.Date;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormPeriodFilter")
public class FormPeriodFilter {
    
    @Inject ProgramBatch program;
    
    @PostConstruct
    public void init() {
        
    }
    
    public String getStartString() {
        return program.getStartString();
    }

    public void setStartString(String range) {
        program.setStartString(range);
    }
    
    public String getEndString() {
        return program.getEndString();
    }

    public void setEndString(String endString) {
        program.setEndString(endString);
    }
    
}
