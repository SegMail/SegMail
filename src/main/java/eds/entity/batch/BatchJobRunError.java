/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import eds.entity.transaction.EnterpriseTransaction;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import static javax.persistence.ConstraintMode.NO_CONSTRAINT;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
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
        this.BATCH_JOB_ID = BATCH_JOB_RUN.getBATCH_JOB().getBATCH_JOB_ID();
        this.BATCH_JOB_RUN_KEY = BATCH_JOB_RUN.getRUN_KEY();
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
    
    
}
