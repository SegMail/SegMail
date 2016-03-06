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
import eds.entity.data.EnterpriseObject;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.email.AutoresponderEmail;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.UserSessionContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.autoresponder.AutoresponderService;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormEditExistingTemplate")
@RequestScoped
public class FormEditExistingTemplate {
    
    @EJB private AutoresponderService autoresponderService;
    @EJB private GenericObjectService objectService;
    //@EJB private UserService userService;
    
    @Inject private ProgramWelcomeEmail program;
    
    @Inject private UserSessionContainer userContainer;
    @Inject private UserRequestContainer requestContainer;
    
    private final String formName = "edit_template_form";
    
    @PostConstruct
    public void init(){
        
    }
    
    public void loadTemplate(long templateId){
        try {
            
            // Retrieve the template based on the Id
            // Using cast because when retrieving with AutoresponderEmail.class, issue https://github.com/SegMail/SegMail/issues/35 occurs
            AutoresponderEmail editing = (AutoresponderEmail) objectService.getEnterpriseObjectById(templateId, EnterpriseObject.class); 
            
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
            AutoresponderEmail newTemplate = autoresponderService.saveAutoEmail(program.getEditingTemplate());
            
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
            autoresponderService.saveAutoEmail(program.getEditingTemplate());
            
            refresh();
            
            //Set success message
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_FATAL, "Template updated.", null);
            
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (EntityExistsException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (EJBException ex) { //Transaction did not go through
            //Throwable cause = ex.getCause();
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void closeWithoutSaving(){
        refresh();
    }
    
    public void deleteTemplate(){
        try {
            autoresponderService.deleteAutoEmail(program.getEditingTemplate().getOBJECTID());
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_FATAL, "Template deleted.",null);
            refresh();
            
            
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR,  ex.getMessage(), null);
        } catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR,  ex.getMessage(), null);
        }
        
    }
    
    public void refresh(){
        try {
            //redirect to itself after setting list editing
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            //ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI()); can't do this else it will show .xhtml
            //ec.redirect(programContainer.getCurrentURL());
            ec.redirect(ec.getRequestContextPath()+"/".concat(requestContainer.getProgramName()));
        } catch (Exception ex){
            FacesMessenger.setFacesMessage(this.program.getFormName(), FacesMessage.SEVERITY_ERROR,  ex.getMessage(), null);
        }
    }

    public ProgramWelcomeEmail getProgram() {
        return program;
    }

    public void setProgram(ProgramWelcomeEmail program) {
        this.program = program;
    }
    
    
}
