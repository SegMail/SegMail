/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign;

import eds.entity.data.EnterpriseData_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(CampaignActivityContent.class)
public class CampaignActivityContent_ extends EnterpriseData_ {
    public static volatile SingularAttribute<CampaignActivityContent,String> SUBJECT;
    public static volatile SingularAttribute<CampaignActivityContent,String> CONTENT;
}
