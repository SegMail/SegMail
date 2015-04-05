/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.test.layout;

import eds.component.data.DBConnectionException;
import eds.component.layout.LayoutAssignmentException;
import eds.component.layout.LayoutService;
import eds.component.program.ProgramService;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.Form;
import seca2.program.test.ProgramTest;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormAssignLayoutProgram")
@RequestScoped
public class FormAssignLayoutProgram extends Form {
    
    @EJB private LayoutService layoutService;
    @EJB private ProgramService programService;
    
    @Inject private ProgramTest programTest;
    
    private long programId;
    private long layoutId;
    
    private final String formName = "assignLayoutToProgramForm";
    
    @PostConstruct
    @Override
    protected void init() {
        this.FORM_NAME = "assignLayoutToProgramForm";
    }
    
    public void assignLayoutToProgram(){
        try{
            this.layoutService.assignLayout(this.programId, this.layoutId);
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_FATAL, "Layout has been assigned!",null);
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } catch (LayoutAssignmentException ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public long getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(long layoutId) {
        this.layoutId = layoutId;
    }

    public long getProgramId() {
        return programId;
    }

    public void setProgramId(long programId) {
        this.programId = programId;
    }

    public ProgramTest getProgramTest() {
        return programTest;
    }

    public void setProgramTest(ProgramTest programTest) {
        this.programTest = programTest;
    }

    
}
