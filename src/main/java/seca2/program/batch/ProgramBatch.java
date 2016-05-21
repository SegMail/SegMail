/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.batch;

import eds.component.batch.BatchProcessingService;
import eds.component.batch.BatchSchedulingService;
import eds.entity.batch.BATCH_JOB_RUN_STATUS;
import eds.entity.batch.BatchJob;
import eds.entity.batch.BatchJobRun;
import eds.entity.batch.BatchJobStep;
import eds.entity.batch.BatchJobTrigger;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import seca2.component.landing.LandingService;
import static seca2.component.landing.ServerNodeType.ERP;
import seca2.entity.landing.ServerInstance;
import seca2.program.Program;

/**
 *
 * @author LeeKiatHaw
 */
public class ProgramBatch extends Program {
    
    private List<BatchJobRun> batchJobRuns;
    private List<ServerInstance> servers;
    private Map<String,String> batchJobStatusMapping;
    private String startString;
    private String endString;
    
    //private BatchJob editingBatchJob;
    private boolean editable;
    private BatchJobRun editingBatchJobRun;
    private BatchJobStep firstAndOnlyStep; //Must be initialized in loadBatchJob()
    private BatchJobTrigger firstAndOnlyTrigger; //Must be initialized in loadBatchJob()
    private final String SCHEDULE_JAVA_DATE_STRING_FORMAT = "yyyy-MM-dd";
    private final String SCHEDULE_JAVA_TIME_STRING_FORMAT = "HH:mm";
    private final String SCHEDULE_JS_DATE_STRING_FORMAT = "yy-mm-dd";
    private final String SCHEDULE_JS_TIME_STRING_FORMAT = "HH:mm";
    

    @EJB LandingService landingService;
    @EJB BatchSchedulingService batchScheduleService;
    @EJB BatchProcessingService batchProcessingService;

    @Override
    public void clearVariables() {
        setEditingBatchJobRun(null);
        setFirstAndOnlyStep(null);
        setFirstAndOnlyTrigger(null);
    }

    @Override
    public void initProgramParams() {
        
    }

    @Override
    public void initProgram() {
        batchJobStatusMapping = new HashMap<String,String>();
        batchJobStatusMapping.put(BATCH_JOB_RUN_STATUS.WAITING.label, "default");
        batchJobStatusMapping.put(BATCH_JOB_RUN_STATUS.SCHEDULED.label, "primary");
        batchJobStatusMapping.put(BATCH_JOB_RUN_STATUS.IN_PROCESS.label, "info");
        batchJobStatusMapping.put(BATCH_JOB_RUN_STATUS.COMPLETED.label, "success");
        batchJobStatusMapping.put(BATCH_JOB_RUN_STATUS.CANCELLED.label, "warning");
        batchJobStatusMapping.put(BATCH_JOB_RUN_STATUS.FAILED.label, "danger");
    }

    public List<BatchJobRun> getBatchJobRuns() {
        return batchJobRuns;
    }

    public void setBatchJobRuns(List<BatchJobRun> batchJobRuns) {
        this.batchJobRuns = batchJobRuns;
    }

    public Map<String, String> getBatchJobStatusMapping() {
        return batchJobStatusMapping;
    }

    public void setBatchJobStatusMapping(Map<String, String> batchJobStatusMapping) {
        this.batchJobStatusMapping = batchJobStatusMapping;
    }

    public List<ServerInstance> getServers() {
        return servers;
    }

    public void setServers(List<ServerInstance> servers) {
        this.servers = servers;
    }
    
    public void loadServers(){
        setServers(landingService.getServerInstances(ERP));
    }
    
    public void loadBatchJobs(){
        
        if(startString == null || startString.isEmpty() 
                || endString == null || endString.isEmpty())
            return;
        
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd MMMM YYYY, HH:mm:ss");
        
        DateTime lowDateTime = formatter.parseDateTime(startString);
        Timestamp lowTS = new Timestamp(lowDateTime.getMillis());
        DateTime highDateTime = formatter.parseDateTime(endString);
        Timestamp highTS = new Timestamp(highDateTime.getMillis());
        
        this.setBatchJobRuns(batchScheduleService.getBatchRuns(lowTS, highTS, null));
        
    }

    public BatchJobRun getEditingBatchJobRun() {
        return editingBatchJobRun;
    }

    public void setEditingBatchJobRun(BatchJobRun editingBatchJobRun) {
        this.editingBatchJobRun = editingBatchJobRun;
    }

    public String getStartString() {
        return startString;
    }

    public void setStartString(String startString) {
        this.startString = startString;
    }

    public String getEndString() {
        return endString;
    }

    public void setEndString(String endString) {
        this.endString = endString;
    }

    public BatchJobStep getFirstAndOnlyStep() {
        return firstAndOnlyStep;
    }

