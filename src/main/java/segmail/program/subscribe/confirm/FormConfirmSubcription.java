/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.confirm;

import eds.component.data.IncompleteDataException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import eds.component.webservice.WebserviceService;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import segmail.program.subscribe.confirm.client.UnwantedAccessException_Exception;
import segmail.program.subscribe.confirm.client.WSConfirmSubscriptionInterface;
import segmail.program.subscribe.confirm.webservice.TransactionProcessedException;
import segmail.program.subscribe.confirm.webservice.UnwantedAccessException;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormConfirmSubcription")
public class FormConfirmSubcription {
    
    @Inject UserRequestContainer reqContainer;
    
    @EJB
    private WebserviceService wsService;

    @Inject
    private ProgramConfirmSubscription program;

    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if(!fc.isPostback()){
            extractParams(reqContainer);
            callWS();
        }
        
    }

    public void callWS() {

        try {
            String namespace = "http://webservice.confirm.subscribe.program.segmail/";
            String endpointName = "WSConfirmSubscription";
            WSConfirmSubscriptionInterface clientService = wsService.getWSProvider(endpointName, namespace, WSConfirmSubscriptionInterface.class);
            String key = program.getRequestKey();
            String results = clientService.confirm(key);
            
            this.setListName(results);
            
        } catch (UnwantedAccessException ex) {
            //show users a Landing Page to sign up for our
            //services.
            ex.printStackTrace(System.out);
        } catch (TransactionProcessedException ex) {
            //Tell users that their subscription has already
            //been processed and they should be receiving their welcome email soon
            ex.printStackTrace(System.out);
        } catch (IncompleteDataException ex) {
            ex.printStackTrace(System.out);
        } catch (MalformedURLException ex) {
            ex.printStackTrace(System.out);
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
    }

    public String getListName() {
        return program.getListName();
    }

    public void setListName(String result) {
        program.setListName(result);
    }

    public void extractParams(UserRequestContainer reqContainer) {
        List<String> params = reqContainer.getProgramParamsOrdered();
        if(params == null || params.isEmpty())
            return;
        
        String reqKey = params.get(0);
        program.setRequestKey(reqKey);
    }
}
