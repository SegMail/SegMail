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
@StaticMetamodel(BatchJobTrigger.class)
public class BatchJobTrigger_ {
    
    public static volatile SingularAttribute<BatchJobTrigger,BatchJob> BATCH_JOB;
    public static volatile SingularAttribute<BatchJobTrigger,String> TRIGGER_STATUS;
    public static volatile SingularAttribute<BatchJobTrigger,String> CRON_EXPRESSION;
    public static volatile SingularAttribute<BatchJobTrigger,Integer> TRIGGER_ORDER;
}
