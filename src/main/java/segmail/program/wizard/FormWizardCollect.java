/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.wizard;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;
import segmail.program.signup.FormSignupCode;
import segmail.program.subscribers.FormImportSubscribers;
import segmail.program.subscribers.FormSetupDatasource;

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
    @Inject FormImportSubscribers formImportSubscribers;
    @Inject FormSetupDatasource formSetupDatasource;
    @Inject ClientContainer clientCont;
    
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
    
    public List<AutoresponderEmail> getConfirmEmails() {
        return formSignupCode.getConfirmEmails();
    }

    public void setConfirmEmails(List<AutoresponderEmail> confirmEmails) {
        formSignupCode.setConfirmEmails(confirmEmails);
    }
    
    public List<SubscriptionListField> getFields() {
        return formSignupCode.getFields();
    }

    public void setFields(List<SubscriptionListField> fields) {
        formSignupCode.setFields(fields);
    }
    
    public String getSignupLink() {
        return formSignupCode.getSignupLink();
    }
    
    public long getClientId() {
        return clientCont.getClient().getOBJECTID();
    }
    
    public void initSignupCodeForm() {
        SubscriptionList selectedList = getSelectedList();
        formSignupCode.setSelectedList(selectedList);
        formSignupCode.setSelectedListId(selectedList.getOBJECTID());
        
        formSignupCode.loadListFields();
    }
    
    public void initImportSubscribers() {
        formImportSubscribers.setOwnedLists(program.getExistingLists());
        List<String> idStrings = new ArrayList<>();
        idStrings.add(program.getSelectedList().getOBJECTID() + "");
        formImportSubscribers.setAssignedLists(idStrings);
        
        formImportSubscribers.setupImport();
        
    }
    
    public void initSetupDatasource() {
        formSetupDatasource.setOwnedLists(program.getExistingLists());
        formSetupDatasource.setSelectedListId(program.getSelectedList().getOBJECTID());
        
    }
    
}
