/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import eds.component.data.DataValidationException;
import eds.component.data.EntityNotFoundException;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.SubscriberService;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormDeleteSubscriberField")
public class FormDeleteSubscriberField {
    @Inject ProgramSubscribers program;
    
    @EJB SubscriberService accService;
    
    private long subscriberId;
    private String fieldKey;

    public long getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(long subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }
    
    
    
    public void delete() {
        if(subscriberId <= 0)
            throw new RuntimeException("Subscriber Id is missing.");

        if(fieldKey == null || fieldKey.isEmpty())
            throw new RuntimeException("Field key is missing.");
        
        try {
            int result = accService.deleteSubscriberField(subscriberId, fieldKey);
            
            if(result <= 0) {
                throw new EntityNotFoundException("Field value doesn't exist anymore. You can close this window.");
            }
            
            program.refresh();
        } catch (DataValidationException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }
}
