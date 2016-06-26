/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import java.util.List;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import segmail.entity.subscription.SubscriptionListField;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormImportSubscriber")
public class FormImportSubscriber {
    
    @Inject ProgramList program;
    
    private Map<Integer,String> listFieldMapping;

    public Map<Integer, String> getListFieldMapping() {
        return listFieldMapping;
    }

    public void setListFieldMapping(Map<Integer, String> listFieldMapping) {
        this.listFieldMapping = listFieldMapping;
    }
    
    public List<SubscriptionListField> getListFields() {
        return program.getFieldList(); //Assuming that FormListFieldSet has already loaded it
    }
    
    
}
