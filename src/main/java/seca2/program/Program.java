/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import seca2.bootstrap.UserRequestContainer;
import seca2.bootstrap.UserSessionContainer;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author LeeKiatHaw
 */
@SessionScoped
public abstract class Program implements Serializable {
    
    protected final String REFRESH_FLAG = "REFRESH_FLAG";
    
    @Inject
    protected UserRequestContainer reqContainer;
    @Inject
    protected UserSessionContainer sessContainer;
    
    @PostConstruct
    public void init() {
        clearVariables();
        initRequestParams();
        initProgram();
    }
    
    /**
     * 
     */
    public abstract void clearVariables();
    
    public abstract void initRequestParams();
    
    public abstract void initProgram();
    
    
    /**
     * 
     */
    public void refresh(){
        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            //Keep all messages posted in this request
            ec.getFlash().setKeepMessages(true);
            // Set a flash scope flag to indicate this was a refresh
            ec.getFlash().put(REFRESH_FLAG, true);
            ec.redirect(ec.getRequestContextPath()+reqContainer.printFullPathWithoutContext());
        } catch (Exception ex){
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR,  ex.getMessage(), null);
        }
    }
    
    public boolean isLastReqRefresh() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        if(ec.getFlash().get(REFRESH_FLAG) != null)
            return (boolean) ec.getFlash().get(REFRESH_FLAG);
        
        return false;
    }
}
