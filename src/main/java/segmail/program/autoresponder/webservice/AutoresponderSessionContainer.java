/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.autoresponder.webservice;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;

/**
 *
 * @author LeeKiatHaw
 */
@SessionScoped
public class AutoresponderSessionContainer implements Serializable {
    
    private List<SubscriptionListField> fields;
    
    private Map<String,String> randomSubscriber;
    
    private AutoresponderEmail editingTemplate;

    public List<SubscriptionListField> getFields() {
        return fields;
    }

    public void setFields(List<SubscriptionListField> fields) {
        this.fields = fields;
    }

    public Map<String, String> getRandomSubscriber() {
        return randomSubscriber;
    }

    public void setRandomSubscriber(Map<String, String> randomSubscriber) {
        this.randomSubscriber = randomSubscriber;
    }

    public AutoresponderEmail getEditingTemplate() {
        return editingTemplate;
    }

    public void setEditingTemplate(AutoresponderEmail editingTemplate) {
        this.editingTemplate = editingTemplate;
    }
    
    
}
