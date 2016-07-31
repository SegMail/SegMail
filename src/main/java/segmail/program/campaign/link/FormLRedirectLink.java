/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign.link;

import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.webservice.WebserviceService;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import segmail.program.campaign.webservice.client.WSCampaignActivityLinkInterface;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormLRedirectLink")
public class FormLRedirectLink {
    
    @Inject UserRequestContainer reqContainer;
    
    @Inject ProgramLink program;
    
    @EJB
    private WebserviceService wsService;
    
    private String reqKey;
    
    private boolean error;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
    
    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if(!fc.isPostback()){
            extractParams(reqContainer);
            callRedirectLinkWS();
        }
    }
    
    public void extractParams(UserRequestContainer reqContainer) {
        List<String> params = reqContainer.getProgramParamsOrdered();
        reqKey = (params != null && !params.isEmpty()) ? params.get(0) : "";    
    }

    private void callRedirectLinkWS() {
        try {
            String namespace = "http://webservice.campaign.program.segmail/";
            String endpointName = "WSCampaignActivityLink";
            WSCampaignActivityLinkInterface clientService = wsService.getWSProvider(endpointName, namespace, WSCampaignActivityLinkInterface.class);
            
            String target = clientService.redirectAndUpdate(reqKey);
            
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            //Keep all messages posted in this request
            ec.getFlash().setKeepMessages(true);
            ec.redirect(target);
            
        } catch (EntityNotFoundException ex) {
            error = true;
            Logger.getLogger(FormLRedirectLink.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IncompleteDataException ex) {
            error = true;
            Logger.getLogger(FormLRedirectLink.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            error = true;
            Logger.getLogger(FormLRedirectLink.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            error = true;
            Logger.getLogger(FormLRedirectLink.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
}
