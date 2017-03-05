/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign.link;

import segmail.entity.campaign.link.CampaignLinkClick;
import eds.entity.transaction.EnterpriseTransaction_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(CampaignLinkClick.class)
public class CampaignLinkClick_ extends EnterpriseTransaction_ {
    
    public static volatile SingularAttribute<CampaignLinkClick,String> LINK_KEY;
    public static volatile SingularAttribute<CampaignLinkClick,String> SOURCE_KEY;
}
