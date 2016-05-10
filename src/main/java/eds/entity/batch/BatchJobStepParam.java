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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * A batch job param can only be the following: - EnterpriseObject (accessible
 * by 1 unique ID) - EnterpriseRelationship (accessible by 2 unique IDs) -
 * EnterpriseTransaction (accessible by 1 transaction key) - String - Integer -
 * Long
 *
 * The most recommended way is by EnterpriseTransaction as it can be used to
 * store a range of IDs, dates, names, etc, to access to the other objects.
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name = "BATCH_JOB_STEP_PARAM")
public class BatchJobStepParam implements Serializable {

    private BatchJobStep BATCH_JOB_STEP;
    private int SNO;

    /**
     * Using Serializable interface to export POJOs into the database.
     */
    private String SERIALIZED_OBJECT;

    private String STRING_VALUE;

    @Id
    @ManyToOne(cascade = {
                CascadeType.PERSIST,
                CascadeType.MERGE,
                CascadeType.REFRESH
            })
    /*@JoinColumns({
        @JoinColumn(
                name="BATCH_JOB",
                referencedColumnName="BATCH_JOB"
        ),
        @JoinColumn(
                name="BATCH_JOB_STEP_NO",
                referencedColumnName="STEP_NO"
        )
    })*/
    public BatchJobStep getBATCH_JOB_STEP() {
        return BATCH_JOB_STEP;
    }

    public void setBATCH_JOB_STEP(BatchJobStep BATCH_JOB_STEP) {
        this.BATCH_JOB_STEP = BATCH_JOB_STEP;
    }

    @Id
    public int getSNO() {
        return SNO;
    }

    public void setSNO(int SNO) {
        this.SNO = SNO;
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
