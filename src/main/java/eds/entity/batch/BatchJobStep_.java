/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import java.util.List;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(BatchJobStep.class)
public class BatchJobStep_ {
    
    public static volatile SingularAttribute<BatchJobStep,BatchJob> BATCH_JOB;
    public static volatile SingularAttribute<BatchJobStep,Integer> STEP_NO;
    public static volatile SingularAttribute<BatchJobStep,String> SERVICE_NAME;
    public static volatile SingularAttribute<BatchJobStep,String> SERVICE_METHOD;
    public static volatile ListAttribute<BatchJobStep,BatchJobStepParam> PARAMS;
}
