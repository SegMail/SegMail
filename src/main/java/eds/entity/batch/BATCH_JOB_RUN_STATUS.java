/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LeeKiatHaw
 */
public enum BATCH_JOB_RUN_STATUS {
    WAITING("WAITING"),
    SCHEDULED("SCHEDULED"),
    IN_PROCESS("IN_PROCESS"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED"),
    CANCELLED("CANCELLED");
    
    public final String label;
    
    private BATCH_JOB_RUN_STATUS(String label){
        this.label = label;
    }
    
    public BATCH_JOB_RUN_STATUS getBATCH_JOB_STATUS(String label){
        for(BATCH_JOB_RUN_STATUS s : BATCH_JOB_RUN_STATUS.values()){
            if(s.label.equals(label))
                return s;
        }
        return BATCH_JOB_RUN_STATUS.values()[0];
    }
    
    public static List<BATCH_JOB_RUN_STATUS> getReadyStatuses() {
        List<BATCH_JOB_RUN_STATUS> statuses = new ArrayList<>();
        statuses.add(WAITING);
        statuses.add(SCHEDULED);
        
        return statuses;
    }
    
    public static List<BATCH_JOB_RUN_STATUS> getActiveStatuses() {
        List<BATCH_JOB_RUN_STATUS> statuses = new ArrayList<>();
        statuses.add(IN_PROCESS);
        
        return statuses;
    }
    
    public static List<BATCH_JOB_RUN_STATUS> getEndStatuses() {
        List<BATCH_JOB_RUN_STATUS> statuses = new ArrayList<>();
        statuses.add(COMPLETED);
        statuses.add(FAILED);
        statuses.add(CANCELLED);
        
        return statuses;
    }
}
