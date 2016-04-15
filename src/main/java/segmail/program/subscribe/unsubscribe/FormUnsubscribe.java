/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.unsubscribe;

import eds.component.data.IncompleteDataException;
import eds.component.webservice.ExpiredTransactionException;
import eds.component.webservice.TransactionProcessedException;
import eds.component.webservice.UnwantedAccessException;
import eds.component.webservice.WebserviceService;
import java.net.MalformedURLException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserRequestContainer;
import segmail.program.subscribe.confirm.ProgramConfirmSubscription;
import segmail.program.subscribe.confirm.client.WSConfirmSubscriptionInterface;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormUnsubscribe")
public class FormUnsubscribe {
    @Inject UserRequestContainer reqContainer;
    
    @EJB
    private WebserviceService wsService;

    @Inject
    private ProgramUnsubscribe program;

    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if(!fc.isPostback()){
            extractParams(reqContainer);
            unsubscribe();
        }
        
    }
    
    public void unsubscribe(){
        
    }

    public void extractParams(UserRequestContainer reqContainer) {
        List<String> params = reqContainer.getProgramParamsOrdered();
        if(params == null || params.isEmpty())
            return;
        
        String reqKey = params.get(0);
        program.setKey(reqKey);
    }
    
    public String getKey() {
        return program.getKey();
    }

    public void setKey(String key) {
        program.setKey(key);
    }
}
