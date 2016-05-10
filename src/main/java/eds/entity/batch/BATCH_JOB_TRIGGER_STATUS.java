/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

/**
 *
 * @author LeeKiatHaw
 */
public enum BATCH_JOB_TRIGGER_STATUS {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");
    
    public final String label;
    
    private BATCH_JOB_TRIGGER_STATUS(String label){
        this.label = label;
    }
    
    public BATCH_JOB_TRIGGER_STATUS getBATCH_JOB_STATUS(String label){
        for(BATCH_JOB_TRIGGER_STATUS s : BATCH_JOB_TRIGGER_STATUS.values()){
            if(s.label.equals(label))
                return s;
        }
        return BATCH_JOB_TRIGGER_STATUS.values()[0];
    }
}
