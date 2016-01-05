/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import java.util.ArrayList;
import java.util.List;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.SubscriberAccount;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserSessionContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.entity.subscription.FIELD_TYPE;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormListSubscriber")
@RequestScoped
public class FormListSubscriber {
    
    public final int SUBSCRIBERS_PER_PAGE = 100;

    @Inject
    private ProgramList program;
    @Inject
    private UserSessionContainer userContainer;

    @EJB
    private SubscriptionService subService;

    private boolean removed;

    private SubscriberAccount subscriber;// = new SubscriberAccount();

    private final String formName = "add_new_sub_form";

    @PostConstruct
    public void init() {
        //Can we use flash scope here?
        FacesContext fc = FacesContext.getCurrentInstance();
        if (!fc.isPostback()) {
            initSubscriberAccount();
            initSubscriberFields();
        }
    }

    public void initSubscriberAccount() {
        subscriber = new SubscriberAccount();
    }

    public void initSubscriberFields() {
        long listId = program.getListEditingId();
        
        if (listId <= 0) {
            return;
        }
        List<SubscriptionListField> fields = program.getFieldList();
        List<SubscriberFieldValue> values = subService.constructSubscriberFieldValues(fields);
        program.setFieldValues(values);
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public void addSubscriber() {
        try {
            if (program.getListEditing() == null) {
                throw new RuntimeException("List is not set yet but you still manage to come to this page? Notify your admin immediately! =)");
            }

            subService.subscribe(program.getListEditingId(), this.getFieldValues());
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_FATAL, "Subscriber added!", null);
            //How to redirect to List editing panel?
            program.refresh();
            
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

    public void loadSubscribers() {
        try {
            SubscriptionList listEditing = getListEditing();
            //Load the maximum # of subscribers depending on the page
            //If list has 101, SUBSCRIBERS_PER_PAGE = 100 and page = 2,
            //load subscriber # (2-1)*100=100 to # (2)*100=200.
            
            
        } catch (EJBException ex) { 
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public SubscriptionList getListEditing() {
        return program.getListEditing();
    }

    public List<SubscriptionListField> getListFields() {
        return program.getFieldList(); //Assuming that FormListFieldSet has already loaded it
    }

    public String getEmailType() {
        return FIELD_TYPE.EMAIL.name();
    }

    public String getTextType() {
        return FIELD_TYPE.TEXT.name();
    }

    public List<SubscriberFieldValue> getFieldValues() {
        return program.getFieldValues();
    }
}
