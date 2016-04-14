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
import segmail.program.subscribe.confirm.client.WSConfirmSubscriptionInterface;
import eds.component.webservice.TransactionProcessedException;
import eds.component.webservice.UnwantedAccessException;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormSubcription")
public class FormSubcription {
    
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
            callConfirmWS();
        }
        
    }

    public void callConfirmWS() {

        try {
            String key = program.getRequestKey();
            if(key == null || key.isEmpty())
                throw new UnwantedAccessException();
            
            String namespace = "http://webservice.confirm.subscribe.program.segmail/";
            String endpointName = "WSConfirmSubscription";
            WSConfirmSubscriptionInterface clientService = wsService.getWSProvider(endpointName, namespace, WSConfirmSubscriptionInterface.class);
            
            String results = clientService.confirm(key);
            
            this.setListName(results);
            program.setCurrentPage(program.getSUCCESS());
            
        } catch (UnwantedAccessException ex) {
            //Can check if key is empty
            //show users a Landing Page to sign up for our
            //services.
            //ex.printStackTrace(System.out);
            program.setCurrentPage(program.getLANDING());
            //alternatively, we can forward them to a landing page.
        } catch (TransactionProcessedException ex) {
            //Tell users that their subscription has already
            //been processed and they should be receiving their welcome email soon
            //ex.printStackTrace(System.out);
            program.setCurrentPage(program.getPROCESSED());
            
        } catch (IncompleteDataException ex) {
            ex.printStackTrace(System.out);
            program.setCurrentPage(program.getERROR());
        } catch (MalformedURLException ex) {
            ex.printStackTrace(System.out);
            program.setCurrentPage(program.getERROR());
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            program.setCurrentPage(program.getERROR());
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
    
    public String getCurrentPage() {
        return program.getCurrentPage();
    }

    public void setCurrentPage(String currentPage) {
        program.setCurrentPage(currentPage);
    }
}
