/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(BatchJob.class)
public class BatchJob_ {
    public static volatile SingularAttribute<BatchJob,Long> BATCH_JOB_ID;
    public static volatile ListAttribute<BatchJob,BatchJobStep> STEPS;
    public static volatile SingularAttribute<BatchJob,String> STATUS;
    public static volatile SingularAttribute<BatchJob,java.sql.Timestamp> SCHEDULED_TIME;
    public static volatile SingularAttribute<BatchJob,java.sql.Timestamp> START_TIME;
    public static volatile SingularAttribute<BatchJob,java.sql.Timestamp> END_TIME;
}
