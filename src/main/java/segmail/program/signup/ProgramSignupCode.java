/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.signup;

import java.util.List;
import seca2.program.Program;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;

/**
 *
 * @author LeeKiatHaw
 */
public class ProgramSignupCode extends Program {

    private List<SubscriptionList> ownedLists;
    //private List<String> selectedLists; //JSF selectMany component will store it as String.
    private String listFieldsJson;
    private long selectedListId;
    
    private List<SubscriptionListField> fields;
    
    private List<AutoresponderEmail> confirmEmails;
    
    private SubscriptionList selectedList;

    public String getListFieldsJson() {
        return listFieldsJson;
    }

    public void setListFieldsJson(String listFieldsJson) {
        this.listFieldsJson = listFieldsJson;
    }

    public List<SubscriptionList> getOwnedLists() {
        return ownedLists;
    }

    public void setOwnedLists(List<SubscriptionList> ownedLists) {
        this.ownedLists = ownedLists;
    }

    public long getSelectedListId() {
        return selectedListId;
    }

    public void setSelectedListId(long selectedListId) {
        this.selectedListId = selectedListId;
    }

    public List<SubscriptionListField> getFields() {
        return fields;
    }

    public void setFields(List<SubscriptionListField> fields) {
        this.fields = fields;
    }

    public List<AutoresponderEmail> getConfirmEmails() {
        return confirmEmails;
    }

    public void setConfirmEmails(List<AutoresponderEmail> confirmEmails) {
        this.confirmEmails = confirmEmails;
    }

    public SubscriptionList getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(SubscriptionList selectedList) {
        this.selectedList = selectedList;
    }
    
    @Override
    public void clearVariables() {

    }

    @Override
    public void initRequestParams() {

    }

    @Override
    public void initProgram() {

    }

}
