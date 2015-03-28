/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program;

/**
 * A template for the backing beans for JSF/HTML forms
 * 
 * @author LeeKiatHaw
 */
public abstract class Form {
    
    protected String FORM_NAME;

    public String getFORM_NAME() {
        return FORM_NAME;
    }

    public void setFORM_NAME(String FORM_NAME) {
        this.FORM_NAME = FORM_NAME;
    }
    
    
}
