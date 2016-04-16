/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribe.unsubscribe;

import eds.component.data.IncompleteDataException;
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
import segmail.program.subscribe.unsubscribe.client.WSUnsubscribeInterface;

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
        try {
            String key = program.getRequestKey();
            if(key == null || key.isEmpty())
                throw new UnwantedAccessException();
            
            String namespace = "http://webservice.unsubscribe.subscribe.program.segmail/";
            String endpointName = "WSUnsubscribe";
            WSUnsubscribeInterface clientService = wsService.getWSProvider(endpointName, namespace, WSUnsubscribeInterface.class);
            
            String results = clientService.unsubscribe(key);
            
            this.setListName(results);
            program.setCurrentPage(program.getSUCCESS());
            
        } catch (UnwantedAccessException ex) {
            program.setCurrentPage(program.getLANDING());
        } catch (IncompleteDataException ex) {
            ex.printStackTrace(System.out);
            program.setCurrentPage(program.getERROR());
        } catch (MalformedURLException ex) {
            ex.printStackTrace(System.out);
            program.setCurrentPage(program.getERROR());
        }
    }

    public void extractParams(UserRequestContainer reqContainer) {
        List<String> params = reqContainer.getProgramParamsOrdered();
        if(params == null || params.isEmpty())
            return;
        
        String reqKey = params.get(0);
        program.setRequestKey(reqKey);
    }
    
    public String getRequestKey() {
        return program.getRequestKey();
    }

    public void setRequestKey(String requestKey) {
        program.setRequestKey(requestKey);
    }

    public String getCurrentPage() {
        return program.getCurrentPage();
    }

    public void setCurrentPage(String currentPage) {
        program.setCurrentPage(currentPage);
    }

    public String getListName() {
        return program.getListName();
    }

    public void setListName(String listName) {
        program.setListName(listName);
    }
}
