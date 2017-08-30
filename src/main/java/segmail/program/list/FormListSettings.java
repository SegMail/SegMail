/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.list;

import eds.component.GenericObjectService;
import eds.component.data.DataValidationException;
import eds.component.user.UserService;
import eds.entity.client.VerifiedSendingAddress;
import java.util.List;
import segmail.component.subscription.SubscriptionService;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.UserSessionContainer;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import segmail.component.subscription.ListService;
import segmail.entity.subscription.SubscriptionList;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormListSettings")
@RequestScoped
public class FormListSettings {
    @Inject private ProgramList program;
    @Inject private UserSessionContainer userContainer;
    @Inject private ClientContainer clientCont;
    
    @EJB private GenericObjectService objService;
    @EJB private ListService listService;
    
    private final String formName = "FormListSettings";
    
    @PostConstruct
    public void init(){
        if(!FacesContext.getCurrentInstance().isPostback()){
            loadVerifiedAddresses();
        }
    }
    
    public List<VerifiedSendingAddress> getVerifiedAddresses() {
        return program.getVerifiedAddresses();
    }
    
    public String getSendingAddress() {
        return program.getSendingAddress();
    }

    public void setSendingAddress(String sendingAddress) {
        program.setSendingAddress(sendingAddress);
    }

    public void setVerifiedAddresses(List<VerifiedSendingAddress> verifiedAddresses) {
        program.setVerifiedAddresses(verifiedAddresses);
    }
    
    /**
     * Dirty trick to invoke the PostConstruct method of this RequestScoped class
     * @return 
     */
    public SubscriptionList getList(){
        return program.getListEditing();
    }
    
    public void saveSettings(){
        try {
            SubscriptionList listEditing = listService.saveList(program.getListEditing());
            program.setListEditing(listEditing);
            
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_FATAL, "Settings saved!", null);
        } catch (DataValidationException ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, "Error with transaction", ex.getMessage());
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(formName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void loadVerifiedAddresses() {
        List<VerifiedSendingAddress> verifiedAddresses = objService.getEnterpriseData(clientCont.getClient().getOBJECTID(), VerifiedSendingAddress.class);
        setVerifiedAddresses(verifiedAddresses);
    }
}
