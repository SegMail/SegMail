/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import eds.component.GenericObjectService;
import eds.component.data.DataValidationException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.ListService;
import segmail.component.subscription.autoresponder.AutoresponderService;
import segmail.entity.subscription.SubscriptionList;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormListEmail")
@RequestScoped
public class FormListEmail {

    @Inject
    private ProgramList program;
    @Inject
    private ClientContainer clientContainer;

    @EJB
    private AutoresponderService autoresponderService;
    @EJB
    private GenericObjectService objectService;
    @EJB
    private ListService listService;

    private final String formName = "form_list_email";
    
    private String redirectConfirmExample;

    /**
     * We can control the number of DB hits for each form in this method. Each
     * load[something] method will contain optimally 1 DB hit and we can monitor
     * and control the number of times by controlling the load calls here.
     */
    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (!fc.isPostback()) {
            //reset();
            initParams();
        }
    }
    
    public void initParams() {
        List<String> confirmUrlParams = getListEditing().generateConfirmParamList();
        setConfirmUrlParams(confirmUrlParams);
        List<String> welcomeUrlParams = getListEditing().generateWelcomeParamList();
        setWelcomeUrlParams(welcomeUrlParams);
        List<String> unsubscribeUrlParams = getListEditing().generateUnsubscribeParamList();
        setUnsubUrlParams(unsubscribeUrlParams);
    }

    public void assignRedirects() {
        try {
            // Assign the params first
            getListEditing().setREDIRECT_CONFIRM_PARAMS(getConfirmUrlParams());
            getListEditing().setREDIRECT_WELCOME_PARAMS(getWelcomeUrlParams());
            getListEditing().setREDIRECT_UNSUBSCRIBE_PARAMS(getUnsubUrlParams());
            
            // Save the redirect link
            SubscriptionList listEditing = listService.saveList(getListEditing());
            setListEditing(listEditing);
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Redirect links are assigned.", "");
        } catch (DataValidationException ex) {
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }
    
    public void saveEmailSettings() {
        assignRedirects();
        //reset();
    }
    
    public ProgramList getProgram() {
        return program;
    }

    public void setProgram(ProgramList program) {
        this.program = program;
    }

    public String getPreview() {
        return (program.getSelectedConfirmationEmail() == null)
                ? null : program.getSelectedConfirmationEmail().getBODY();
    }

    public void resetConfirmationEmailPanel() {
        program.setSelectedConfirmationEmail(null);
    }

    public void reset() {
    }

    public SubscriptionList getListEditing() {
        return program.getListEditing();
    }

    public void setListEditing(SubscriptionList listEditing) {
        program.setListEditing(listEditing);
    }
    
    public List<String> getConfirmUrlParams() {
        return program.getConfirmUrlParams();
    }

    public void setConfirmUrlParams(List<String> subscribeUrlParams) {
        program.setConfirmUrlParams(subscribeUrlParams);
    }
    
    public String getConfirmUrlMasked() throws URISyntaxException {
        return getListEditing().generateConfirmUrl();
    }

    public List<String> getWelcomeUrlParams() {
        return program.getWelcomeUrlParams();
    }

    public void setWelcomeUrlParams(List<String> confirmUrlParams) {
        program.setWelcomeUrlParams(confirmUrlParams);
    }

    public List<String> getUnsubUrlParams() {
        return program.getUnsubUrlParams();
    }

    public void setUnsubUrlParams(List<String> unsubUrlParams) {
        program.setUnsubUrlParams(unsubUrlParams);
    }

    public String getRedirectConfirmExample() {
        return redirectConfirmExample;
    }

    public void setRedirectConfirmExample(String redirectConfirmExample) {
        this.redirectConfirmExample = redirectConfirmExample;
    }
}
