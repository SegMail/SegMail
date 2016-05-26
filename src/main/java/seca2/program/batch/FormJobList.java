/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.batch;

import eds.component.batch.BatchSchedulingService;
import eds.entity.batch.BatchJobRun;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormJobList")
public class FormJobList {
    @Inject ProgramBatch program;
    
    @EJB BatchSchedulingService scheduleService;
    
    @PostConstruct
    public void init(){
        if(!FacesContext.getCurrentInstance().isPostback()) {
            loadBatchJobs();
        }
    }
    
    public List<BatchJobRun> getBatchJobRuns() {
        return program.getBatchJobRuns();
    }

    public void setBatchJobRuns(List<BatchJobRun> batchJobRuns) {
        program.setBatchJobRuns(batchJobRuns);
    }
    
    public Map<String, String> getBatchJobStatusMapping() {
        return program.getBatchJobStatusMapping();
    }

    public void setBatchJobStatusMapping(Map<String, String> batchJobStatusMapping) {
        program.setBatchJobStatusMapping(batchJobStatusMapping);
    }
    
    public void loadBatchJobs(){
        program.loadBatchJobs();
    }
    
    public String getStartString() {
        return program.getStartString();
    }

    public void setStartString(String startString) {
        program.setStartString(startString);
    }

    public String getEndString() {
        return program.getEndString();
    }

    public void setEndString(String endString) {
        program.setEndString(endString);
    }
    
    public String getSCHEDULE_JAVA_DATE_STRING_FORMAT() {
        return program.getSCHEDULE_JAVA_DATE_STRING_FORMAT();
    }
    
    public String getSCHEDULE_JAVA_TIME_STRING_FORMAT() {
        return program.getSCHEDULE_JAVA_TIME_STRING_FORMAT();
    }
    
    public String getSCHEDULE_JS_DATE_STRING_FORMAT() {
        return program.getSCHEDULE_JS_DATE_STRING_FORMAT();
    }

    public String getSCHEDULE_JS_TIME_STRING_FORMAT() {
        return program.getSCHEDULE_JAVA_TIME_STRING_FORMAT();
    }
    
    public String getRunTime(Timestamp start) {
        DateTime now = DateTime.now();
        long seconds = (now.getMillis() - start.getTime())/1000;
        return Long.toString(seconds);
    }
}
