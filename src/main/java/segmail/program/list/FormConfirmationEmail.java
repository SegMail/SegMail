/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

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
import segmail.entity.subscription.email.EmailTemplate;

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
    
    private final String formName = "form_confirm_email";
    
    @PostConstruct
    public void init(){
        FacesContext fc = FacesContext.getCurrentInstance();
        if(!fc.isPostback()){
            loadAvailableConfirmationEmails();
        }
    }
    
    public void loadAvailableConfirmationEmails(){
        try {
            List<EmailTemplate> confirmEmails = subscriptionService.getAvailableTemplatesForClient(
                    clientContainer.getClient().getOBJECTID(), EmailTemplate.EMAIL_TYPE.CONFIRMATION);
            program.setConfirmationEmails(confirmEmails);
        } catch(EJBException ex) {
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }
    
    public void assignConfirmationEmail(){
        try {
            subscriptionService.assignConfirmationEmailToList(program.getSelectedConfirmationEmailId(), program.getListEditing().getOBJECTID());
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_FATAL, "Confirmation email assigned", null);
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch(EJBException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } 
    }

    public ProgramList getProgram() {
        return program;
    }

    public void setProgram(ProgramList program) {
        this.program = program;
    }

    
}
