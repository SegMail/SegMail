/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.batch;

import eds.component.batch.BatchJobDripDataSource;
import eds.component.batch.BatchSchedulingService;
import eds.entity.batch.BatchJobRun;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormJobList")
public class FormJobList {
    
    private final int RECORDS_PER_PAGE = 20;
    private final int PAGE_RANGE = 7;
    
    @Inject ProgramBatch program;
    
    @EJB BatchSchedulingService batchScheduleService;
    
    
    @PostConstruct
    public void init(){
        if(!FacesContext.getCurrentInstance().isPostback()) {
            getJobRunDrip().init(RECORDS_PER_PAGE);
            setCurrentPage(1);
            loadBatchJobs(getCurrentPage());
            
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
    
    public void loadBatchJobsChangeTime() {
        if(program.getStartString() == null || program.getStartString().isEmpty() 
                || program.getEndString() == null || program.getEndString().isEmpty())
            return;
        
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd MMMM YYYY, HH:mm:ss");
        
        DateTime lowDateTime = formatter.parseDateTime(program.getStartString());
        Timestamp lowTS = new Timestamp(lowDateTime.getMillis());
        getJobRunDrip().setStart(lowTS);
        DateTime highDateTime = formatter.parseDateTime(program.getEndString());
        Timestamp highTS = new Timestamp(highDateTime.getMillis());
        getJobRunDrip().setEnd(highTS);
        
        getJobRunDrip().init(); //Here we want to re-initialize because the refresh button supposed to re-hit the DB
        loadBatchJobs(1); //reset the page number if there's a change in time range
        loadPageNumbers();
    }
    
    public void loadBatchJobsStatus() {
        //Get statuses
        List<String> statusString = new ArrayList<>();
        Map<String,Boolean> status = this.getStatuses();
        for(String key : getStatuses().keySet()) {
            if(getStatuses().get(key))
                statusString.add(key);
        }
        //If no statuses are selected then don't bother
        if(statusString.isEmpty()) {
            setBatchJobRuns(new ArrayList<BatchJobRun>());
            return;
        }
        
        if(!getJobRunDrip().compareStatuses(statusString)) {
            getJobRunDrip().setStatuses(statusString);
        }
        
        getJobRunDrip().init(); //Here we want to re-initialize because the criteria has been changed
        loadBatchJobs(1);
        loadPageNumbers();
    }
    
    public void loadBatchJobs(int page) {
        
        List<BatchJobRun> batchJobRuns = getJobRunDrip().drip(page);
        this.setBatchJobRuns(batchJobRuns);
        
        this.setCurrentPage(page);
    }
    
    public void loadPageNumbers() {
        //List<Integer> pageNumbers = getJobRunDrip().loadPageNumbers();
        //this.setPageNumbers(pageNumbers);
        int startPage = Math.max(1, getCurrentPage() - (PAGE_RANGE/2));
        int endPage = Math.min(getTotalPage(), startPage + PAGE_RANGE/2);
        
        setPageNumbers(new ArrayList<Integer>());
        for(int i = startPage; i<= endPage; i++){
            getPageNumbers().add(i);
        }
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
    
    public Map<String, Boolean> getStatuses() {
        return program.getStatuses();
    }

    public void setStatuses(Map<String, Boolean> statuses) {
        program.setStatuses(statuses);
    }
    
    public List<Integer> getPageNumbers() {
        return program.getPageNumbers();
    }

    public void setPageNumbers(List<Integer> pageNumbers) {
        program.setPageNumbers(pageNumbers);
    }

    public int getRECORDS_PER_PAGE() {
        return RECORDS_PER_PAGE;
    }
    
    public int getCurrentPage() {
        return program.getCurrentPage();
    }

    public void setCurrentPage(int currentPage) {
        program.setCurrentPage(currentPage);
    }
    
    public BatchJobDripDataSource getJobRunDrip() {
        return program.getJobRunDrip();
    }

    public void setJobRunDrip(BatchJobDripDataSource jobRunDrip) {
        program.setJobRunDrip(jobRunDrip);
    }
    
    public int getTotalPage() {
        return (int) ((getJobRunDrip().count() / RECORDS_PER_PAGE) + 1);
    }
}
