/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import eds.component.GenericObjectService;
import eds.component.data.EntityNotFoundException;
import segmail.component.subscription.SubscriptionService;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.entity.subscription.email.ConfirmEmailListAssignment;
import segmail.entity.subscription.email.ConfirmationEmailTemplate;
import segmail.entity.subscription.email.EmailTemplate;
import static segmail.entity.subscription.email.EmailTemplateFactory.TYPE.CONFIRMATION;
import segmail.entity.subscription.email.TemplateListAssignment;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormConfirmationEmail")
@RequestScoped
public class FormConfirmationEmail {
    
    @Inject private ProgramList program;
    @Inject private ClientContainer clientContainer;
    
    @EJB private SubscriptionService subscriptionService;
    @EJB private GenericObjectService objectService;
    
    private final String formName = "form_confirm_email";
    
    @PostConstruct
    public void init(){
        FacesContext fc = FacesContext.getCurrentInstance();
        if(!fc.isPostback()){
            loadAvailableConfirmationEmails();
            loadAssignedEmailTemplates();
            //Preview confirmation panel or set as null if no template has been assigned yet
            previewConfirmationTemplate();
        }
    }
    
    public void loadAvailableConfirmationEmails(){
        try {
            List<ConfirmationEmailTemplate> confirmEmails = subscriptionService.getAvailableConfirmationTemplateForClient(
                    clientContainer.getClient().getOBJECTID());
            program.setConfirmationEmails(confirmEmails);
            
        } catch(EJBException ex) {
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }
    
    public void loadAssignedEmailTemplates(){
        try {
            if(program.getListEditing() == null) return;
            //If there is already a confirmation email assigned, load it
            List<ConfirmationEmailTemplate> assignedConfirmEmails = subscriptionService.getAssignedEmailTemplatesForList(program.getListEditing().getOBJECTID(),
                    ConfirmationEmailTemplate.class);
            if(assignedConfirmEmails != null && !assignedConfirmEmails.isEmpty()){
                ConfirmationEmailTemplate selected = assignedConfirmEmails.get(0);
                program.setSelectedConfirmationEmailId(selected.getOBJECTID());
                program.setSelectedConfirmationEmail(selected);
            }
        } catch(EJBException ex) {
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }
    
    public void assignConfirmationEmail(){
        try {
            long selectedConfirmEmailId = program.getSelectedConfirmationEmailId();
            long editingListId = program.getListEditing().getOBJECTID();
            
            //Nothing is selected
            if(selectedConfirmEmailId <= 0) {
                subscriptionService.removeAllAssignedConfirmationTemplateFromList(editingListId);
                FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_WARN, "Confirmation email unassigned. ", "You need to assign a confirmation email to start receiving signups.");
                this.resetConfirmationEmailPanel();
                return;
            }
            
            ConfirmEmailListAssignment newAssign = subscriptionService.assignConfirmationEmailToList(program.getSelectedConfirmationEmailId(), program.getListEditing().getOBJECTID());
            program.setSelectedConfirmationEmail(newAssign.getSOURCE());
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_FATAL, "Confirmation email assigned", null);
            
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch(EJBException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } 
    }
    
    public void previewConfirmationTemplate(){
        long selectedConfirmEmailId = program.getSelectedConfirmationEmailId();
        
        //Nothing is selected
        if(selectedConfirmEmailId <= 0){
            resetConfirmationEmailPanel();
            return;
        }
        
        ConfirmationEmailTemplate selected = objectService.getEnterpriseObjectById(selectedConfirmEmailId, ConfirmationEmailTemplate.class);
        program.setSelectedConfirmationEmail(selected);
        
    }

    public ProgramList getProgram() {
        return program;
    }

    public void setProgram(ProgramList program) {
        this.program = program;
    }
    
    public String getPreview(){
        return (program.getSelectedConfirmationEmail() == null) ? 
                null : program.getSelectedConfirmationEmail().getBODY();
    }

    public void resetConfirmationEmailPanel(){
        program.setSelectedConfirmationEmail(null);
    }
    
    
}
