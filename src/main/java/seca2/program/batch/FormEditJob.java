/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.batch;

import eds.entity.batch.BatchJob;
import eds.entity.batch.BatchJobRun;
import eds.entity.batch.BatchJobStep;
import eds.entity.batch.BatchJobTrigger;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.entity.landing.ServerInstance;
import seca2.program.FormEdit;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormEditJob")
public class FormEditJob implements FormEdit{
    
    @Inject ProgramBatch program;
    
    /**
     * 
     * @param batchJobId 
     */
    public void loadBatchJob(long batchJobId){
        //To load BatchJob, we will need to also load BatchJobRun as editable is dependent on it
        
    }

    @Override
    public void saveAndContinue() {
        
    }

    @Override
    public void saveAndClose() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void closeWithoutSaving() {
        program.clearVariables(); //Important!
        program.refresh();
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public BatchJobRun getEditingBatchJobRun() {
        return program.getEditingBatchJobRun();
    }

    public void setEditingBatchJobRun(BatchJobRun editingBatchJobRun) {
        program.setEditingBatchJobRun(editingBatchJobRun);
    }
    
    public BatchJob getEditingBatchJob() {
        return (program.getEditingBatchJobRun() == null) ? null : program.getEditingBatchJobRun().getBATCH_JOB();
    }

    public void setEditingBatchJob(BatchJob editingBatchJob) {
        program.getEditingBatchJobRun().setBATCH_JOB(editingBatchJob);
    }
    
    public BatchJobStep getFirstAndOnlyStep() {
        return program.getFirstAndOnlyStep();
    }

    public void setFirstAndOnlyStep(BatchJobStep firstAndOnlyStep) {
        program.setFirstAndOnlyStep(firstAndOnlyStep);
    }
    
    public BatchJobTrigger getFirstAndOnlyTrigger() {
        return program.getFirstAndOnlyTrigger();
    }

    public void setFirstAndOnlyTrigger(BatchJobTrigger firstAndOnlyTrigger) {
        program.setFirstAndOnlyTrigger(firstAndOnlyTrigger);
    }
    
    public boolean isEditable() {
        return program.isEditable();
    }

    public void setEditable(boolean editable) {
        program.setEditable(editable);
    }
    
    public List<ServerInstance> getServers() {
        return program.getServers();
    }

    public void setServers(List<ServerInstance> servers) {
        program.setServers(servers);
    }
    
}