    public void setFirstAndOnlyStep(BatchJobStep firstAndOnlyStep) {
        this.firstAndOnlyStep = firstAndOnlyStep;
    }

    public BatchJobTrigger getFirstAndOnlyTrigger() {
        return firstAndOnlyTrigger;
    }

    public void setFirstAndOnlyTrigger(BatchJobTrigger firstAndOnlyTrigger) {
        this.firstAndOnlyTrigger = firstAndOnlyTrigger;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getSCHEDULE_JAVA_DATE_STRING_FORMAT() {
        return SCHEDULE_JAVA_DATE_STRING_FORMAT;
    }

    public String getSCHEDULE_JAVA_TIME_STRING_FORMAT() {
        return SCHEDULE_JAVA_TIME_STRING_FORMAT;
    }

    public String getSCHEDULE_JS_DATE_STRING_FORMAT() {
        return SCHEDULE_JS_DATE_STRING_FORMAT;
    }

    public String getSCHEDULE_JS_TIME_STRING_FORMAT() {
        return SCHEDULE_JS_TIME_STRING_FORMAT;
    }
    
    public DateTime getCurrentRunDateTime() {
        if(getEditingBatchJobRun() == null)
            return null;
        Timestamp ts = getEditingBatchJobRun().getSCHEDULED_TIME();
        DateTime dt = new DateTime(ts.getTime());
        
        return dt;
    }
    
    public String timestampToString(Timestamp ts) {
        if(ts == null)
            //ts = new Timestamp((new DateTime()).getMillis()); //Set today
            return "";
        DateTimeFormatter formatter = DateTimeFormat.forPattern(SCHEDULE_JAVA_DATE_STRING_FORMAT+" "+SCHEDULE_JAVA_TIME_STRING_FORMAT);
        String dateTimeString = formatter.print(ts.getTime());
        return dateTimeString;
    }

    public Timestamp stringToTimestamp(String dateTimeString) {
        if(dateTimeString == null || dateTimeString.isEmpty())
            return null;
        DateTimeFormatter formatter = DateTimeFormat.forPattern(SCHEDULE_JAVA_DATE_STRING_FORMAT+" "+SCHEDULE_JAVA_TIME_STRING_FORMAT);
        DateTime dt = formatter.parseDateTime(dateTimeString);
        Timestamp scheduledTS = new Timestamp(dt.getMillis());
        return scheduledTS;
    }
    
    public String getScheduledTime() {
        if(getEditingBatchJobRun() == null)
            return "";
        String timeString = timestampToString(getEditingBatchJobRun().getSCHEDULED_TIME());
        return timeString;
    }
    
    public void setScheduledTime(String timeString) {
        if(getEditingBatchJobRun() == null)
            return;
        Timestamp ts = stringToTimestamp(timeString);
        this.getEditingBatchJobRun().setSCHEDULED_TIME(ts);
        
    }

    public void updateEditable() {
        switch(BATCH_JOB_RUN_STATUS.valueOf(BATCH_JOB_RUN_STATUS.class,getEditingBatchJobRun().getSTATUS())){
            case WAITING    :   setEditable(true); break;
            case SCHEDULED  :   setEditable(true); break;
            case IN_PROCESS :   setEditable(false); break;
            case COMPLETED  :   setEditable(false); break;
            case FAILED     :   setEditable(false); break;
            case CANCELLED  :   setEditable(false); break;
        }
    }
    
    public void loadBatchJobRun(String runKey){
        /**
         * https://github.com/SegMail/SegMail/issues/57
         * This problem is inherent in JSF but can be prevented.
         */
        if(getEditingBatchJobRun() != null && runKey.equals(getEditingBatchJobRun().getRUN_KEY()))
            return;
        
        List<BatchJobRun> results = batchScheduleService.getJobRunsByKey(runKey);
        if(results == null || results.isEmpty())
            return;
        setEditingBatchJobRun(results.get(0));
        
        //Load trigger and step
        BatchJob bj = getEditingBatchJobRun().getBATCH_JOB();
        
        List<BatchJobTrigger> triggers = batchScheduleService.loadBatchJobTriggers(bj.getBATCH_JOB_ID());
        List<BatchJobStep> steps = batchScheduleService.loadBatchJobSteps(bj.getBATCH_JOB_ID());
        
        if(triggers != null && !triggers.isEmpty())
            this.setFirstAndOnlyTrigger(triggers.get(0));
        
        if(steps != null && !steps.isEmpty())
            this.setFirstAndOnlyStep(steps.get(0));
        
        updateEditable();
    }
    
    private class JobComparator implements Comparator<BatchJobRun> {

        @Override
        public int compare(BatchJobRun o1, BatchJobRun o2) {
            //Compare time scheduled, cancellation
            
            //Compare start and end time
            return -1;
        }
        
    }
}
