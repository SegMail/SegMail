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
public enum BATCH_JOB_STATUS {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");
    
    public final String label;
    
    private BATCH_JOB_STATUS(String label){
        this.label = label;
    }
}
