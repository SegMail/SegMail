/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.template;

import eds.component.GenericObjectService;
import eds.component.data.DBConnectionException;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.email.EmailTemplate;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Program.ProgramContainer;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormEditExistingTemplate")
@RequestScoped
public class FormEditExistingTemplate {
    
    @EJB private SubscriptionService subscriptionService;
    @EJB private GenericObjectService objectService;
    //@EJB private UserService userService;
    
    @Inject private ProgramTemplate program;
    //@Inject private UserContainer userContainer;
    @Inject private ProgramContainer programContainer;
    
    private final String formName = "edit_template_form";
    
    @PostConstruct
    public void init(){
        
    }
    
    public void loadTemplate(long templateId){
        try {
            // Retrieve the template based on the Id
            EmailTemplate editing = objectService.getEnterpriseObjectById(templateId, EmailTemplate.class);
            program.setEditingTemplate(editing);
            
        } catch (EJBException ex) { //Transaction did not go through
            //Throwable cause = ex.getCause();
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void saveTemplateAndContinue(){
        try {
            EmailTemplate newTemplate = subscriptionService.saveTemplate(program.getEditingTemplate());
            
            //redirect to itself after setting list editing
            //ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            //ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI()); can't do this else it will show .xhtml
            //ec.redirect(programContainer.getCurrentURL());
            
            //Do not redirect, reload instead
            program.setEditingTemplate(newTemplate);
            
            //Set success message
            FacesMessenger.setFacesMessage(this.formName, FacesMessage.SEVERITY_FATAL, "Template updated.", null);
            
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (EntityExistsException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (EJBException ex) { //Transaction did not go through
            //Throwable cause = ex.getCause();
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void saveTemplateAndClose(){
        try {
            subscriptionService.saveTemplate(program.getEditingTemplate());
            
            //redirect to itself after setting list editing
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            //ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI()); can't do this else it will show .xhtml
            ec.redirect(programContainer.getCurrentURL());
            
            //Set success message
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_FATAL, "Template updated.", null);
            
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (EntityExistsException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (IOException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (EJBException ex) { //Transaction did not go through
            //Throwable cause = ex.getCause();
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void closeWithoutSaving(){
        try {
            //redirect to itself after setting list editing
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            //ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI()); can't do this else it will show .xhtml
            ec.redirect(programContainer.getCurrentURL());
        } catch (IOException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void deleteTemplate(){
        try {
            subscriptionService.deleteTemplate(program.getEditingTemplate().getOBJECTID());
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_FATAL, "Template deleted.",null);
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR,  ex.getMessage(), null);
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR,  ex.getMessage(), null);
        }
        
    }

    public ProgramTemplate getProgram() {
        return program;
    }

    public void setProgram(ProgramTemplate program) {
        this.program = program;
    }
    
    
}
