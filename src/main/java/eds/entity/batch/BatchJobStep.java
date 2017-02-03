/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.CascadeType;
import static javax.persistence.ConstraintMode.NO_CONSTRAINT;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name = "BATCH_JOB_STEP")
public class BatchJobStep implements Serializable {

    private BatchJob BATCH_JOB;

    private int STEP_ORDER;

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
    /*@JoinColumns({
        @JoinColumn(name = "BATCH_JOB_STEP"),
        @JoinColumn(name = "BATCH_JOB_STEP_ORDER")
    })*/
    @OrderColumn(name = "PARAM_ORDER")
    public List<BatchJobStepParam> getPARAMS() {
        return PARAMS;
    }

    public void setPARAMS(List<BatchJobStepParam> PARAMS) {
        this.PARAMS = PARAMS;
    }

    @Id
    @ManyToOne(cascade = {
        //CascadeType.PERSIST,
        CascadeType.MERGE,
        CascadeType.REFRESH
    })
    @JoinColumn(name = "BATCH_JOB",
            referencedColumnName = "BATCH_JOB_ID",
            foreignKey = @ForeignKey(name = "BATCH_JOB", value = NO_CONSTRAINT))
    public BatchJob getBATCH_JOB() {
        return BATCH_JOB;
    }

    /**
     * It doesn't matter now whether this field is auto-generated because all 
     * batch jobs only have 1 step.
     * 
     * @return 
     */
    @Id
    public int getSTEP_ORDER() {
        return STEP_ORDER;
    }

    public void setSTEP_ORDER(int STEP_ORDER) {
        this.STEP_ORDER = STEP_ORDER;
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

    public Object execute() 
            throws 
            IOException, 
            ClassNotFoundException, 
            NamingException, 
            IllegalAccessException, 
            InvocationTargetException {
        int numParams = getPARAMS().size();
        Object[] params = new Object[numParams];

        for (int i = 0; i < numParams; i++) {
            BatchJobStepParam param = getPARAMS().get(i);

            if (param.getSERIALIZED_OBJECT() != null && !param.getSERIALIZED_OBJECT().isEmpty()) {
                Object obj = param.SERIALIZED_OBJECT();
                Class clazz = obj.getClass();
                params[i] = obj;
                continue;
            }
            params[i] = param.getSTRING_VALUE();
        }

        Object ejb = InitialContext.doLookup("java:module/" + getSERVICE_NAME());

        Object ret = null;
        Method[] methodArray = ejb.getClass().getMethods();
        for (int i = 0; i < methodArray.length; i++) {
            if (getSERVICE_METHOD().equals(methodArray[i].getName())) {
                Method method = methodArray[i];
                ret = method.invoke(ejb, params);
                break;
            }
        }
        
        return ret;
    }
}
