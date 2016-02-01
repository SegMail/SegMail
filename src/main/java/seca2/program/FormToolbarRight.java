/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 * Ideally, one should be able to just change the methods in this class to change
 * the behaviour in the frontend form without having to change the xhtml files.
 * But we're not there yet.
 * 
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormToolbarRight")
public class FormToolbarRight  {
    
    @Inject private UserRequestContainer reqContainer;
    
    private String formName = "FormToolbarRight";
    
    public void refresh(){
        try {
            //redirect to itself after setting list editing
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            //Keep all messages posted in this request
            ec.getFlash().setKeepMessages(true);
            ec.redirect(ec.getRequestContextPath()+"/".concat(reqContainer.getProgramName()));
        } catch (Exception ex){
            FacesMessenger.setFacesMessage(this.getFormName(), FacesMessage.SEVERITY_ERROR,  ex.getMessage(), null);
        }
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }
    
    
}
