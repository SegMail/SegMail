/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.autoresponder;

import segmail.entity.subscription.autoresponder.AutoresponderEmail;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserSessionContainer;
import seca2.program.Program;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;
import segmail.entity.subscription.email.mailmerge.MAILMERGE_REQUEST;

/**
 *
 * @author LeeKiatHaw
 */
@Named("ProgramAutoresponder")
@SessionScoped
public class ProgramAutoresponder extends Program{
    
    
    @Inject private UserSessionContainer userContainer;
    
    
    private List<AutoresponderEmail> confirmationTemplates;
    
    private List<AutoresponderEmail> welcomeTemplates;
    
    private AutoresponderEmail editingTemplate;
    
    private Map<Long,SubscriptionList> assignedToLists;
    
    private long editingTemplateId;
    
    private boolean edit;
    
    private List<SubscriptionList> lists;
    
    private long selectedListId;
    
    private List<SubscriptionListField> listFields;
    
    private SubscriptionList assignedList;
    
    private Map<String,String> randomSubscriber;
    
    private MAILMERGE_REQUEST[] mailmergeLinkTags = MAILMERGE_REQUEST.values();
    
    private Map<String,String> mailmergeLinks = new HashMap<>();
    
    private List<String> listTags;

    public List<AutoresponderEmail> getConfirmationTemplates() {
        return confirmationTemplates;
    }

    public void setConfirmationTemplates(List<AutoresponderEmail> confirmationTemplates) {
        this.confirmationTemplates = confirmationTemplates;
    }

    public List<AutoresponderEmail> getWelcomeTemplates() {
        return welcomeTemplates;
    }

    public void setWelcomeTemplates(List<AutoresponderEmail> welcomeTemplates) {
        this.welcomeTemplates = welcomeTemplates;
    }

    UserSessionContainer getUserContainer() {
        return userContainer;
    }

    void setUserContainer(UserSessionContainer userContainer) {
        this.userContainer = userContainer;
    }

    public AutoresponderEmail getEditingTemplate() {
        return editingTemplate;
    }

    public void setEditingTemplate(AutoresponderEmail editingTemplate) {
        this.editingTemplate = editingTemplate;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public long getEditingTemplateId() {
        return editingTemplateId;
    }

    public void setEditingTemplateId(long editingTemplateId) {
        this.editingTemplateId = editingTemplateId;
    }

    public List<SubscriptionList> getLists() {
        return lists;
    }

    public void setLists(List<SubscriptionList> lists) {
        this.lists = lists;
    }

    public long getSelectedListId() {
        return selectedListId;
    }

    public void setSelectedListId(long selectedListId) {
        this.selectedListId = selectedListId;
    }

    public List<SubscriptionListField> getListFields() {
        return listFields;
    }

    public void setListFields(List<SubscriptionListField> listFields) {
        this.listFields = listFields;
    }

    public SubscriptionList getAssignedList() {
        return assignedList;
    }

    public void setAssignedList(SubscriptionList assignedList) {
        this.assignedList = assignedList;
    }

    public Map<String, String> getRandomSubscriber() {
        return randomSubscriber;
    }

    public void setRandomSubscriber(Map<String, String> randomSubscriber) {
        this.randomSubscriber = randomSubscriber;
    }

    public MAILMERGE_REQUEST[] getMailmergeLinkTags() {
        return mailmergeLinkTags;
    }

    public void setMailmergeLinkTags(MAILMERGE_REQUEST[] mailmergeLinkTags) {
        this.mailmergeLinkTags = mailmergeLinkTags;
    }

    public Map<String, String> getMailmergeLinks() {
        return mailmergeLinks;
    }

    public void setMailmergeLinks(Map<String, String> mailmergeLinks) {
        this.mailmergeLinks = mailmergeLinks;
    }

    public Map<Long, SubscriptionList> getAssignedToLists() {
        return assignedToLists;
    }

    public void setAssignedToLists(Map<Long, SubscriptionList> assignedToLists) {
        this.assignedToLists = assignedToLists;
    }
    
    public List<String> getListTags() {
        return listTags;
    }

    public void setListTags(List<String> listTags) {
        this.listTags = listTags;
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
