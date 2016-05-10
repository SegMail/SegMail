/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import seca2.entity.landing.ServerInstance;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(BatchJobRun.class)
public class BatchJobRun_ {

    public static volatile SingularAttribute<BatchJobRun, BatchJob> BATCH_JOB;
    public static volatile SingularAttribute<BatchJobRun, String> RUN_KEY;
    public static volatile SingularAttribute<BatchJobRun, java.sql.Timestamp> DATETIME_CREATED;
    public static volatile SingularAttribute<BatchJobRun, java.sql.Timestamp> SCHEDULED_TIME;
    public static volatile SingularAttribute<BatchJobRun, java.sql.Timestamp> START_TIME;
    public static volatile SingularAttribute<BatchJobRun, java.sql.Timestamp> END_TIME;
    public static volatile SingularAttribute<BatchJobRun, String> CREATED_BY;
    public static volatile SingularAttribute<BatchJobRun, String> RUN_BY;
    public static volatile SingularAttribute<BatchJobRun, String> STATUS;
    public static volatile SingularAttribute<BatchJobRun, ServerInstance> SERVER;
}
