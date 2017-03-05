/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.batch;

import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.entity.batch.BatchJobCondition;
import eds.entity.batch.BatchJobRun;
import eds.entity.batch.BatchJobRunLog;
import eds.entity.batch.BatchJobSchedule;
import eds.entity.batch.BatchJobStep;
import eds.entity.batch.BatchJobStep_;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class BatchExecutionService {

    @EJB
    GenericObjectService objService;
    @EJB
    UpdateObjectService updService;
    @EJB
    BatchSchedulingService scheduleService;
    @EJB
    BatchExecutionHelper execHelper;

    /**
     * Processes 1 job.
     *
     * @param job
     * @return
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void executeJob(BatchJobRun job) {

        execHelper.startJob(job);

        //Loop through each step, execute and get the returned result.
        //If it fails in any of the 
        List<BatchJobStep> steps = this.getBatchJobSteps(job.getBATCH_JOB().getBATCH_JOB_ID());
        try {
            for (BatchJobStep step : steps) {
                Object ret = step.execute(); 
                //Create log entry
                BatchJobRunLog log = new BatchJobRunLog();
                log.setBATCH_JOB_RUN(job);
                log.setTIME(new java.sql.Timestamp(DateTime.now().getMillis()));
                updService.getEm().persist(log);
            }
            
            //Trigger the next batch run
            List<BatchJobCondition> conds = scheduleService.loadBatchJobConditions(job.getBATCH_JOB().getBATCH_JOB_ID());
            List<BatchJobSchedule> schedules = scheduleService.loadBatchJobSchedules(job.getBATCH_JOB().getBATCH_JOB_ID());
            
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(BatchExecutionService.class.getName()).log(Level.SEVERE, null, ex);
            execHelper.logErrors(job,ex);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<BatchJobStep> getBatchJobSteps(long batchJobId) {
        CriteriaBuilder builder = objService.getEm().getCriteriaBuilder();
        CriteriaQuery<BatchJobStep> query = builder.createQuery(BatchJobStep.class);
        Root<BatchJobStep> fromBatchJobRun = query.from(BatchJobStep.class);

        query.select(fromBatchJobRun);
        query.where(builder.and(
                builder.equal(fromBatchJobRun.get(BatchJobStep_.BATCH_JOB), batchJobId)
        ));

        List<BatchJobStep> results = objService.getEm().createQuery(query)
                .getResultList();

        return results;
    }
    
}
