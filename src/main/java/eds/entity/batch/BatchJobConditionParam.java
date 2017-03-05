/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import static javax.persistence.ConstraintMode.NO_CONSTRAINT;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name = "BATCH_JOB_CONDITION_PARAM")
public class BatchJobConditionParam implements Serializable {
    
    private BatchJobCondition BATCH_JOB_CONDITION;
    private int PARAM_ORDER;
    
    private String SERIALIZED_OBJECT;
    
    private String STRING_VALUE;

    @Id
    @ManyToOne(cascade = {
                //CascadeType.PERSIST,
                CascadeType.MERGE,
                CascadeType.REFRESH
            })
    @JoinColumns({
        @JoinColumn(
                name="BATCH_JOB",
                referencedColumnName="BATCH_JOB",
                foreignKey = @ForeignKey(name = "BATCH_JOB", value = NO_CONSTRAINT)
        )
    })
    public BatchJobCondition getBATCH_JOB_CONDITION() {
        return BATCH_JOB_CONDITION;
    }

    public void setBATCH_JOB_CONDITION(BatchJobCondition BATCH_JOB_CONDITION) {
        this.BATCH_JOB_CONDITION = BATCH_JOB_CONDITION;
    }

    public int getPARAM_ORDER() {
        return PARAM_ORDER;
    }

    public void setPARAM_ORDER(int PARAM_ORDER) {
        this.PARAM_ORDER = PARAM_ORDER;
    }

    @Column(columnDefinition = "BLOB")
    public String getSERIALIZED_OBJECT() {
        return SERIALIZED_OBJECT;
    }

    public void setSERIALIZED_OBJECT(String SERIALIZED_OBJECT) {
        this.SERIALIZED_OBJECT = SERIALIZED_OBJECT;
    }
    
    public void setSERIALIZED_OBJECT(Serializable s) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(s);
        oos.close();

        String serializedString = Base64.getEncoder().encodeToString(baos.toByteArray());
        setSERIALIZED_OBJECT(serializedString);
    }
    
    public Object SERIALIZED_OBJECT() throws IOException, ClassNotFoundException{
        byte [] data = Base64.getDecoder().decode(getSERIALIZED_OBJECT());
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
    }
    
    public String getSTRING_VALUE() {
        return STRING_VALUE;
    }

    public void setSTRING_VALUE(String STRING_VALUE) {
        this.STRING_VALUE = STRING_VALUE;
    }
    
}
