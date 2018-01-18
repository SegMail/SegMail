/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.wizard;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import segmail.entity.subscription.SubscriptionList;
import segmail.program.signup.FormSignupCode;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormWizardCollect")
public class FormWizardCollect {
    @Inject ProgramSetupWizard program;
    @Inject FormWizardInit formWizard;
    
    // This time we do it differently and leverage on existing code
    @Inject FormSignupCode formSignupCode;
    
    @PostConstruct
    public void init() {
        initSignupCodeForm();
    }
    
    public SubscriptionList getSelectedList() {
        return program.getSelectedList();
    }

    public void setSelectedList(SubscriptionList selectedList) {
        program.setSelectedList(selectedList);
    }
    
    public void initSignupCodeForm() {
        SubscriptionList selectedList = getSelectedList();
        formSignupCode.setSelectedList(selectedList);
        formSignupCode.setSelectedListId(selectedList.getOBJECTID());
        
        formSignupCode.loadListFields();
    }
    
}
