/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.campaign;

import eds.component.data.DataValidationException;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 *
 * @author LeeKiatHaw
 */
public class CampaignActivityScheduleListener {
    
    @PrePersist
    @PreUpdate
    public void PrePersistUpdate(CampaignActivitySchedule object) throws DataValidationException{
        validateSendInBatch(object);
    }
    
    public void validateSendInBatch(CampaignActivitySchedule object) throws DataValidationException {
        if(object.getSEND_IN_BATCH() <= 0) {
            object.setSEND_IN_BATCH(CampaignActivitySchedule.MAX_SEND_IN_BATCH);
        }
        
        if(object.getSEND_IN_BATCH() > CampaignActivitySchedule.MAX_SEND_IN_BATCH) {
            throw new DataValidationException("You can only send in batches of not more than "+CampaignActivitySchedule.MAX_SEND_IN_BATCH);
        }
            
    }
}
