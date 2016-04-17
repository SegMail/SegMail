/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import static javax.persistence.ConstraintMode.NO_CONSTRAINT;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="BATCH_JOB")
@TableGenerator(name="BATCH_JOB_SEQ",initialValue=1,allocationSize=100,table="SEQUENCE")
public class BatchJob implements Serializable {
    
    private long BATCH_JOB_ID;
    
    private List<BatchJobStep> STEPS = new ArrayList<>();
    
    private String STATUS;

    @OneToMany(mappedBy="BATCH_JOB") //Required, if not you'll end up with another table
    /*@JoinColumn(name="BATCH_JOB",
            referencedColumnName="BATCH_JOB_ID",
            foreignKey=@ForeignKey(name="BATCH_JOB"))*/
    public List<BatchJobStep> getSTEPS() {
        return STEPS;
    }

    public void setSTEPS(List<BatchJobStep> STEPS) {
        this.STEPS = STEPS;
    }

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

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }
    
    public void addSTEP(BatchJobStep step) {
        STEPS.add(step);
    }
    
}
