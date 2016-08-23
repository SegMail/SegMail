/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.subscribe;

import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.jboss.logging.Logger;
import segmail.program.subscribe.subscribe.client.RestClientSubscribe;

/**
 *
 * @author LeeKiatHaw
 */

@RequestScoped
@Named("FormSubscribe")
public class FormSubscribe {
    
    @Inject RestClientSubscribe wsClient;
    
    @Inject ProgramSubscribe program;
    
    private String invokeString = "";
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()) {
            program.init();
            callWS();
        }
        
    }

    public String getInvokeString() {
        return invokeString;
    }

    public void setInvokeString(String invokeString) {
        this.invokeString = invokeString;
    }
    
    public void callWS() {
        try {
            String confirmationKey = wsClient.subscribe(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap());
            //setListname(sub.getTARGET().getLIST_NAME());
            this.setPageName(program.getPAGE_ALREADY_SUBSCRIBED_AND_RESEND());
            this.setConfirmationKey(confirmationKey);
        } catch (Throwable ex) {
            this.setPageName(program.getPAGE_GENERIC_ERROR());
            setErrorMessage(ex.getMessage());
            java.util.logging.Logger.getLogger(this.getClass().getName()).log(Level.INFO, ex.getMessage());
        }
    }
    
    public boolean isError() {
        return program.isError();
    }

    public void setError(boolean error) {
        program.setError(error);
    }
    
    public String getListname() {
        return program.getListname();
    }

    public void setListname(String listname) {
        program.setListname(listname);
    }
    
    public String getErrorMessage() {
        return program.getErrorMessage();
    }

    public void setErrorMessage(String errorMessage) {
        program.setErrorMessage(errorMessage);
    }
    
    public String getPageName() {
        return program.getPageName();
    }

    public void setPageName(String pageName) {
        program.setPageName(pageName);
    }
    
    public String getConfirmationKey() {
        return program.getConfirmationKey();
    }

    public void setConfirmationKey(String confirmationKey) {
        program.setConfirmationKey(confirmationKey);
    }
    
    public void resendConfirmation() {
        try {
            wsClient.retriggerConfirmation(program.getConfirmationKey());
            this.setPageName(program.getPAGE_ALREADY_SUBSCRIBED_AND_RESEND());
        } catch (Exception ex) {
            this.setPageName(program.getPAGE_GENERIC_ERROR());
            
            Logger.getLogger(this.getClass().getName()).log(Logger.Level.ERROR, ex.getMessage());
        }
    }
}
