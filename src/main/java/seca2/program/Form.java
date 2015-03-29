/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program;

import java.io.Serializable;
import javax.annotation.PostConstruct;

/**
 * A template for the backing beans for JSF/HTML forms
 * 
 * @author LeeKiatHaw
 */
public abstract class Form implements Serializable {
    
    protected String FORM_NAME;
    
    @PostConstruct
    protected abstract void init();

    public String getFORM_NAME() {
        return FORM_NAME;
    }

    public void setFORM_NAME(String FORM_NAME) {
        this.FORM_NAME = FORM_NAME;
    }
    
    
}
