/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign.filter;

import eds.entity.data.EnterpriseData_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(CampaignActivityFilter.class)
public class CampaignActivityFilter_ extends EnterpriseData_{
    
    public static volatile SingularAttribute<CampaignActivityFilter,String> FIELD_DISPLAY;
    public static volatile SingularAttribute<CampaignActivityFilter,String> FIELD_KEY;
    public static volatile SingularAttribute<CampaignActivityFilter,String> OPERATOR;
    public static volatile SingularAttribute<CampaignActivityFilter,String> VALUE;

}
