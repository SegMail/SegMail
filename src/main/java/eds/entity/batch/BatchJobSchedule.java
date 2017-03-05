/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import java.io.Serializable;
import javax.persistence.CascadeType;
import static javax.persistence.ConstraintMode.NO_CONSTRAINT;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Defines the schedule of the BatchJob. The absence of it means batch job only 
 * run once.
 * 
 * @author LeeKiatHaw
 */
@Entity
@Table(name="BATCH_JOB_SCHEDULE")
public class BatchJobSchedule implements Serializable {
    
    private BatchJob BATCH_JOB;
    
    private String TRIGGER_STATUS;
    
    private String CRON_EXPRESSION;
    
    private int TRIGGER_ORDER;

    @Id
    @ManyToOne(cascade = {
        CascadeType.MERGE,
        CascadeType.REFRESH
    })
    @JoinColumn(name = "BATCH_JOB",
            referencedColumnName = "BATCH_JOB_ID",
            foreignKey = @ForeignKey(name = "BATCH_JOB",value=NO_CONSTRAINT))
    public BatchJob getBATCH_JOB() {
        return BATCH_JOB;
    }

    public void setBATCH_JOB(BatchJob BATCH_JOB) {
        this.BATCH_JOB = BATCH_JOB;
    }

    @Id
    public int getTRIGGER_ORDER() {
        return TRIGGER_ORDER;
    }

    public void setTRIGGER_ORDER(int TRIGGER_ORDER) {
        this.TRIGGER_ORDER = TRIGGER_ORDER;
    }

    public String getCRON_EXPRESSION() {
        return CRON_EXPRESSION;
    }

    public void setCRON_EXPRESSION(String CRON_EXPRESSION) {
        this.CRON_EXPRESSION = CRON_EXPRESSION;
    }

    public String getTRIGGER_STATUS() {
        return TRIGGER_STATUS;
    }

    public void setTRIGGER_STATUS(String TRIGGER_STATUS) {
        this.TRIGGER_STATUS = TRIGGER_STATUS;
    }
    
    
}
