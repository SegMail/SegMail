/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign;

import eds.entity.data.EnterpriseObject;
import eds.entity.data.EnterpriseObject_;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(CampaignActivity.class)
public class CampaignActivity_ extends EnterpriseObject_ {
    
    public static volatile SingularAttribute<CampaignActivity,String> ACTIVITY_NAME;
    public static volatile SingularAttribute<CampaignActivity,String> ACTIVITY_TYPE;
    public static volatile SingularAttribute<CampaignActivity,String> ACTIVITY_GOALS;
    public static volatile SingularAttribute<CampaignActivity,String> STATUS;
    
    public static volatile SingularAttribute<CampaignActivity,java.sql.Timestamp> SCHEDULED_TIME;
    public static volatile SingularAttribute<CampaignActivity,java.sql.Timestamp> START_TIME;
    public static volatile SingularAttribute<CampaignActivity,java.sql.Timestamp> END_TIME;
    public static volatile SingularAttribute<CampaignActivity,java.sql.Timestamp> CANCEL_TIME;
}
