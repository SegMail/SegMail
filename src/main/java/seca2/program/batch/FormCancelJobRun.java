/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.batch;

import eds.component.batch.BatchProcessingException;
import eds.component.batch.BatchSchedulingService;
import eds.entity.batch.BatchJob;
import eds.entity.batch.BatchJobRun;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import org.joda.time.DateTime;
import seca2.jsf.custom.messenger.FacesMessenger;
import seca2.program.FormEdit;

/**
 * FormDelete
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormCancelJobRun")
public class FormCancelJobRun implements FormEdit{
    
    @Inject ProgramBatch program;
    
    private String key;
    
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
        program.refresh();
    }

    @Override
    public void delete() {
        try {
            scheduleService.cancelBatchJobRun(getEditingBatchJobRun().getRUN_KEY());
            DateTime current = program.getCurrentRunDateTime();
            scheduleService.triggerNextBatchJobRun(current,program.getFirstAndOnlyTrigger());
            FacesMessenger.setFacesMessage(program.getClass().getSimpleName(), FacesMessage.SEVERITY_FATAL, "Batch job run has been cancelled.", "");
            closeWithoutSaving();
        } catch (EJBException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        } catch (BatchProcessingException ex) {
            FacesMessenger.setFacesMessage(this.getClass().getSimpleName(), FacesMessage.SEVERITY_ERROR, ex.getMessage(), "");
        }
        
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
    
    public String getNextRunTime() {
        if(getEditingBatchJobRun() == null)
            return "";
        DateTime current = program.getCurrentRunDateTime();
        
        String cronExp = program.getFirstAndOnlyTrigger().getCRON_EXPRESSION();
        DateTime next = scheduleService.getNextExecutionTimeCron(cronExp, current, scheduleService.STANDARD_CRON_TYPE);
        
        return next.toString(program.getSCHEDULE_JAVA_DATE_STRING_FORMAT()+" "+program.getSCHEDULE_JAVA_TIME_STRING_FORMAT());
    }
    
    public void loadBatchJobRun(String runKey){
        program.loadBatchJobRun(runKey);
    }
}
