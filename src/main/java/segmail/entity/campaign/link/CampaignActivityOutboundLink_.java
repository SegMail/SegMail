/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign.link;

import eds.entity.data.EnterpriseData;
import eds.entity.data.EnterpriseData_;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(CampaignActivityOutboundLink.class)
public class CampaignActivityOutboundLink_ extends EnterpriseData_{

    public static volatile SingularAttribute<CampaignActivityOutboundLink,String> LINK_KEY;
    
    public static volatile SingularAttribute<CampaignActivityOutboundLink,String> LINK_TARGET;
    
    public static volatile SingularAttribute<CampaignActivityOutboundLink,String> LINK_TEXT;

    
}
