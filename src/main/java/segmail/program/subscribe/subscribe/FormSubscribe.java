/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.subscribe;

import eds.component.data.IncompleteDataException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
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
    
    private String listname = "";
    
    @PostConstruct
    public void init() {
        program.init();
        callWS();
    }

    public String getInvokeString() {
        return invokeString;
    }

    public void setInvokeString(String invokeString) {
        this.invokeString = invokeString;
    }
    
    public void callWS() {
        listname = wsClient.subscribe(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap());
        
    }
}
