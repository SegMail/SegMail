/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 *
 * @author LeeKiatHaw
 */
public class CampaignActivityOutboundLinkListener {
    
    
    @PrePersist
    @PreUpdate
    public void prepersist(CampaignActivityOutboundLink link) {
        checkAndGenerate(link);
    }
    
    public void checkAndGenerate(CampaignActivityOutboundLink link) {
        if(link.getLINK_KEY() != null && !link.getLINK_KEY().isEmpty())
            return;
        //So that no collision!
        link.setLINK_KEY((String) link.generateKey());
    }
}
