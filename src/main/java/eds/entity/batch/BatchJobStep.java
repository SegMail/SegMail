/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import static javax.persistence.ConstraintMode.NO_CONSTRAINT;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="BATCH_JOB_STEP")
public class BatchJobStep implements Serializable {
    
    private BatchJob BATCH_JOB;
    
    private int STEP_NO;
    
    private String SERVICE_NAME;
    
    private String SERVICE_METHOD;
    
    private List<BatchJobStepParam> PARAMS = new ArrayList<>();

    public String getSERVICE_NAME() {
        return SERVICE_NAME;
    }

    public void setSERVICE_NAME(String SERVICE_NAME) {
        this.SERVICE_NAME = SERVICE_NAME;
    }

    @OneToMany(mappedBy="BATCH_JOB_STEP")
    public List<BatchJobStepParam> getPARAMS() {
        return PARAMS;
    }

    public void setPARAMS(List<BatchJobStepParam> PARAMS) {
        this.PARAMS = PARAMS;
    }

    @Id
    public int getSTEP_NO() {
        return STEP_NO;
    }

    public void setSTEP_NO(int STEP_NO) {
        this.STEP_NO = STEP_NO;
    }

    @Id @ManyToOne(cascade = {
                CascadeType.PERSIST,
                CascadeType.MERGE,
                CascadeType.REFRESH
            })
    @JoinColumn(name="BATCH_JOB",
            referencedColumnName="BATCH_JOB_ID",
            foreignKey=@ForeignKey(name="BATCH_JOB"))
    public BatchJob getBATCH_JOB() {
        return BATCH_JOB;
    }

    public void setBATCH_JOB(BatchJob BATCH_JOB) {
        this.BATCH_JOB = BATCH_JOB;
    }

    public String getSERVICE_METHOD() {
        return SERVICE_METHOD;
    }

    public void setSERVICE_METHOD(String SERVICE_METHOD) {
        this.SERVICE_METHOD = SERVICE_METHOD;
    }
    
    public void addPARAMS(BatchJobStepParam param) {
        this.PARAMS.add(param);
    }
}
