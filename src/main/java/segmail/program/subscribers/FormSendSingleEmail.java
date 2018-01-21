/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import eds.component.GenericObjectService;
import eds.component.data.DataValidationException;
import eds.component.mail.MailServiceOutbound;
import eds.entity.client.VerifiedSendingAddress;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import org.joda.time.DateTime;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberFieldValue;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormSendSingleEmail")
public class FormSendSingleEmail {
    
    @Inject ProgramSubscribers program;
    
    @Inject UserRequestContainer reqCont;
    @Inject ClientContainer clientCont;
    
    @EJB GenericObjectService objService;
    @EJB MailServiceOutbound mailService;
    
    public boolean isPreview() {
        return program.isPreview();
    }

    public void setPreview(boolean preview) {
        program.setPreview(preview);
    }
    
    public String getSubject() {
        return program.getSubject();
    }

    public void setSubject(String subject) {
        program.setSubject(subject);
    }

    public String getBody() {
        return program.getBody();
    }

    public void setBody(String body) {
        program.setBody(body);
    }
    
    public Map<String, SubscriberFieldValue> getSubscriberValues() {
        return program.getSubscriberValues();
    }

    public void setSubscriberValues(Map<String, SubscriberFieldValue> subscriberValues) {
        program.setSubscriberValues(subscriberValues);
    }
    
    public List<VerifiedSendingAddress> getVerifiedAddresses() {
        return program.getVerifiedAddresses();
    }

    public void setVerifiedAddresses(List<VerifiedSendingAddress> verifiedAddresses) {
        program.setVerifiedAddresses(verifiedAddresses);
    }
    
    public String getSenderAddress() {
        return program.getSenderAddress();
    }

    public void setSenderAddress(String senderAddress) {
        program.setSenderAddress(senderAddress);
    }
    
    public String getRecipient() {
        return program.getRecipient();
    }

    public void setRecipient(String recipient) {
        program.setRecipient(recipient);
    }
    
    public void resetPreview() {
        this.setPreview(false);
    }
    
    public void initMMTags() {
        // Reload the SubscriberFieldValues list
        if(reqCont.getProgramParamsOrdered().size() <= 0)
            return;
        
        long subscriberId = Long.parseLong(reqCont.getProgramParamsOrdered().get(0));
        
        program.loadSubscriberValues(subscriberId);
    }
    
    public void initVerifiedAddresses() {
        List<VerifiedSendingAddress> verifiedAddresses = objService.getEnterpriseData(clientCont.getClient().getOBJECTID(), VerifiedSendingAddress.class);
        setVerifiedAddresses(verifiedAddresses);
    }
    
    public void initRecipient() {
        SubscriberAccount recipient = program.getSubscriber();
        
        setRecipient(recipient.getEMAIL());
    }
    
    public void previewEmail() {
        // Do some checkes before proceeding
        try {
            String subject = getSubject();
            String sender = this.getSenderAddress();
            String body = this.getBody();
            
            if(subject == null || subject.isEmpty())
                throw new DataValidationException("Please enter a Subject.");
            
            if(sender == null || sender.isEmpty())
                throw new DataValidationException("Please choose a sending address.");
            
            if(body == null || body.isEmpty())
                throw new DataValidationException("Please enter some text.");
            
            this.setPreview(true);
            
        } catch (DataValidationException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }
    
    public void backToEdit() {
        resetPreview();
    }
    
    public void sendEmail() {
        mailService.sendQuickMail(
                this.getSubject(), 
                this.getBody(), 
                this.getSenderAddress(), 
                DateTime.now(), 
                true, 
                this.getRecipient());
        
        program.refresh();
    }
    
    public void init() {
        this.resetPreview();
        this.initMMTags();
        this.initVerifiedAddresses();
        this.initRecipient();
    }
}
