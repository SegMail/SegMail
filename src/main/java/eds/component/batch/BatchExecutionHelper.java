/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.batch;

import eds.component.UpdateObjectService;
import eds.entity.batch.BATCH_JOB_RUN_STATUS;
import eds.entity.batch.BatchJobCondition;
import eds.entity.batch.BatchJobRun;
import eds.entity.batch.BatchJobRunError;
import eds.entity.batch.BatchJobSchedule;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.joda.time.DateTime;

/**
 * This class is necessary because when a REQUIRES_NEW transaction is invoked,
 * if the invoked method is in the same class, the same transaction will be used
 * and will not flush and commit even after the invocation ends.
 * 
 * @author LeeKiatHaw
 */
@Stateless
public class BatchExecutionHelper {
    
    @EJB
    UpdateObjectService updService;
    @EJB
    BatchSchedulingService scheduleService;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BatchJobRun startJob(BatchJobRun job) {
        //reload the job as this instance exists in the last transaction
        job = updService.getEm().find(BatchJobRun.class, job.getRUN_KEY());
        //Update the status first to "lock" the BatchJobRun record
        job = updService.getEm().merge(job);
        job.start(DateTime.now());
        //updService.getEm().flush();
        return job;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void logErrors(BatchJobRun job, Throwable ex) {
        BatchJobRunError newError = new BatchJobRunError(job, ex);

        updService.getEm().persist(newError);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void triggerNextRun(BatchJobRun job, List<BatchJobCondition> conds, List<BatchJobSchedule> schedules) 
            throws BatchProcessingException {
        //If there is a condition that say don't run, don't run. Else, continue.
        if(conds != null && !conds.isEmpty() && !conds.get(0).continueNextRun())
            return;

        //If there is no schedules defined, it means this job is run-once only.
        if(schedules == null || schedules.isEmpty())
            return;

        DateTime next = DateTime.now();
        //If the entire batch job only took less than 1 second,
        //we have to add 1 second so that it would be scheduled in the next
        //second. This is because the granularity of triggerNextBatchJobRun()
        //is 1 second, not milliseconds.
        DateTime start = new DateTime(job.getSTART_TIME().getTime());
        if (next.withMillisOfSecond(0).equals(start.withMillisOfSecond(0))) {
            next = next.plusSeconds(1);
        }
        scheduleService.triggerNextBatchJobRun(next, schedules.get(0).getBATCH_JOB());
    }
}
