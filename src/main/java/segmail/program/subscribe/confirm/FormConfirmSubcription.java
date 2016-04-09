/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.confirm;

import java.net.URL;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.namespace.QName;
import seca2.bootstrap.UserRequestContainer;
import seca2.component.landing.LandingService;
import eds.component.webservice.GenericWSProvider;
import eds.component.webservice.WebserviceService;
import segmail.program.subscribe.confirm.client.WSConfirmSubscriptionInterface;

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
            String results = clientService.confirm(program.getRequestKey());
            
            this.setListName(results);
            
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
