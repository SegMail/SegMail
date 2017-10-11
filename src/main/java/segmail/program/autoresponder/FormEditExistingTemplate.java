/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.autoresponder;

import eds.component.GenericObjectService;
import eds.component.data.DataValidationException;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.UserSessionContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.FormEditEntity;
import segmail.component.subscription.ListService;
import segmail.component.subscription.SubscriptionService;
import segmail.component.subscription.autoresponder.AutoresponderService;
import segmail.component.subscription.mailmerge.MailMergeService;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;
import static segmail.entity.subscription.autoresponder.AUTO_EMAIL_TYPE.CONFIRMATION;
import static segmail.entity.subscription.autoresponder.AUTO_EMAIL_TYPE.WELCOME;
import segmail.entity.subscription.autoresponder.Assign_AutoresponderEmail_List;
import segmail.entity.subscription.email.mailmerge.MAILMERGE_REQUEST;
import segmail.program.autoresponder.webservice.AutoresponderSessionContainer;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormEditExistingTemplate")
@RequestScoped
public class FormEditExistingTemplate implements FormEditEntity {

    @EJB
    private AutoresponderService autoresponderService;
    @EJB
    private GenericObjectService objectService;
    //@EJB private UserService userService;
    @EJB
    private ListService listService;
    @EJB
    private SubscriptionService subService;
    @EJB
    MailMergeService mmService;

    @Inject
    private ProgramAutoresponder program;

    @Inject
    private UserSessionContainer userContainer;
    @Inject
    private UserRequestContainer requestContainer;
    @Inject AutoresponderSessionContainer autoresponderCont;

    @PostConstruct
    public void init() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            loadListAndListFields();
            loadRandomSubscriber();
            loadMMUrls();
        }
    }

    @Override
    public void closeWithoutSaving() {
        program.refresh();
    }

    public ProgramAutoresponder getProgram() {
        return program;
    }

    public void setProgram(ProgramAutoresponder program) {
        this.program = program;
    }

    
    public AutoresponderEmail getEditingTemplate() {
        return program.getEditingTemplate();
    }

    public void setEditingTemplate(AutoresponderEmail editingTemplate) {
        program.setEditingTemplate(editingTemplate);
    }
    
    public List<SubscriptionListField> getListFields() {
        return autoresponderCont.getFields();
    }

    public void setListFields(List<SubscriptionListField> listFields) {
        autoresponderCont.setFields(listFields);
    }
    
    public SubscriptionList getAssignedList() {
        return program.getAssignedList();
    }

    public void setAssignedList(SubscriptionList assignedList) {
        program.setAssignedList(assignedList);
    }
    
    public Map<String, String> getRandomSubscriber() {
        return autoresponderCont.getRandomSubscriber();
    }

    public void setRandomSubscriber(Map<String, String> randomSubscriber) {
        autoresponderCont.setRandomSubscriber(randomSubscriber);
    }
    
    public MAILMERGE_REQUEST[] getMailmergeLinkTags() {
        return program.getMailmergeLinkTags();
    }
    
    public Map<String, String> getMailmergeLinks() {
        return program.getMailmergeLinks();
    }

    public void setMailmergeLinks(Map<String, String> mailmergeLinks) {
        program.setMailmergeLinks(mailmergeLinks);
    }

    public void setMailmergeLinkTags(MAILMERGE_REQUEST[] mailmergeLinkTags) {
        program.setMailmergeLinkTags(mailmergeLinkTags);
    }
    
    public List<String> getListTags() {
        return program.getListTags();
    }

    public void setListTags(List<String> campaignTags) {
        program.setListTags(campaignTags);
    }

    public String getPreviewBody() {
        return program.getPreviewBody();
    }

    public void setPreviewBody(String previewBody) {
        program.setPreviewBody(previewBody);
    }
    
    @Override
    public void saveAndContinue() {
        try {
            AutoresponderEmail newTemplate = autoresponderService.saveAutoEmail(program.getEditingTemplate());

            program.setEditingTemplate(newTemplate);
            loadListAndListFields();
            //Set success message
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Template updated.", null);

        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (EntityExistsException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (EJBException ex) { //Transaction did not go through
            //Throwable cause = ex.getCause();
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    @Override
    public void saveAndClose() {
        saveAndContinue();
        closeWithoutSaving();
    }

    @Override
    public void delete() {
        try {
            autoresponderService.deleteAutoEmail(program.getEditingTemplate().getOBJECTID());
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Template deleted.", null);
            program.refresh();

        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }
    
    public void loadListAndListFields() {
        setAssignedList(null);
        setListFields(null);
        
        if(this.getEditingTemplate() == null) 
            return;
        
        List<SubscriptionList> assignedLists = this.objectService.getAllTargetObjectsFromSource(
                getEditingTemplate().getOBJECTID(), 
                Assign_AutoresponderEmail_List.class, 
                SubscriptionList.class);
        
        if(assignedLists == null || assignedLists.isEmpty())
            throw new RuntimeException("List is not assigned.");
        
        SubscriptionList assignedList = assignedLists.get(0);
        
        setAssignedList(assignedList);
        
        List<SubscriptionListField> listFields = listService.getFieldsForSubscriptionList(assignedList.getOBJECTID());
        
        setListFields(listFields);
    }
    
    public void loadRandomSubscriber() {
        //Clear it first
        setRandomSubscriber(new HashMap<String,String>());
        
        if(this.getEditingTemplate() == null) 
            return;
        
        //This is a coding errror
        if(this.getAssignedList() == null)
            throw new RuntimeException("No assigned lists found.");
        
        SubscriptionList assignedList = getAssignedList();
        Map<Long, Map<String, String>> subscribers = subService.getSubscriberValuesMap(assignedList.getOBJECTID(), 0, 1);
        
        for(Long id : subscribers.keySet()) {
            setRandomSubscriber(subscribers.get(id));
            getRandomSubscriber().put("OBJECTID", id.toString());
        }
    }
    
    public void loadMMUrls() {

        try {
            setMailmergeLinks(new HashMap<String, String>());
            for (MAILMERGE_REQUEST request : this.getMailmergeLinkTags()) {
                //For confirm emails, don't load unsubscribe links
                //For welcome emails, don't load confirm links
                if(request.equals(MAILMERGE_REQUEST.CONFIRM) && WELCOME.name.equals(getEditingTemplate().getTYPE())) {
                    continue;
                }
                if(request.equals(MAILMERGE_REQUEST.UNSUBSCRIBE) && CONFIRMATION.name.equals(getEditingTemplate().getTYPE())) {
                    continue;
                }
                String url = mmService.getSystemTestLink(request.label());
                getMailmergeLinks().put(request.label(), url);
            }
        } catch (DataValidationException ex) {
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } 
    }
    
    public void loadListTags() {
        
        List<String> listTags = new ArrayList<>();
        listTags.add(SubscriptionList.MM_SENDER_NAME);
        listTags.add(SubscriptionList.MM_SUPPORT_EMAIL);
        
        this.setListTags(listTags);
    }
}
