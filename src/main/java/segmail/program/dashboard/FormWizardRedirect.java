/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.dashboard;

import eds.component.user.UserService;
import eds.entity.user.UserAccount;
import eds.entity.user.UserSetting;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserSessionContainer;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.ListService;
import segmail.entity.subscription.SubscriptionList;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormWizardRedirect")
public class FormWizardRedirect {
    
    final String LOAD_WELCOME = "FormWizardRedirect.loadWelcome";
    
    @Inject ProgramDashboard program;
    @Inject UserSessionContainer userCont;
    @Inject ClientContainer clientCont;
    
    @EJB UserService userService;
    @EJB ListService listService;
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()) {
            loadWelcomeAndRedirect();
        }
    }
    
    public boolean isShowWelcome() {
        return program.isShowWelcome();
    }

    public void setShowWelcome(boolean showWelcome) {
        program.setShowWelcome(showWelcome);
    }

    public boolean isDontShowThisAgain() {
        return program.isDontShowThisAgain();
    }

    public void setDontShowThisAgain(boolean dontShowThisAgain) {
        program.setDontShowThisAgain(dontShowThisAgain);
    }
    
    public boolean isRedirectToWizard() {
        return program.isRedirectToWizard();
    }

    public void setRedirectToWizard(boolean redirectToWizard) {
        program.setRedirectToWizard(redirectToWizard);
    }
    /**
     * Determine if the welcome popup should be displayed
     */
    public void loadWelcomeAndRedirect() {
        // Check if the current user is logging in for the first time
        UserAccount accts = userService.getUserAccountById(userCont.getUser().getOBJECTID());
        UserSetting setting  = userService.getSetting(userCont.getUser().getOBJECTID(), LOAD_WELCOME);
        setShowWelcome(accts.isFIRST_LOGIN() && (setting == null || !"true".equals(setting.getVALUE())));
        setDontShowThisAgain(!isShowWelcome());
        
        List<SubscriptionList> lists = listService.getAllListForClient(clientCont.getClient().getOBJECTID());
        setRedirectToWizard(lists == null || lists.isEmpty());
        if(lists == null || lists.isEmpty()) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, 
                    "Hey there! Go to our <a href='"
                            + FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()
                            + "/wizard'>setup wizard</a> to get up and running!",
                    "");
        }
    }
    
    public void dontShowThisAgain() {
        if(isDontShowThisAgain()) {
            userService.updateSetting(userCont.getUser().getOBJECTID(), LOAD_WELCOME, "true");
        } else {
            userService.updateSetting(userCont.getUser().getOBJECTID(), LOAD_WELCOME, "false");
        }
    }
}
