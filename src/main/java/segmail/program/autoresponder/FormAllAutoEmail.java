/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.autoresponder;

import eds.component.GenericObjectService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import segmail.component.subscription.autoresponder.AutoresponderService;
import segmail.entity.subscription.SubscriptionList;
import segmail.entity.subscription.autoresponder.AUTO_EMAIL_TYPE;
import segmail.entity.subscription.autoresponder.Assign_AutoresponderEmail_List;
import segmail.entity.subscription.autoresponder.AutoresponderEmail;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormAllAutoEmail")
public class FormAllAutoEmail {
    
    @Inject ProgramAutoresponder program;
    
    @EJB AutoresponderService autoRespService;
    @EJB GenericObjectService objService;
    
    @Inject ClientContainer clientContainer;
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()) {
            loadAllEmails();
            loadAssignedLists();
        }
    }
    
    public void loadAllEmails() {
        List<AutoresponderEmail> emails = autoRespService.getAvailableAutoEmailsForClient(clientContainer.getClient().getOBJECTID(), null);
        
        program.setConfirmationTemplates(new ArrayList<AutoresponderEmail>());
        program.setWelcomeTemplates(new ArrayList<AutoresponderEmail>());
        
        for(AutoresponderEmail email : emails) {
            AUTO_EMAIL_TYPE type = AUTO_EMAIL_TYPE.valueOf(email.getTYPE());
            switch(type) {
                case CONFIRMATION : program.getConfirmationTemplates().add(email);
                                    break;
                case WELCOME    : program.getWelcomeTemplates().add(email);
                                    break;
                default         : break;
            }
        }
    }
    
    public void loadAssignedLists() {
        
        this.setAssignedToLists(new HashMap<Long,SubscriptionList>());
        
        List<AutoresponderEmail> allEmails = new ArrayList<>();
        allEmails.addAll(getConfirmationTemplates());
        allEmails.addAll(getWelcomeTemplates());
        
        List<Long> ids = new ArrayList<>();
        for(AutoresponderEmail email : allEmails) {
            ids.add(email.getOBJECTID());
        }
        List<Assign_AutoresponderEmail_List> assignments = objService.getRelationshipsForSourceObjects(ids, Assign_AutoresponderEmail_List.class);
        for(Assign_AutoresponderEmail_List assignment : assignments) {
            getAssignedToLists().put(assignment.getSOURCE().getOBJECTID(), assignment.getTARGET());
        }
    }
    
    public List<AutoresponderEmail> getConfirmationTemplates() {
        return program.getConfirmationTemplates();
    }

    public void setConfirmationTemplates(List<AutoresponderEmail> confirmationTemplates) {
        program.setConfirmationTemplates(confirmationTemplates);
    }
    
    public List<AutoresponderEmail> getWelcomeTemplates() {
        return program.getWelcomeTemplates();
    }

    public void setWelcomeTemplates(List<AutoresponderEmail> welcomeTemplates) {
        program.setWelcomeTemplates(welcomeTemplates);
    }
    
    public Map<Long, SubscriptionList> getAssignedToLists() {
        return program.getAssignedToLists();
    }

    public void setAssignedToLists(Map<Long, SubscriptionList> assignedToLists) {
        program.setAssignedToLists(assignedToLists);
    }
}
