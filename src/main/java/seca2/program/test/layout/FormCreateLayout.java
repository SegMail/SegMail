/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.test.layout;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import eds.component.data.DBConnectionException;
import eds.component.data.EntityExistsException;
import eds.component.data.IncompleteDataException;
import eds.component.layout.LayoutService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.Form;
import seca2.program.test.ProgramTest;

/**
 *
 * @author vincent.a.lee
 */
@RequestScoped
public class FormCreateLayout extends Form implements Serializable {
    
    private final String formName = "createLayoutForm";
    
    private String layoutName;
    private String viewRoot;
    
    @EJB private LayoutService layoutService;
    
    @Inject private ProgramTest programTest;
    
    @PostConstruct
    @Override
    public void init(){
        
    }
    
    public void registerLayout(){
        try{
            this.layoutService.registerLayout(layoutName, viewRoot);
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_FATAL, "Layout successfully registered.",null);
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } catch (EntityExistsException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } 
    }

    public String getLayoutName() {
        return layoutName;
    }

    public void setLayoutName(String layoutName) {
        this.layoutName = layoutName;
    }

    public String getViewRoot() {
        return viewRoot;
    }

    public void setViewRoot(String viewRoot) {
        this.viewRoot = viewRoot;
    }
    
    
}
