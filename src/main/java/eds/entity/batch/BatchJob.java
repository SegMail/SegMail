/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

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
    
    private String STATUS;
    
    private String SERVER_NAME;
    
    /**
     * Over-simplification of BatchJobTrigger
     */
    private java.sql.Timestamp DATETIME_CREATED;
    private java.sql.Timestamp DATETIME_CHANGED;
    
    private String CREATED_BY;
    private String CHANGED_BY;
    
    private java.sql.Timestamp LAST_RUN;

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

    public String getSERVER_NAME() {
        return SERVER_NAME;
    }

    public void setSERVER_NAME(String SERVER_NAME) {
        this.SERVER_NAME = SERVER_NAME;
    }
}
