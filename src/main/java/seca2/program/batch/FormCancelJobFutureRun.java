/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.batch;

import eds.component.batch.BatchSchedulingService;
import eds.entity.batch.BatchJob;
import eds.entity.batch.BatchJobRun;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.FormEdit;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormCancelJobFutureRun")
public class FormCancelJobFutureRun implements FormEdit {
    
    @Inject ProgramBatch program;
    
    @EJB BatchSchedulingService scheduleService;

    @Override
    public void saveAndContinue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        try {
            scheduleService.cancelBatchJobRun(getEditingBatchJobRun().getRUN_KEY());
            //DateTime current = program.getCurrentRunDateTime();
            ///scheduleService.triggerNextBatchJobRun(current,program.getFirstAndOnlyTrigger());
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "All batch job runs have been cancelled.", "");
            closeWithoutSaving();
        } catch (EJBException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } 
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
    
    public String getScheduledTime() {
        return program.getScheduledTime();
    }
    
    public void setScheduledTime(String timeString) {
        program.setScheduledTime(timeString);
        
    }
    
    public void loadBatchJobRun(String runKey){
        program.loadBatchJobRun(runKey);
    }
}
