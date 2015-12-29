/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.SubscriptionService;
import segmail.entity.subscription.SubscriptionListFieldList;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormListFieldSet")
@RequestScoped
public class FormListFieldSet {
    
    @Inject private ProgramList program;
    @Inject private ClientContainer clientContainer;

    @EJB private SubscriptionService subscriptionService;
    
    private final String formName = "form_list_fieldset";
    
    @PostConstruct
    public void init(){
        FacesContext fc = FacesContext.getCurrentInstance();
        if (!fc.isPostback()) {
           loadFormFields(program.getListEditingId());
        }
    }
    
    public void loadFormFields(long listId){
        try {
            //to improve performance
            if(listId <= 0) return;
            
            SubscriptionListFieldList fieldList = subscriptionService.getFieldListForSubscriptionList(listId);
            
            this.program.setFieldList(fieldList);
            
        } catch (EJBException ex) {
            FacesMessenger.setFacesMessage(program.getFormName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
        
    }
    
    public SubscriptionListFieldList getFieldList(){
        return program.getFieldList();
    }
}
