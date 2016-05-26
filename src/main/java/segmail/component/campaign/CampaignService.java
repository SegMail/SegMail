/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.campaign;

import eds.component.GenericObjectService;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import segmail.entity.campaign.Campaign;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class CampaignService {
    
    @EJB GenericObjectService objService;
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Campaign getCampaign(long campaignId) {
        Campaign campaign = objService.getEnterpriseObjectById(campaignId, Campaign.class);
        return campaign;
    }
}
