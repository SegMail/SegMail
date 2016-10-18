/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.mysettings;

import eds.component.GenericObjectService;
import eds.entity.client.VerifiedSendingAddress;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormVerifiedEmails")
public class FormVerifiedEmails {
    
    @Inject MySettingsProgram program;
    
    @Inject ClientContainer clientCont;
    
    @EJB GenericObjectService objService;
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()){
            loadAddresses();
        }
    }
    
    public List<VerifiedSendingAddress> getAddresses() {
        return program.getAddresses();
    }

    public void setAddresses(List<VerifiedSendingAddress> addresses) {
        program.setAddresses(addresses);
    }
    
    public void loadAddresses() {
        List<VerifiedSendingAddress> addresses = objService.getEnterpriseData(clientCont.getClient().getOBJECTID(), VerifiedSendingAddress.class);
        setAddresses(addresses);
    }
}
