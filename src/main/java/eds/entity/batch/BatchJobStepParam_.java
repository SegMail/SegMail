/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(BatchJobStepParam.class)
public class BatchJobStepParam_ {
    public static volatile SingularAttribute<BatchJobStep,BatchJobStep> BATCH_JOB_STEP;
    public static volatile SingularAttribute<BatchJobStep,Integer> SNO;
    public static volatile SingularAttribute<BatchJobStep,String> SERIALIZED_OBJECT;
    public static volatile SingularAttribute<BatchJobStep,String> STRING_VALUE;
}
