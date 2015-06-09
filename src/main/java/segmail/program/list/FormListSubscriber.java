/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import eds.component.subscription.SubscriptionService;
import segmail.entity.subscription.SubscriberAccount;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.User.UserContainer;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormListSubscriber")
@RequestScoped
public class FormListSubscriber {
    @Inject private ProgramList programList;
    @Inject private UserContainer userContainer;
    
    @EJB private SubscriptionService subService;
    
    private boolean removed;
    
    private SubscriberAccount subscriber;// = new SubscriberAccount();
    
    private final String formName = "add_new_sub_form";
    
    @PostConstruct
    public void init(){
        //Can we use flash scope here?
        subscriber = new SubscriberAccount();
    }
    

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }
    
    public void addSubscriber(){
        try {
            if(programList.getListEditing() == null)
                throw new RuntimeException("List is not set yet but you still manage to come to this page? Notify your admin immediately! =)");
            
            subService.subscribe(subscriber, programList.getListEditing().getOBJECTID(),true);
            //How to redirect to List editing panel?
            
        } catch (EJBException ex) { //Transaction did not go through
            //Throwable cause = ex.getCause();
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public SubscriberAccount getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(SubscriberAccount subscriber) {
        this.subscriber = subscriber;
    }
    
    public void loadSubscribers(){
        try {
            
        } catch (EJBException ex) { //Transaction did not go through
            //Throwable cause = ex.getCause();
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
}
