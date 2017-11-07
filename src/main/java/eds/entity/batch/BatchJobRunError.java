/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import eds.entity.transaction.EnterpriseTransaction;
import eds.entity.transaction.TransactionStatus;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.joda.time.DateTime;

/**
 * This is a log class/table, not a status class/table
 * @author LeeKiatHaw
 */
@Entity
@Table(name="BATCH_JOB_RUN_ERROR")
public class BatchJobRunError extends EnterpriseTransaction {
    
    private long BATCH_JOB_ID;
    
    private String BATCH_JOB_RUN_KEY;
    
    private String EXCEPTION_CLASS;
    
    private String EXCEPTION_MESSAGE;
    
    private String STACK_TRACE;

    public BatchJobRunError() {
    }

    public BatchJobRunError(BatchJobRun BATCH_JOB_RUN, Throwable ex) {
        //Temporary fix to discover root cause
        if(BATCH_JOB_RUN != null) {
            this.BATCH_JOB_ID = BATCH_JOB_RUN.getBATCH_JOB().getBATCH_JOB_ID();
            this.BATCH_JOB_RUN_KEY = BATCH_JOB_RUN.getRUN_KEY();
        }
        
        this.EXCEPTION_CLASS = ex.getClass().getName();
        this.EXCEPTION_MESSAGE = ex.getMessage();
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        this.STACK_TRACE = sw.toString();
    }

    public String getEXCEPTION_CLASS() {
        return EXCEPTION_CLASS;
    }

    public void setEXCEPTION_CLASS(String EXCEPTION_CLASS) {
        this.EXCEPTION_CLASS = EXCEPTION_CLASS;
    }

    @Column(columnDefinition="MEDIUMTEXT")
    public String getEXCEPTION_MESSAGE() {
        return EXCEPTION_MESSAGE;
    }

    public void setEXCEPTION_MESSAGE(String EXCEPTION_MESSAGE) {
        this.EXCEPTION_MESSAGE = EXCEPTION_MESSAGE;
    }

    @Column(columnDefinition="MEDIUMTEXT")
    public String getSTACK_TRACE() {
        return STACK_TRACE;
    }

    public void setSTACK_TRACE(String STACK_TRACE) {
        this.STACK_TRACE = STACK_TRACE;
    }

    public long getBATCH_JOB_ID() {
        return BATCH_JOB_ID;
    }

    public void setBATCH_JOB_ID(long BATCH_JOB_ID) {
        this.BATCH_JOB_ID = BATCH_JOB_ID;
    }

    public String getBATCH_JOB_RUN_KEY() {
        return BATCH_JOB_RUN_KEY;
    }

    public void setBATCH_JOB_RUN_KEY(String BATCH_JOB_RUN_KEY) {
        this.BATCH_JOB_RUN_KEY = BATCH_JOB_RUN_KEY;
    }

    @Override
    public <Ts extends TransactionStatus> Ts PROCESSING_STATUS() {
        return null;
    }

    @Override
    public BatchJobRunError transit(TransactionStatus newStatus, DateTime dt) {
        return this;
    }
    
    
}
