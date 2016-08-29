/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.autoresponder;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import segmail.component.subscription.autoresponder.AutoresponderService;
import segmail.entity.subscription.autoresponder.AUTO_EMAIL_TYPE;
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
    
    @Inject ClientContainer clientContainer;
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()) {
            loadAllEmails();
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
}
