/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.campaign;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.bootstrap.module.Client.ClientContainer;
import seca2.program.FormListEntity;
import segmail.component.campaign.CampaignService;
import segmail.entity.campaign.Campaign;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormCampaignListPanel")
public class FormCampaignListPanel implements FormListEntity {
    
    private final int MAX_GOAL_LENGTH = 30;

    @EJB CampaignService campaignService;
    
    @Inject ProgramCampaign program;
    @Inject ClientContainer clientCont;
    
    @PostConstruct
    public void init(){
        if(!FacesContext.getCurrentInstance().isPostback()) {
            loadList();
        }
    }
    
    @Override
    public void loadList() {
        List<Campaign> campaigns = campaignService.getAllCampaignForClient(clientCont.getClient().getOBJECTID());
        setAllCampaigns(campaigns);
    }

    @Override
    public void loadSelectedEntity(long entityId) {
        
    }

    @Override
    public void loadSelectedEntity(String entityKey) {
        
    }

    public List<Campaign> getAllCampaigns() {
        return program.getAllCampaigns();
    }

    public void setAllCampaigns(List<Campaign> allCampaigns) {
        program.setAllCampaigns(allCampaigns);
    }
    
    public String shortenCampaignGoals(String fullgoals) {
        return fullgoals.substring(0, Integer.min(fullgoals.length(), MAX_GOAL_LENGTH));
    }
}
