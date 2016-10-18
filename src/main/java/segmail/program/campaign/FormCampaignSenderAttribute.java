/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import eds.component.GenericObjectService;
import eds.component.client.ClientService;
import eds.component.data.IncompleteDataException;
import eds.entity.client.VerifiedSendingAddress;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.FormEditEntity;
import segmail.component.campaign.CampaignService;
import segmail.entity.campaign.Campaign;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormCampaignSenderAttribute")
public class FormCampaignSenderAttribute implements FormEditEntity {

    @Inject ProgramCampaign program;
    @Inject FormProgramModeSwitch formSwitch;
    
    @Inject ClientContainer clientCont;
    
    @EJB
    CampaignService campaignService;
    @EJB 
    GenericObjectService objService;
    
    @PostConstruct
    public void init() {
        if(!FacesContext.getCurrentInstance().isPostback()){
            loadVerifiedAddress();
        }
    }
    
    public Campaign getEditingCampaign() {
        return program.getEditingCampaign();
    }

    public void setEditingCampaign(Campaign editingCampaign) {
        program.setEditingCampaign(editingCampaign);
    }
    
    public List<VerifiedSendingAddress> getVerifiedAddresses() {
        return program.getVerifiedAddresses();
    }

    public void setVerifiedAddresses(List<VerifiedSendingAddress> verifiedAddresses) {
        program.setVerifiedAddresses(verifiedAddresses);
    }
    
    @Override
    public void saveAndContinue() {
        try {
            campaignService.updateCampaign(this.getEditingCampaign());
            reloadProgramToolbar();
            FacesMessenger.setFacesMessage(getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Campaign updated!", "");
        } catch (IncompleteDataException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }

    @Override
    public void saveAndClose() {
        
    }

    @Override
    public void closeWithoutSaving() {
        
    }

    @Override
    public void delete() {
        
    }
    
    public void reloadProgramToolbar() {
        formSwitch.reloadEntities();
        //formSwitch.initEditCampaignMode();
        formSwitch.modifySessionContainer();
    }
    
    public void loadVerifiedAddress() {
        List<VerifiedSendingAddress> addresses = objService.getEnterpriseData(clientCont.getClient().getOBJECTID(), VerifiedSendingAddress.class);
        this.setVerifiedAddresses(addresses);
    }
}
