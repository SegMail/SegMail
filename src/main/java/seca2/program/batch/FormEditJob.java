/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.batch;

import eds.component.batch.BatchSchedulingService;
import eds.entity.batch.BatchJob;
import eds.entity.batch.BatchJobRun;
import eds.entity.batch.BatchJobStep;
import eds.entity.batch.BatchJobTrigger;
import java.util.List;
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
@Named("FormEditJob")
public class FormEditJob implements FormEdit{
    
    @Inject ProgramBatch program;
    
    @EJB BatchSchedulingService batchScheduleService;
    
    /**
     * 
     * @param runKey 
     */
    public void loadBatchJobRun(String runKey){
        program.loadBatchJobRun(runKey);
    }

    @Override
    public void saveAndContinue() {
        try {
            batchScheduleService.updateBatchJobRun(getEditingBatchJobRun());
            batchScheduleService.updateBatchJobTrigger(getFirstAndOnlyTrigger());
            batchScheduleService.updateBatchJobStep(getFirstAndOnlyStep());
            //batchScheduleService.updateBatchJob(getEditingBatchJob()); //All the above methods are cascaded to their owners
        } catch (EJBException ex) {
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
