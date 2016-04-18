/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import eds.component.batch.BatchProcesingException;
import eds.component.data.DataValidationException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.data.RelationshipExistsException;
import java.util.List;
import java.util.Map;
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
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormListSubscriber")
@RequestScoped
public class FormListSubscriber {
    
    private final int SUBSCRIBERS_PER_PAGE = 100;

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
            loadListFields();
            initSubscriberAccount();
            initSubscriberFields();
            loadSubscribers();
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
        Map<String,Object> values = subService.constructSubscriberFieldValues(fields);
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
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_FATAL, "Subscriber added! A welcome email will be sent to the subscriber soon.", null);
            //How to redirect to List editing panel?
            program.refresh();
            
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (DataValidationException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (RelationshipExistsException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, "Subscriber already exist in this list", "");
        } catch (BatchProcesingException ex) {
            ex.printStackTrace(System.out);
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, "Something wrong with the sending of emails", "Please contact your system admin.");
        } /*catch (Exception ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }*/
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
            if(listEditing == null) return;
            //Load the maximum # of subscribers depending on the page
            //If list has 101, SUBSCRIBERS_PER_PAGE = 100 and page = 2,
            //load subscriber # (2-1)*100=100 to # (2)*100=200.
            int pageNum = getPage();
            Map<Long,Map<String,String>> accounts = 
                    subService.getSubscriberValuesMap(listEditing.getOBJECTID(), pageNum*SUBSCRIBERS_PER_PAGE, SUBSCRIBERS_PER_PAGE);
            
            program.setSubscriberTable(accounts);
            
        } catch (EJBException ex) { 
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void loadListFields() {
        try {
            long listId = program.getListEditingId();
            //to improve performance
            //no! it's necessary else there will be nullpointerexception :p
            if (listId <= 0) {
                return;
            }
            List<SubscriptionListField> fieldList = subService.getFieldsForSubscriptionList(listId);
            this.program.setFieldList(fieldList);

        } catch (EJBException ex) {
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
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

    public Map<String,Object> getFieldValues() {
        return program.getFieldValues();
    }

    public int getSUBSCRIBERS_PER_PAGE() {
        return SUBSCRIBERS_PER_PAGE;
    }
    
    public int getPage(){
        return program.getPage();
    }
    
    public Map<Long,Map<String,String>> getSubscriberTable(){
        return program.getSubscriberTable();
    }
}
