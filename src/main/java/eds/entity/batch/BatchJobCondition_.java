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
@StaticMetamodel(BatchJobCondition.class)
public class BatchJobCondition_ {
    public static volatile SingularAttribute<BatchJobCondition,BatchJob> BATCH_JOB;
    public static volatile SingularAttribute<BatchJobCondition,String> SERVICE_NAME;
    public static volatile SingularAttribute<BatchJobCondition,String> SERVICE_METHOD;
    public static volatile ListAttribute<BatchJobCondition,BatchJobCondition> PARAMS;
}
