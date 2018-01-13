/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import eds.component.GenericObjectService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.joda.time.DateTime;
import seca2.bootstrap.UserRequestContainer;
import segmail.component.subscription.ListService;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.Subscription;
import segmail.entity.subscription.SubscriptionListField;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormSubscriberView")
public class FormSubscriberView {
    
    @Inject ProgramSubscribers program;
    @Inject UserRequestContainer reqCont;
    
    @EJB SubscriptionService subService;
    @EJB GenericObjectService objService;
    @EJB ListService listService;
    
    public SubscriberAccount getSubscriber() {
        return program.getSubscriber();
    }

    public void setSubscriber(SubscriberAccount subscriber) {
        program.setSubscriber(subscriber);
    }

    public Map<String, SubscriberFieldValue> getSubscriberValues() {
        return program.getSubscriberValues();
    }

    public void setSubscriberValues(Map<String, SubscriberFieldValue> subscriberValues) {
        program.setSubscriberValues(subscriberValues);
    }

    public List<Subscription> getSubscriptions() {
        return program.getSubscriptions();
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        program.setSubscriptions(subscriptions);
    }
    
    public Map<String, String> getStatusColor() {
        return program.getStatusColor();
    }

    public void setStatusColor(Map<String, String> statusColor) {
        program.setStatusColor(statusColor);
    }
    
    public String getSubscriberAge() {
        return program.getSubscriberAge();
    }

    public void setSubscriberAge(String subscriberAge) {
        program.setSubscriberAge(subscriberAge);
    }
    
    public DateTime getSubscribedSince() {
        return program.getSubscribedSince();
    }

    public void setSubscribedSince(DateTime subscribedSince) {
        program.setSubscribedSince(subscribedSince);
    }
    
    public List<SubscriptionListField> getFieldList() {
        return program.getFieldList();
    }

    public void setFieldList(List<SubscriptionListField> fieldList) {
        program.setFieldList(fieldList);
    }
    
    public List<SubscriptionListField> getRemainFields() {
        return program.getRemainFields();
    }

    public void setRemainFields(List<SubscriptionListField> remainFields) {
        program.setRemainFields(remainFields);
    }
    
    @PostConstruct
    public void init(){
        if(!FacesContext.getCurrentInstance().isPostback()) {
            loadSubscriber();
            calculateAge();
            loadFieldLists();
        }
    }
    
    public void loadSubscriber() {
        
        // This workaround is here because:
        // 1) We need to use the c:forEach JSF component in subscriber_view.xhtml page,
        // instead of ui:repeat because ui:repeat doesn't work with Maps 
        // 2) By using c:forEach, we cannot prevent this form bean from rendering
        // even by using the rendered attribute because by default, all c: component
        // will be rendered.
        if(reqCont.getProgramParamsOrdered().size() <= 0)
            return;
        
        // Set the subscriber object first
        long subscriberId = Long.parseLong(reqCont.getProgramParamsOrdered().get(0));
        SubscriberAccount subscriber = objService.getEnterpriseObjectById(subscriberId, SubscriberAccount.class);
        setSubscriber(subscriber);
        
        // If the URL was inputted manually with an invalid subscriberId, then 
        // redirect it back to the /subscriber page
        if(subscriber == null) {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            try {
                ec.redirect(ec.getRequestContextPath() + "/subscribers");
            } catch (IOException ex) {
                Logger.getLogger(FormSubscriberView.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Cannot redirect to /subscribers");
            }
            return;
        }
        
        // Set values/attributes
        program.loadSubscriberValues(subscriberId);
        /*List<SubscriberFieldValue> values = objService.getEnterpriseData(subscriberId, SubscriberFieldValue.class);
        List<String> fieldkeys = new ArrayList<>(); // This is for later
        Map<String,SubscriberFieldValue> subscriberValues = new HashMap<>();
        values.forEach(v -> {
            if(!fieldkeys.contains(v.getFIELD_KEY())) {
                fieldkeys.add(v.getFIELD_KEY());
            }
            subscriberValues.put(v.getFIELD_KEY(), v);
        });
        setSubscriberValues(subscriberValues);*/
        
        // Set subscriptions
        List<Subscription> subscriptions = objService.getRelationshipsForSourceObject(subscriberId, Subscription.class);
        setSubscriptions(subscriptions);
        
        // Set field-value map containing both field and value information
        List<Long> listIds = subscriptions.stream().map(s -> s.getTARGET().getOBJECTID()).collect(toList());
        List<SubscriptionListField> fields = listService.getFieldsByKeyOrLists(
                new ArrayList<>(getSubscriberValues().keySet()), listIds);
        setFieldList(fields);
        
        // Set since date
        setSubscribedSince(new DateTime(subscriber.getDATE_CREATED()));
        
    }
    
    public void calculateAge() {
        SubscriberAccount acc = getSubscriber();
        String ageString = subService.calculateSubscriberAge(acc, DateTime.now());
        
        setSubscriberAge(ageString);
    }
    
    public void loadFieldLists() {
        
        List<SubscriptionListField> fields = getFieldList();
        Map<String,SubscriberFieldValue> valueMap = getSubscriberValues();
        
        setRemainFields(new ArrayList<>());
        for(SubscriptionListField field : fields) {
            if(!valueMap.containsKey(field.generateKey())) {
                getRemainFields().add(field);
            }
        }
        
    }
    
    public void resendConfirmation() {
        
    }
    
    public void sendAnEmail() {
        
    }
    
    public void removeFromList() {
        
    }
    
    public void updateField() {
        
    }
    
    
}
