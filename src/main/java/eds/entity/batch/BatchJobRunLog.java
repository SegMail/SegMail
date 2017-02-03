/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name = "BATCH_JOB_RUN_LOG")
public class BatchJobRunLog implements Serializable {
    
    private BatchJobRun BATCH_JOB_RUN;
    private int STEP_ORDER;
    private java.sql.Timestamp TIME;
    private String MESSAGE;

    @Id
    @ManyToOne
    @SequenceGenerator(name = "gen_batch_job_run_log_id", sequenceName = "batch_job_run_log_id", allocationSize = 1)
    public BatchJobRun getBATCH_JOB_RUN() {
        return BATCH_JOB_RUN;
    }

    public void setBATCH_JOB_RUN(BatchJobRun BATCH_JOB_RUN) {
        this.BATCH_JOB_RUN = BATCH_JOB_RUN;
    }

    public Timestamp getTIME() {
        return TIME;
    }

    public void setTIME(Timestamp TIME) {
        this.TIME = TIME;
    }

    public String getMESSAGE() {
        return MESSAGE;
    }

    public void setMESSAGE(String MESSAGE) {
        this.MESSAGE = MESSAGE;
    }

    @Id
    public int getSTEP_ORDER() {
        return STEP_ORDER;
    }

    public void setSTEP_ORDER(int STEP_ORDER) {
        this.STEP_ORDER = STEP_ORDER;
    }
    
    
}
