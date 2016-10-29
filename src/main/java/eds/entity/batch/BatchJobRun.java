/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.CascadeType;
import static javax.persistence.ConstraintMode.NO_CONSTRAINT;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.joda.time.DateTime;
import seca2.entity.landing.ServerInstance;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name = "BATCH_JOB_RUN")
@EntityListeners({
    BatchJobRunListener.class
})
public class BatchJobRun implements Serializable {
    
    private BatchJob BATCH_JOB;
    
    private String RUN_KEY;
    
    private java.sql.Timestamp DATETIME_CREATED;
    private java.sql.Timestamp SCHEDULED_TIME;
    private java.sql.Timestamp START_TIME;
    private java.sql.Timestamp END_TIME;
    private java.sql.Timestamp CANCEL_TIME;
    
    private String CREATED_BY;
    private String RUN_BY;
    
    private String STATUS;
    
    /**
     * The execution server for a BatchJob can be changed anytime within its 
     * lifecycle. After a couple of runs, the administrator may decide that the 
     * BatchJob should switch server from A to B due to load, then in BatchJob, 
     * it would be updated as B, in the previous completed runs for BatchJobRun
     * it should read A but all future runs will read B.
     */
    //private ServerInstance SERVER;
    
    private String SERVER_NAME;

    @Id
    @ManyToOne(cascade={
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
    public String getRUN_KEY() {
        return RUN_KEY;
    }

    public void setRUN_KEY(String RUN_KEY) {
        this.RUN_KEY = RUN_KEY;
    }

    public Timestamp getSCHEDULED_TIME() {
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
    }

    public String getRUN_BY() {
        return RUN_BY;
    }

    public void setRUN_BY(String RUN_BY) {
        this.RUN_BY = RUN_BY;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    /*@ManyToOne
    @JoinColumn(name="SERVER",
            referencedColumnName="OBJECTID",
            foreignKey=@ForeignKey(name="SERVER",value=NO_CONSTRAINT))
    public ServerInstance getSERVER() {
        return SERVER;
    }

    public void setSERVER(ServerInstance SERVER) {
        this.SERVER = SERVER;
    }*/

    /*public void setSCHEDULED_TIME(DateTime nextExecution) {
        Timestamp ts = new Timestamp(nextExecution.getMillis());
        this.setSCHEDULED_TIME(ts);
    }*/

    public Timestamp getDATETIME_CREATED() {
        return DATETIME_CREATED;
    }

    public void setDATETIME_CREATED(Timestamp DATETIME_CREATED) {
        this.DATETIME_CREATED = DATETIME_CREATED;
    }

    public String getCREATED_BY() {
        return CREATED_BY;
    }

    public void setCREATED_BY(String CREATED_BY) {
        this.CREATED_BY = CREATED_BY;
    }

    public Timestamp getCANCEL_TIME() {
        return CANCEL_TIME;
    }

    public void setCANCEL_TIME(Timestamp CANCEL_TIME) {
        this.CANCEL_TIME = CANCEL_TIME;
    }

    public String getSERVER_NAME() {
        return SERVER_NAME;
    }

    public void setSERVER_NAME(String SERVER_NAME) {
        this.SERVER_NAME = SERVER_NAME;
    }
    
    //Lifecycle methods
    
    /**
     * Don't do shit.
     * 
     * @param changeDateTime 
     */
    public void wait(DateTime changeDateTime) {
        //Create high end date
        DateTime high = new DateTime(9999,12,31,23,59,59);
        this.setSCHEDULED_TIME(null);
        this.setSTART_TIME(null);
        this.setEND_TIME(new Timestamp(high.getMillis()));
        this.setCANCEL_TIME(null);
        this.setSTATUS(BATCH_JOB_RUN_STATUS.SCHEDULED.label);
    }
    
    /**
     * Do shit when I say it's time to do shit.
     * 
     * @param changeDateTime 
     */
    public void schedule(DateTime changeDateTime){
        //Create high end date
        DateTime high = new DateTime(9999,12,31,23,59);
        this.setSCHEDULED_TIME(new Timestamp(changeDateTime.getMillis()));
        this.setSTART_TIME(null);
        this.setEND_TIME(new Timestamp(high.getMillis()));
        this.setCANCEL_TIME(null);
        this.setSTATUS(BATCH_JOB_RUN_STATUS.SCHEDULED.label);
    }
    
    /**
     * Do it.
     * 
     * @param changeDateTime 
     */
    public void start(DateTime changeDateTime) {
        //Create high end date
        DateTime high = new DateTime(9999,12,31,23,59);
        this.setSTART_TIME(new Timestamp(changeDateTime.getMillis()));
        this.setEND_TIME(new Timestamp(high.getMillis()));
        this.setCANCEL_TIME(null);
        this.setSTATUS(BATCH_JOB_RUN_STATUS.IN_PROCESS.label);
    }
    
    /**
     * It's off. 
     * 
     * @param changeDateTime 
     */
    public void cancel(DateTime changeDateTime) {
        this.setCANCEL_TIME(new Timestamp(changeDateTime.getMillis()));
        this.setEND_TIME(this.getCANCEL_TIME());
        this.setSTATUS(BATCH_JOB_RUN_STATUS.CANCELLED.label);
    }
    
    /**
     * It's done.
     * 
     * @param changeDateTime 
     */
    public void complete(DateTime changeDateTime) {
        this.setEND_TIME(new Timestamp(changeDateTime.getMillis()));
        this.setCANCEL_TIME(null);
        this.setSTATUS(BATCH_JOB_RUN_STATUS.COMPLETED.label);
    }
    
    public void fail(DateTime changeDateTime) {
        this.setEND_TIME(new Timestamp(changeDateTime.getMillis()));
        this.setCANCEL_TIME(null);
        this.setSTATUS(BATCH_JOB_RUN_STATUS.FAILED.label);
    }
}
