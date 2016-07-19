/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription;

import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;

/**
 *
 * @author LeeKiatHaw
 */
@SessionScoped
public class SubscriptionContainer implements Serializable {
    
    private SubscriptionList list;
    private List<SubscriptionListField> listFields;

    public SubscriptionList getList() {
        return list;
    }

    public void setList(SubscriptionList list) {
        this.list = list;
    }

    public List<SubscriptionListField> getListFields() {
        return listFields;
    }

    public void setListFields(List<SubscriptionListField> listFields) {
        this.listFields = listFields;
    }
    
    
}
