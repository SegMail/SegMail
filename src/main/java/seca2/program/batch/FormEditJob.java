/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.batch;

import eds.component.batch.BatchSchedulingService;
import eds.entity.batch.BATCH_JOB_STATUS;
import eds.entity.batch.BatchJob;
import eds.entity.batch.BatchJobStep;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.entity.landing.ServerInstance;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormEditJob")
public class FormEditJob {
    @Inject ProgramBatch program;
    
    @EJB BatchSchedulingService batchScheduleService;
    
    //These cannot be here, because a new instance will be created between requests
    //and all the persisted data is gon.
    /*private BatchJob editingBatchJob;
    private BatchJobStep firstAndOnlyStep;*/
    private boolean editable;

    public void loadBatchJob(long batchJobId){
        setEditingBatchJob(batchScheduleService.getBatchJobById(batchJobId)); //Set the main batch job
        if(this.getEditingBatchJob() != null && this.getEditingBatchJob().getSTEPS().size() > 0) //Fill in batch job step info if available
            this.setFirstAndOnlyStep(getEditingBatchJob().getSTEPS().get(0));
        
        program.loadServers(); //reload servers
        updateEditable(); //Set the editable flag based on the batch job's status.
    }
    
    public void updateBatchJob() {
        
    }

    public void setProgram(ProgramBatch program) {
        this.program = program;
    }

    public BatchJob getEditingBatchJob() {
        return program.getEditingBatchJob();
    }

    public void setEditingBatchJob(BatchJob editingBatchJob) {
        program.setEditingBatchJob(editingBatchJob);
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public BatchJobStep getFirstAndOnlyStep() {
        return program.getFirstAndOnlyStep();
    }

    public void setFirstAndOnlyStep(BatchJobStep firstAndOnlyStep) {
        program.setFirstAndOnlyStep(firstAndOnlyStep);
    }
    
    public List<ServerInstance> getServers() {
        return program.getServers();
    }
    
    public void setServers(List<ServerInstance> servers) {
        program.setServers(servers);
    }
    
    public void updateEditable() {
        switch(BATCH_JOB_STATUS.valueOf(getEditingBatchJob().getSTATUS())){
            case WAITING    :   setEditable(true);
            case SCHEDULED  :   setEditable(true);
            case IN_PROCESS :   setEditable(false);
            case COMPLETED  :   setEditable(false);
            case FAILED     :   setEditable(false);
        }
    }
}
