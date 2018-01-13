/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import eds.component.data.RelationshipNotFoundException;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.SUBSCRIPTION_STATUS;
import segmail.entity.subscription.Subscription;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormRemoveSubscription")
public class FormRemoveSubscription {

    @Inject ProgramSubscribers program;
    
    @EJB SubscriptionService subService;
    
    private String unsubKey;

    public String getUnsubKey() {
        return unsubKey;
    }

    public void setUnsubKey(String unsubKey) {
        this.unsubKey = unsubKey;
    }
    
    public void remove() {
        try {
            List<Subscription> result = subService.updateSubscription(unsubKey,SUBSCRIPTION_STATUS.REMOVED);
            
            FacesMessenger.setFacesMessage(ProgramSubscribers.class.getSimpleName(), FacesMessage.SEVERITY_FATAL, 
                    "Subscription to "+result.get(0).getTARGET().getLIST_NAME()+" has been removed.", "");
            
            program.refresh();
        } catch (RelationshipNotFoundException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }
}
