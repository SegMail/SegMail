/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import javax.persistence.PrePersist;

/**
 *
 * @author LeeKiatHaw
 */
public class BatchJobTriggerListener {
    
    @PrePersist
    public void PrePersist(BatchJobTrigger job) {
        //this.labelStepNo(job);
    }
}
