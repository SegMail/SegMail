/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import eds.component.UpdateObjectService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.entity.subscription.SubscriberAccount;
import segmail.entity.subscription.SubscriberFieldValue;
import segmail.entity.subscription.SubscriptionListField;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormAddSubscriberField")
public class FormAddSubscriberField {
    
    @Inject ProgramSubscribers program;
    
    @EJB UpdateObjectService updService;
    
    private String fieldKey;
    private String fieldValue;
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()) {
            
        }
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
    
    public List<SubscriptionListField> getFieldList() {
        return program.getFieldList();
    }

    public void setFieldList(List<SubscriptionListField> fieldList) {
        program.setFieldList(fieldList);
    }
    
    public Map<String, SubscriberFieldValue> getSubscriberValues() {
        return program.getSubscriberValues();
    }    
    
    public List<SubscriptionListField> getRemainFields() {
        return program.getRemainFields();
    }
    
    public void addField() {
        try {
            SubscriberAccount acc = program.getSubscriber();
            Map<String,SubscriberFieldValue> fieldVals = program.getSubscriberValues();

            // Without querying the DB!
            SubscriberFieldValue highestSNO = fieldVals.values().stream()
                    .max((v1,v2) -> Integer.compare(v1.getSNO(), v2.getSNO()))
                    .get();
            int sno = highestSNO.getSNO() + 1;

            SubscriberFieldValue newValue = new SubscriberFieldValue();
            newValue.setOWNER(acc);
            newValue.setFIELD_KEY(fieldKey);
            newValue.setVALUE(fieldValue);
            newValue.setSNO(sno);

            newValue = (SubscriberFieldValue) updService.persist(newValue);

            program.refresh();
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }
}
