/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign;

import eds.entity.transaction.EnterpriseTransaction;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Stores all information that the CampaignSendingService needs. This is just
 * a container that facilitate asynchronous processing to to-be-executed campaign
 * activities.
 * 
 * @author LeeKiatHaw
 */
@Entity
@Table(name="CAMPAIGN_ACTIVITY_EXECUTION_SCHEDULE")
public class CampaignActivityExecutionSchedule extends EnterpriseTransaction {
    
    private long CAMPAIGN_ACTIVITY_ID;

    /**
     * Here we go - a one-to-many relationship stored as a delimited text. This 
     * should only be used for Transactional data, not master data.
     */
    private String TARGETED_LIST_ID;

    public long getCAMPAIGN_ACTIVITY_ID() {
        return CAMPAIGN_ACTIVITY_ID;
    }

    public void setCAMPAIGN_ACTIVITY_ID(long CAMPAIGN_ACTIVITY_ID) {
        this.CAMPAIGN_ACTIVITY_ID = CAMPAIGN_ACTIVITY_ID;
    }

    public String getTARGETED_LIST_ID() {
        return TARGETED_LIST_ID;
    }

    public void setTARGETED_LIST_ID(String TARGETED_LIST_ID) {
        this.TARGETED_LIST_ID = TARGETED_LIST_ID;
    }
    
    
}
