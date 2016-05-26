/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import eds.component.batch.BatchProcessingException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.ConstraintMode.NO_CONSTRAINT;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import seca2.entity.landing.ServerInstance;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="BATCH_JOB")
@TableGenerator(name="BATCH_JOB_SEQ",initialValue=1,allocationSize=1,table="SEQUENCE")
@EntityListeners({
    BatchJobListener.class
})
public class BatchJob implements Serializable {
    
    private long BATCH_JOB_ID;
    
    private String BATCH_JOB_NAME;
    
    /**
     * The steps within a batch job. Each step is an EJB service method with a 
     * set of parameters.
     */
    //private List<BatchJobStep> STEPS = new ArrayList<>();
    
    /**
     * The triggers that will fire the next run. A batch job can have many triggers
     * and each one can fire off their individual runs, which should be independent 
     * of each other.
     */
    //private List<BatchJobTrigger> TRIGGERS = new ArrayList<>();
    
    /**
     * The instantiated runs of the batch job. Each run should be independent of 
     * each other regardless of schedule and execution content. 
     */
    //private List<BatchJobRun> RUNS = new ArrayList<>();
    
    private String STATUS;
    
    private ServerInstance SERVER;
    
    /**
     * Over-simplification of BatchJobTrigger
     */
    private java.sql.Timestamp DATETIME_CREATED;
    private java.sql.Timestamp DATETIME_CHANGED;
    //private java.sql.Timestamp SCHEDULED_TIME;
    //private java.sql.Timestamp START_TIME;
    //private java.sql.Timestamp END_TIME;
    
    private String CREATED_BY;
    private String CHANGED_BY;
    
    private java.sql.Timestamp LAST_RUN;
    

    /*@OneToMany(cascade={
        CascadeType.MERGE
    })
    @JoinColumn(name="BATCH_JOB") //Required, if not you'll end up with another table
    @OrderColumn(name="STEP_ORDER")
    public List<BatchJobStep> getSTEPS() {
        return STEPS;
    }

    public void setSTEPS(List<BatchJobStep> STEPS) {
        this.STEPS = STEPS;
    }*/

    @Id @GeneratedValue(generator="BATCH_JOB_SEQ",strategy=GenerationType.TABLE) 
    public long getBATCH_JOB_ID() {
        return BATCH_JOB_ID;
    }

    public void setBATCH_JOB_ID(long BATCH_JOB_ID) {
        this.BATCH_JOB_ID = BATCH_JOB_ID;
    }

    public String getSTATUS() {
        return STATUS;
    }

    /**
     * Set local scope to allow BatchJobLifecycleManager to modify only.
     * 
     * @param STATUS 
     */
    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }
    
    /*public Timestamp getSCHEDULED_TIME() {
        return SCHEDULED_TIME;
    }

    public void setSCHEDULED_TIME(Timestamp SCHEDULED_TIME) {
        this.SCHEDULED_TIME = SCHEDULED_TIME;
    }

    public Timestamp getSTART_TIME() {
        return START_TIME;
    }

    public void setSTART_TIME(Timestamp START_TIME) {
        this.START_TIME = START_TIME;
    }

    public Timestamp getEND_TIME() {
        return END_TIME;
    }

    public void setEND_TIME(Timestamp END_TIME) {
        this.END_TIME = END_TIME;
    }*/

    @ManyToOne
    @JoinColumn(name="SERVER",
            referencedColumnName="OBJECTID",
            foreignKey=@ForeignKey(name="SERVER",value=NO_CONSTRAINT))
    public ServerInstance getSERVER() {
        return SERVER;
    }

    public void setSERVER(ServerInstance SERVER) {
        this.SERVER = SERVER;
    }

    public Timestamp getDATETIME_CREATED() {
        return DATETIME_CREATED;
    }

    public void setDATETIME_CREATED(Timestamp DATETIME_CREATED) {
        this.DATETIME_CREATED = DATETIME_CREATED;
    }

    public Timestamp getDATETIME_CHANGED() {
        return DATETIME_CHANGED;
    }

    public void setDATETIME_CHANGED(Timestamp DATETIME_CHANGED) {
        this.DATETIME_CHANGED = DATETIME_CHANGED;
    }

    public String getCREATED_BY() {
        return CREATED_BY;
    }

    public void setCREATED_BY(String CREATED_BY) {
        this.CREATED_BY = CREATED_BY;
    }

    public String getCHANGED_BY() {
        return CHANGED_BY;
    }

    public void setCHANGED_BY(String CHANGED_BY) {
        this.CHANGED_BY = CHANGED_BY;
    }

    /*@OneToMany(cascade={
        CascadeType.MERGE,
        CascadeType.REFRESH
    })
    @JoinColumn(name="BATCH_JOB")
    @OrderColumn(name="TRIGGER_ORDER")
    public List<BatchJobTrigger> getTRIGGERS() {
        return TRIGGERS;
    }

    public void setTRIGGERS(List<BatchJobTrigger> TRIGGERS) {
        this.TRIGGERS = TRIGGERS;
    }*/

    public String getBATCH_JOB_NAME() {
        return BATCH_JOB_NAME;
    }

    public void setBATCH_JOB_NAME(String BATCH_JOB_NAME) {
        this.BATCH_JOB_NAME = BATCH_JOB_NAME;
    }

    public Timestamp getLAST_RUN() {
        return LAST_RUN;
    }

    public void setLAST_RUN(Timestamp LAST_RUN) {
        this.LAST_RUN = LAST_RUN;
    }
    
    
    /**
     * There's no remove yet because we want to keep things simple - each BatchJob
     * only has 1 BatchJobStep at the moment.
     * 
     * @param step 
     */
    /*public void addSTEP(BatchJobStep step) {
        if(!STEPS.isEmpty())
            STEPS.clear(); //Just clear everything. Keep things simple.
        STEPS.add(step);
    }*/
    
    /**
     * 
     * @throws BatchProcessingException 
     */
    /*public void execute() throws BatchProcessingException{
        for (BatchJobStep step : getSTEPS()){
            step.execute();
        }
    }*/
}
