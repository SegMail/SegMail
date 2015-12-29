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
import segmail.entity.subscription.email.Assign_AutoWelcomeEmail_List;
import segmail.entity.subscription.email.AutoConfirmEmail;
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
            List<AutoConfirmEmail> confirmEmails = subscriptionService.getAvailableConfirmationEmailForClient(
                    clientContainer.getClient().getOBJECTID());
            program.setConfirmationEmails(confirmEmails);

        } catch (EJBException ex) {
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }

    public void loadAvailableWelcomeEmails() {
        try {
            List<AutoWelcomeEmail> welcomeEmails = subscriptionService.getAvailableWelcomeEmailForClient(
                    clientContainer.getClient().getOBJECTID());
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
            List<AutoConfirmEmail> assignedAutoEmails = objectService.getAllSourceObjectsFromTarget(program.getListEditing().getOBJECTID(),
                    Assign_AutoConfirmEmail_List.class, AutoConfirmEmail.class);
            
            program.setSelectedConfirmationEmail(
                    (assignedAutoEmails == null || assignedAutoEmails.isEmpty())?
                            null : assignedAutoEmails.get(0));
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
            List<AutoWelcomeEmail> assignedAutoEmails = objectService.getAllSourceObjectsFromTarget(program.getListEditing().getOBJECTID(),
                    Assign_AutoWelcomeEmail_List.class, AutoWelcomeEmail.class);
            
            program.setSelectedWelcomeEmail(
                    (assignedAutoEmails == null || assignedAutoEmails.isEmpty()) ?
                            null : assignedAutoEmails.get(0)
            );
        } catch (EJBException ex) {
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }

    public void assignConfirmationEmail() {
        try {
            long selectedConfirmEmailId = program.getSelectedConfirmationEmailId();
            long editingListId = program.getListEditing().getOBJECTID();
            
            //Nothing is selected
            if (selectedConfirmEmailId <= 0) {
                subscriptionService.removeAllAssignedConfirmationEmailFromList(editingListId);
                FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_WARN, "Confirmation email unassigned. ", "You need to assign a confirmation email to start receiving signups.");
                this.resetConfirmationEmailPanel();
                return;
            }
            
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_FATAL, "Confirmation email assigned", null);
            Assign_AutoConfirmEmail_List newAssign = subscriptionService.assignConfirmationEmailToList(program.getSelectedConfirmationEmailId(), program.getListEditing().getOBJECTID());
            program.setSelectedConfirmationEmail(newAssign.getSOURCE());

        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (EJBException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }

    public void assignWelcomeEmail() {
        try {
            long selectedWelcomeEmailId = program.getSelectedWelcomeEmailId();
            long editingListId = program.getListEditing().getOBJECTID();

            //Nothing is selected
            if (selectedWelcomeEmailId <= 0) {
                subscriptionService.removeAllAssignedWelcomeEmailFromList(editingListId);
                FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_WARN, "Welcome email unassigned. ", "You need to assign a confirmation email to start receiving signups.");
                this.resetConfirmationEmailPanel();
                return;
            }

            Assign_AutoWelcomeEmail_List newAssign = subscriptionService.assignWelcomeEmailToList(program.getSelectedWelcomeEmailId(), program.getListEditing().getOBJECTID());
            program.setSelectedWelcomeEmail(newAssign.getSOURCE());
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_FATAL, "Welcome email assigned", null);

        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (EJBException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
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
