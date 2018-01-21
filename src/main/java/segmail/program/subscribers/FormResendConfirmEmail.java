/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import eds.component.GenericObjectService;
import eds.component.data.DataValidationException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipNotFoundException;
import eds.component.mail.InvalidEmailException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.Subscription;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormResendConfirmEmail")
public class FormResendConfirmEmail {

    @Inject
    ProgramSubscribers program;

    @EJB
    SubscriptionService subService;
    @EJB
    GenericObjectService objService;

    private long sourceId;
    private long targetId;

    @PostConstruct
    public void init() {
        if (!FacesContext.getCurrentInstance().isPostback()) {

        }
    }

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public void resend() {
        try {
            List<Subscription> subscriptions = objService.getRelationshipsForObject(sourceId, targetId, Subscription.class);

            if (subscriptions == null || subscriptions.isEmpty()) {
                throw new RelationshipNotFoundException("Subscription is not found.");
            }
            
            subService.sendConfirmationEmail(subscriptions.get(0));
            
            FacesMessenger.setFacesMessage(ProgramSubscribers.class.getSimpleName(), FacesMessage.SEVERITY_FATAL, "Confirmation email has been resent!", "");
            
            program.refresh();
        } catch (RelationshipNotFoundException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (DataValidationException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (InvalidEmailException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }
}
