/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import seca2.entity.landing.ServerInstance;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(BatchJob.class)
public class BatchJob_ {
    public static volatile SingularAttribute<BatchJob,Long> BATCH_JOB_ID;
    public static volatile ListAttribute<BatchJob,BatchJobStep> STEPS;
    public static volatile ListAttribute<BatchJob,BatchJobStep> TRIGGERS;
    public static volatile SingularAttribute<BatchJob,String> STATUS;
    //public static volatile SingularAttribute<BatchJob,ServerInstance> SERVER;
    public static volatile SingularAttribute<BatchJob,String> SERVER_NAME;
    public static volatile SingularAttribute<BatchJob,java.sql.Timestamp> DATETIME_CREATED;
    public static volatile SingularAttribute<BatchJob,java.sql.Timestamp> DATETIME_CHANGED;
    public static volatile SingularAttribute<BatchJob,String> CREATED_BY;
    public static volatile SingularAttribute<BatchJob,String> CHANGED_BY;
    public static volatile SingularAttribute<BatchJob,java.sql.Timestamp> LAST_RUN;
}
