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
import segmail.entity.subscription.email.Assign_AutoConfirmEmail_List;
import segmail.entity.subscription.email.AutoConfirmEmail;
import segmail.entity.subscription.email.AutoresponderEmail;
import static segmail.entity.subscription.email.AutoEmailTypeFactory.TYPE.CONFIRMATION;
import segmail.entity.subscription.email.Assign_AutoresponderEmail_List;
import segmail.entity.subscription.email.AutoWelcomeEmail;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormListEmail")
@RequestScoped
public class FormListEmail {
    
    @Inject private ProgramList program;
    @Inject private ClientContainer clientContainer;
    
    @EJB private SubscriptionService subscriptionService;
    @EJB private GenericObjectService objectService;
    
    private final String formName = "form_list_email";
    
    @PostConstruct
    public void init(){
        FacesContext fc = FacesContext.getCurrentInstance();
        if(!fc.isPostback()){
            loadAvailableConfirmationEmails();
            loadAvailableWelcomeEmails();
            loadAssignedConfirmEmail();
            loadAssignedWelcomeEmail();
            //Preview confirmation panel or set as null if no template has been assigned yet
            previewConfirmationTemplate();
        }
    }
    
    public void loadAvailableConfirmationEmails(){
        try {
            List<AutoConfirmEmail> confirmEmails = subscriptionService.getAvailableConfirmationEmailForClient(
                    clientContainer.getClient().getOBJECTID());
            program.setConfirmationEmails(confirmEmails);
            
        } catch(EJBException ex) {
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }
    
    public void loadAvailableWelcomeEmails(){
        try {
            List<AutoWelcomeEmail> welcomeEmails = subscriptionService.getAvailableWelcomeEmailForClient(
                    clientContainer.getClient().getOBJECTID());
            program.setWelcomeEmails(welcomeEmails);
            
        } catch(EJBException ex) {
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }
    
    public void loadAssignedConfirmEmail(){
        try {
            if(program.getListEditing() == null) return;
            //If there is already a confirmation email assigned, load it
            List<AutoConfirmEmail> assignedAutoEmails = subscriptionService.getAssignedAutoEmailForList(program.getListEditing().getOBJECTID(),
                    AutoConfirmEmail.class);
            if(assignedAutoEmails != null && !assignedAutoEmails.isEmpty()){
                AutoConfirmEmail selected = assignedAutoEmails.get(0);
                program.setSelectedConfirmationEmailId(selected.getOBJECTID());
                program.setSelectedConfirmationEmail(selected);
            }
        } catch(EJBException ex) {
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }
    
    public void loadAssignedWelcomeEmail(){
        try {
            if(program.getListEditing() == null) return;
            //If there is already a confirmation email assigned, load it
            List<AutoWelcomeEmail> assignedAutoEmails = subscriptionService.getAssignedAutoEmailForList(program.getListEditing().getOBJECTID(),
                    AutoWelcomeEmail.class);
            if(assignedAutoEmails != null && !assignedAutoEmails.isEmpty()){
                AutoWelcomeEmail selected = assignedAutoEmails.get(0);
                program.setSelectedWelcomeEmailId(selected.getOBJECTID());
                program.setSelectedWelcomeEmail(selected);
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
                subscriptionService.removeAllAssignedConfirmationEmailFromList(editingListId);
                FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_WARN, "Confirmation email unassigned. ", "You need to assign a confirmation email to start receiving signups.");
                this.resetConfirmationEmailPanel();
                return;
            }
            
            Assign_AutoConfirmEmail_List newAssign = subscriptionService.assignConfirmationEmailToList(program.getSelectedConfirmationEmailId(), program.getListEditing().getOBJECTID());
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
        
        AutoConfirmEmail selected = objectService.getEnterpriseObjectById(selectedConfirmEmailId, AutoConfirmEmail.class);
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
