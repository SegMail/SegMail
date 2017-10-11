/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.wizard;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.data.DataValidationException;
import eds.component.data.IncompleteDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.ListService;
import segmail.component.subscription.mailmerge.MailMergeService;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.SubscriptionListField;
import static segmail.entity.subscription.autoresponder.AUTO_EMAIL_TYPE.CONFIRMATION;
import static segmail.entity.subscription.autoresponder.AUTO_EMAIL_TYPE.WELCOME;
import segmail.entity.subscription.autoresponder.Assign_AutoresponderEmail_List;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;
import segmail.entity.subscription.email.mailmerge.MAILMERGE_REQUEST;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormWizardAuto")
public class FormWizardAuto {
    @Inject ProgramSetupWizard program;
    @Inject FormWizardInit formWizard;
    
    @EJB GenericObjectService objService;
    @EJB UpdateObjectService updService;
    @EJB ListService listService;
    @EJB MailMergeService mmService;
    
    public MAILMERGE_REQUEST[] getMailmergeLinkTags() {
        return program.getMailmergeLinkTags();
    }
    
    public List<SubscriptionListField> getListFields() {
        return program.getListFields();
    }

    public void setListFields(List<SubscriptionListField> listFields) {
        program.setListFields(listFields);
    }

    public Map<String, String> getMailmergeLinks() {
        return program.getMailmergeLinks();
    }

    public void setMailmergeLinks(Map<String, String> mailmergeLinks) {
        program.setMailmergeLinks(mailmergeLinks);
    }

    public List<String> getListTags() {
        return program.getListTags();
    }

    public void setListTags(List<String> listTags) {
        program.setListTags(listTags);
    }

    public SubscriptionList getSelectedList() {
        return program.getSelectedList();
    }

    public void setSelectedList(SubscriptionList selectedList) {
        program.setSelectedList(selectedList);
    }
    
    public AutoresponderEmail getConfirmEmail() {
        return program.getConfirmEmail();
    }

    public void setConfirmEmail(AutoresponderEmail confirmEmail) {
        program.setConfirmEmail(confirmEmail);
    }
    
    public AutoresponderEmail getWelcomeEmail() {
        return program.getWelcomeEmail();
    }

    public void setWelcomeEmail(AutoresponderEmail welcomeEmail) {
        program.setWelcomeEmail(welcomeEmail);
    }
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()) {
            this.loadListTags();
            this.loadMMTags();
        }
        this.loadAutoresponders();
        this.loadListFields();
    }
    
    public void loadAutoresponders() {
        SubscriptionList list = getSelectedList();
        List<AutoresponderEmail> emails = objService.getAllSourceObjectsFromTarget(list.getOBJECTID(), Assign_AutoresponderEmail_List.class, AutoresponderEmail.class);
        
        emails.forEach(email -> {
            if(CONFIRMATION.name.equals(email.getTYPE())) {
                setConfirmEmail(email);
            } else if(WELCOME.name.equals(email.getTYPE())) {
                setWelcomeEmail(email);
            }
        });
        
    }
    
    public void loadListFields() {
        SubscriptionList list = getSelectedList();
        List<SubscriptionListField> listFields = listService.getFieldsForSubscriptionList(list.getOBJECTID());
        
        setListFields(listFields);
    }
    
    public void loadMMTags() {
        try {
            setMailmergeLinks(new HashMap<String, String>());
            for (MAILMERGE_REQUEST request : this.getMailmergeLinkTags()) {
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
        
        setListTags(listTags);
    }
    
    public void saveAutoresponders() {
        AutoresponderEmail confirm = getConfirmEmail();
        AutoresponderEmail welcome = getWelcomeEmail();
        
        confirm = (AutoresponderEmail) updService.merge(confirm);
        welcome = (AutoresponderEmail) updService.merge(welcome);
        
        setConfirmEmail(confirm);
        setWelcomeEmail(welcome);
        
        formWizard.nextPage();
    }
}
