/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.batch;

import eds.component.batch.BatchProcessingException;
import eds.component.batch.BatchSchedulingService;
import eds.component.data.EntityNotFoundException;
import eds.entity.batch.BatchJob;
import eds.entity.batch.BatchJobRun;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import seca2.entity.landing.ServerInstance;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.FormEdit;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormEditJobRun")
public class FormEditJobRun implements FormEdit{
    
    @Inject ProgramBatch program;
    
    @EJB BatchSchedulingService batchScheduleService;

    public void loadBatchJobRun(String runKey){
        program.loadBatchJobRun(runKey);
    }
    
    @Override
    public void saveAndContinue() {
        try {
            batchScheduleService.updateBatchJobRun(program.getEditingBatchJobRun());
            assignServerIdToRun();
            
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Batch Job Run updated", "");
        } catch (EJBException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (EntityNotFoundException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (BatchProcessingException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }

    @Override
    public void saveAndClose() {
        saveAndContinue();
        closeWithoutSaving();
    }

    @Override
    public void closeWithoutSaving() {
        program.clearVariables(); //Important!
        program.refresh();
    }

    /**
     * This is cancelling or stopping a batch job run.
     * Not used anymore. Batch Job runs should not be deleted.
     */
    @Override
    public void delete() {
        try {
            batchScheduleService.deleteBatchJobRun(program.getEditingBatchJobRun().getRUN_KEY());
        } catch (EJBException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
    }
    
    public List<ServerInstance> getServers() {
        return program.getServers();
    }

    public void setServers(List<ServerInstance> servers) {
        program.setServers(servers);
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
    
    public boolean isEditable() {
        return program.isEditable();
    }

    public void setEditable(boolean editable) {
        program.setEditable(editable);
    }
    
    public String getScheduledTime() {
        if(getEditingBatchJobRun() == null)
            return "";
        String timeString = program.timestampToString(getEditingBatchJobRun().getSCHEDULED_TIME());
        return timeString;
    }
    
    public void setScheduledTime(String timeString) {
        if(getEditingBatchJobRun() == null)
            return;
        Timestamp ts = program.stringToTimestamp(timeString);
        this.getEditingBatchJobRun().setSCHEDULED_TIME(ts);
        
    }
    
    public long getSelectedServerId() {
        return program.getSelectedServerId();
    }

    public void setSelectedServerId(long selectedServerId) {
        program.setSelectedServerId(selectedServerId);
    }
    
    public void assignServerIdToRun() throws EntityNotFoundException, BatchProcessingException {
        BatchJobRun run = this.getEditingBatchJobRun();
        long serverId = this.getSelectedServerId();
        batchScheduleService.assignServerToBatchJobRun(run.getRUN_KEY(), serverId);
    }
}
