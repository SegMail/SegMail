/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign;

import eds.entity.data.EnterpriseRelationship;
import javax.persistence.Entity;
import javax.persistence.Table;
import segmail.entity.subscription.SubscriptionList;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="ASSIGN_CAMPAIGNACTIVITY_LIST")
public class Assign_CampaignActivity_List extends EnterpriseRelationship<CampaignActivity,SubscriptionList> {

    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
