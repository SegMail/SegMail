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
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="CAMPAIGN_EXECUTION_ERROR")
public class CampaignExecutionError extends EnterpriseTransaction {
    private long CAMPAIGN_ACTIVITY_ID;
    
    private String RECIPIENT;
    
    private String ERROR_MESSAGE;

    public long getCAMPAIGN_ACTIVITY_ID() {
        return CAMPAIGN_ACTIVITY_ID;
    }

    public void setCAMPAIGN_ACTIVITY_ID(long CAMPAIGN_ACTIVITY_ID) {
        this.CAMPAIGN_ACTIVITY_ID = CAMPAIGN_ACTIVITY_ID;
    }

    public String getRECIPIENT() {
        return RECIPIENT;
    }

    public void setRECIPIENT(String RECIPIENT) {
        this.RECIPIENT = RECIPIENT;
    }

    public String getERROR_MESSAGE() {
        return ERROR_MESSAGE;
    }

    public void setERROR_MESSAGE(String ERROR_MESSAGE) {
        this.ERROR_MESSAGE = ERROR_MESSAGE;
    }
    
    
}
