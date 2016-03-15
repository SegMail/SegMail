/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import eds.component.GenericObjectService;
import eds.component.data.EntityNotFoundException;
import java.util.List;
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
import segmail.component.subscription.autoresponder.AutoresponderService;
import segmail.entity.subscription.autoresponder.AUTO_EMAIL_TYPE;
import segmail.entity.subscription.autoresponder.Assign_AutoresponderEmail_List;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormListEmail")
@RequestScoped
public class FormListEmail {

    @Inject private ProgramList program;
    @Inject private ClientContainer clientContainer;

    @EJB private AutoresponderService autoresponderService;
    @EJB private GenericObjectService objectService;

    private final String formName = "form_list_email";

    /**
     * We can control the number of DB hits for each form in this method. Each
     * load[something] method will contain optimally 1 DB hit and we can monitor
     * and control the number of times by controlling the load calls here.
     */
    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (!fc.isPostback()) {
            //loadAvailableConfirmationEmails();
            //loadAvailableWelcomeEmails();
            //loadAssignedConfirmEmail();
            //loadAllAssignedEmails(); //doesn't work because of missing OBJECT_TYPE column in complex join statement
            //loadAssignedWelcomeEmail();
            //Preview confirmation panel or set as null if no template has been assigned yet
            //previewConfirmationTemplate();
            reset();
        }
    }

    public void loadAvailableConfirmationEmails() {
        try {
            List<AutoresponderEmail> confirmEmails = 
                    autoresponderService.getAvailableAutoEmailsForClient(
                            clientContainer.getClient().getOBJECTID(), 
                            AUTO_EMAIL_TYPE.CONFIRMATION);
            program.setConfirmationEmails(confirmEmails);

        } catch (EJBException ex) {
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }

    public void loadAvailableWelcomeEmails() {
        try {
            List<AutoresponderEmail> welcomeEmails = 
                    autoresponderService.getAvailableAutoEmailsForClient(
                        clientContainer.getClient().getOBJECTID(),
                        AUTO_EMAIL_TYPE.WELCOME);
            program.setWelcomeEmails(welcomeEmails);

        } catch (EJBException ex) {
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }

    public void loadAssignedConfirmEmail() {
        try {
            if (program.getListEditing() == null) {
                return;
            }
            //If there is already a confirmation email assigned, load it
            List<AutoresponderEmail> assignedConfirmEmails = autoresponderService.getAssignedAutoEmailsForList(
                    program.getListEditing().getOBJECTID(), AUTO_EMAIL_TYPE.CONFIRMATION);
            
            program.setSelectedConfirmationEmail(
                    (assignedConfirmEmails == null || assignedConfirmEmails.isEmpty())?
                            null : assignedConfirmEmails.get(0));
        } catch (EJBException ex) {
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }

    public void loadAssignedWelcomeEmail() {
        try {
            if (program.getListEditing() == null) {
                return;
            }
            //If there is already a confirmation email assigned, load it
            //List<AutoWelcomeEmail> assignedAutoEmails = objectService.getAllSourceObjectsFromTarget(program.getListEditing().getOBJECTID(),
            //        Assign_AutoWelcomeEmail_List.class, AutoWelcomeEmail.class);
            List<AutoresponderEmail> assignedWelcomeEmails = autoresponderService.getAssignedAutoEmailsForList(
                    program.getListEditing().getOBJECTID(), AUTO_EMAIL_TYPE.WELCOME);
            
            program.setSelectedWelcomeEmail(
                    (assignedWelcomeEmails == null || assignedWelcomeEmails.isEmpty()) ?
                            null : assignedWelcomeEmails.get(0)
            );
        } catch (EJBException ex) {
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }

    public void assignConfirmationEmail() {
        try {
            long selectedConfirmEmailId = program.getSelectedConfirmationEmailId();
            long editingListId = program.getListEditing().getOBJECTID();
            
            //Remove all assigned Confirmation emails first
            autoresponderService.removeAllAssignedConfirmationEmailFromList(editingListId);
            
            //Nothing is selected
            if (selectedConfirmEmailId <= 0) {
                //autoresponderService.removeAllAssignedConfirmationEmailFromList(editingListId);
                FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_WARN, "Confirmation email is not assigned. ", "You need to assign a confirmation email to start receiving signups.");
                this.resetConfirmationEmailPanel();
                return;
            }
            
            Assign_AutoresponderEmail_List newAssign = autoresponderService.assignAutoEmailToList(program.getSelectedConfirmationEmailId(), program.getListEditing().getOBJECTID());
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_FATAL, "Confirmation email assigned", "");
            program.setSelectedConfirmationEmail(newAssign.getSOURCE());

        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName()+": "+ex.getMessage(), "Please contact your system administrator.");
        }
    }

    public void assignWelcomeEmail() {
        try {
            long selectedWelcomeEmailId = program.getSelectedWelcomeEmailId();
            long editingListId = program.getListEditing().getOBJECTID();

            //Remove all assigned Welcome emails first
            autoresponderService.removeAllAssignedWelcomeEmailFromList(editingListId);
            
            //Nothing is selected
            if (selectedWelcomeEmailId <= 0) {
                //autoresponderService.removeAllAssignedWelcomeEmailFromList(editingListId);
                FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_WARN, "Welcome email is not assigned. ", "You need to assign a confirmation email to start receiving signups.");
                this.resetConfirmationEmailPanel();
                return;
            }

            Assign_AutoresponderEmail_List newAssign = autoresponderService.assignAutoEmailToList(program.getSelectedWelcomeEmailId(), program.getListEditing().getOBJECTID());
            program.setSelectedWelcomeEmail(newAssign.getSOURCE());
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_FATAL, "Welcome email assigned", "");

        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName()+": "+ex.getMessage(), "Please contact your system administrator.");
        }
    }


    public void saveEmailSettings() {
        //try {
            this.assignConfirmationEmail();
            this.assignWelcomeEmail();
            
            reset();

        /*} catch (IOException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }*/
    }

    public ProgramList getProgram() {
        return program;
    }

    public void setProgram(ProgramList program) {
        this.program = program;
    }

    public String getPreview() {
        return (program.getSelectedConfirmationEmail() == null)
                ? null : program.getSelectedConfirmationEmail().getBODY();
    }

    public void resetConfirmationEmailPanel() {
        program.setSelectedConfirmationEmail(null);
    }

    public void reset() {
        loadAvailableConfirmationEmails();
        loadAvailableWelcomeEmails();
        loadAssignedConfirmEmail();
        //loadAllAssignedEmails(); //doesn't work because of missing OBJECT_TYPE column in complex join statement
        loadAssignedWelcomeEmail();
            //Preview confirmation panel or set as null if no template has been assigned yet
        //previewConfirmationTemplate();
    }
}
