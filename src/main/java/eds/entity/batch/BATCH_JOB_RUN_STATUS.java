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
    /**
     * Initial creation.
     * 
     * Possible transition to:
     * - SCHEDULED
     * - CANCELLED
     */
    WAITING("WAITING", "BATCH_JOB_RUN", "BatchJobRun"),
    
    /**
     * Will run once scheduled time reaches.
     * 
     * Possible transition to:
     * - QUEUED
     * - CANCELLED
     */
    SCHEDULED("SCHEDULED", "BATCH_JOB_RUN_SCHEDULED", "BatchJobRunScheduled"),
    
    /**
     * Scheduled time has reached and this job is already sent to a executor 
     * to be executed.
     * 
     * Possible transition to:
     * - IN_PROCESS
     * - CANCELLED
     */
    QUEUED("QUEUED", "BATCH_JOB_RUN_QUEUED", "BatchJobRunQueued"),
    
    /**
     * Executing.
     * 
     * Possible transition to:
     * - COMPLETED
     * - FAILED
     * - CANCELLED
     */
    IN_PROCESS("IN_PROCESS", "BATCH_JOB_RUN_IN_PROCESS", "BatchJobRun"),
    
    /**
     * Completed execution.
     * 
     * Possible transition to:
     * - [none]
     */
    COMPLETED("COMPLETED", "BATCH_JOB_RUN_COMPLETED", "BatchJobRun"),
    
    /**
     * Encountered error in execution.
     * 
     * Possible transition to:
     * - [none]
     */
    FAILED("FAILED", "BATCH_JOB_RUN_FAILED", "BatchJobRunFailed"),
    
    /**
     * A cancellation is triggered before the job has been QUEUED, IN_PROCESS, 
     * COMPLETED, or FAILED. Note that if this is triggered during IN_PROCESS, this 
     * does not mean that the current execution will stop, it only means that 
     * there will not be any future executions after the current one.
     * 
     * Possible transition to:
     * - [none]
     */
    CANCELLED("CANCELLED", "BATCH_JOB_RUN_CANCELLED", "BatchJobRunCancelled");
    
    public final String label;
    
    public final String tableName;
    
    public final String className;
    
    private BATCH_JOB_RUN_STATUS(String label, String tableName, String className){
        this.label = label;
        this.tableName = tableName;
        this.className = className;
    }
    
    public static List<BATCH_JOB_RUN_STATUS> getReadyStatuses() {
        List<BATCH_JOB_RUN_STATUS> statuses = new ArrayList<>();
        statuses.add(WAITING);
        statuses.add(SCHEDULED);
        
        return statuses;
    }
    
    public static List<BATCH_JOB_RUN_STATUS> getActiveStatuses() {
        List<BATCH_JOB_RUN_STATUS> statuses = new ArrayList<>();
        statuses.add(QUEUED);
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
