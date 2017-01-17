package eds.component.batch;

import com.google.common.base.Objects;
import eds.component.UpdateObjectService;
import eds.component.data.IncompleteDataException;
import eds.entity.batch.BATCH_JOB_RUN_STATUS;
import eds.entity.batch.BatchJobRun;
import eds.entity.batch.BatchJobRunError;
import eds.entity.batch.BatchJobRun_;
import eds.entity.batch.BatchJobTrigger;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.joda.time.DateTime;
import seca2.component.landing.LandingService;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author LeeKiatHaw
 */
@Singleton
public class BatchProcessingService {

    public static final String MAX_JOBS_PER_CRON = "BatchProcessingService.MAX_JOBS_PER_CRON";

    public static final String PROCESS_JOB_MODE = "BatchProcessingService.PROCESS_JOB_MODE";

    private boolean reported;

    @EJB
    UpdateObjectService updService;

    @EJB
    LandingService landingService;

    @EJB
    BatchExecutionService execService;
    
    @EJB
    BatchSchedulingService scheduleService;

    @PostConstruct
    public void init() {
        reported = false;
        
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void processBatchJobQueue(DateTime dt) {
        String maxJobString = System.getProperty(MAX_JOBS_PER_CRON);
        if (maxJobString == null || maxJobString.isEmpty()) {
            System.setProperty(PROCESS_JOB_MODE, "false");
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Sytem property " + MAX_JOBS_PER_CRON + " is not set", "");
            return;
        }
        int maxJobs = Integer.parseInt(maxJobString);
        List<BatchJobRun> nextNJobs = this.getNextNJobRuns(maxJobs, dt);
        for (BatchJobRun run : nextNJobs) {
            try {
                //Update the status first
                DateTime start = DateTime.now();
                //run.setSTATUS(BATCH_JOB_RUN_STATUS.IN_PROCESS.label);
                //run.setSTART_TIME(new Timestamp(start.getMillis()));
                run.start(start);
                run.getBATCH_JOB().setLAST_RUN(new Timestamp(start.getMillis()));
                run = updService.getEm().merge(run);
                updService.getEm().flush();
                
                //results.add(execService.executeJob(run));
                Future<?> result = execService.executeJobNew(run);
                Object ret = result.get();
                
                //If an exception has occured
                if(ret != null && Throwable.class.isAssignableFrom(ret.getClass())) {
                    BatchJobRunError newError = new BatchJobRunError(run, (Throwable) ret);
                    updService.getEm().persist(newError);
                    
                    //run.setSTATUS(BATCH_JOB_RUN_STATUS.FAILED.label);
                    run.fail(DateTime.now());
                    updService.getEm().persist(run);
                    continue;
                }
                
                //run.setSTATUS(BATCH_JOB_RUN_STATUS.COMPLETED.label);
                //run.setEND_TIME(new Timestamp(DateTime.now().getMillis()));
                run.complete(DateTime.now());
                run = updService.getEm().merge(run);
                updService.getEm().flush();
                
                //Dont schedule the next run
                if(ret != null && Objects.equal(ret.getClass(), StopNextRunQuickAndDirty.class)) {
                    continue;
                }
                List<BatchJobTrigger> triggers = scheduleService.loadBatchJobTriggers(run.getBATCH_JOB().getBATCH_JOB_ID());
                if (triggers != null && !triggers.isEmpty()) {
                    DateTime next = DateTime.now();
                    //If the entire batch job only took less than 1 second,
                    //we have to add 1 second so that it would be scheduled in the next
                    //second. This is because the granularity of triggerNextBatchJobRun()
                    //is 1 second, not milliseconds.
                    if (next.withMillisOfSecond(0).equals(start.withMillisOfSecond(0))) {
                        next = next.plusSeconds(1);
                    }
                    scheduleService.triggerNextBatchJobRun(next, triggers.get(0));
                }
                
            }
            catch (InterruptedException ex) {
                Logger.getLogger(BatchProcessingService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(BatchProcessingService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BatchProcessingException ex) {
                Logger.getLogger(BatchProcessingService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * Get the next earliest n job runs after given time
     *
     * @param n
     * @param time
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<BatchJobRun> getNextNJobRuns(int n, DateTime time) {

        Timestamp ts = new Timestamp(time.getMillis());
        String serverName = landingService.getOwnServerName();

        CriteriaBuilder builder = updService.getEm().getCriteriaBuilder();
        CriteriaQuery<BatchJobRun> query = builder.createQuery(BatchJobRun.class);
        Root<BatchJobRun> fromBatchJobRun = query.from(BatchJobRun.class);

        query.select(fromBatchJobRun);
        query.where(
                builder.and(
                        builder.equal(fromBatchJobRun.get(BatchJobRun_.STATUS), BATCH_JOB_RUN_STATUS.SCHEDULED.label),
                        builder.equal(fromBatchJobRun.get(BatchJobRun_.SERVER_NAME), serverName),
                        builder.lessThanOrEqualTo(fromBatchJobRun.get(BatchJobRun_.SCHEDULED_TIME), ts)
                )
        );

        query.orderBy(builder.asc(fromBatchJobRun.get(BatchJobRun_.SCHEDULED_TIME)));

        List<BatchJobRun> results = updService.getEm().createQuery(query)
                .setFirstResult(0)
                .setMaxResults(n)
                //.setLockMode(LockModeType.PESSIMISTIC_READ) //The BatchExecutionService should be locking it, not this
                .getResultList();

        return results;

    }

    @Schedule(second = "*/10", minute = "*", hour = "*")
    public void cron() throws IncompleteDataException {
        
        try {
            boolean processMode = this.getProcessMode();
            int cronNum = this.getCronNum();
            
            if(cronNum <= 0)
                throw new BatchProcessingException("Sytem property " + MAX_JOBS_PER_CRON + " is not set");
            
            if(!processMode)
                throw new BatchProcessingException("System property "+ PROCESS_JOB_MODE + " is not set");
            
            DateTime now = DateTime.now();
            this.processBatchJobQueue(now);
            reported = false;
            
        } catch (BatchProcessingException ex) {
            if (!reported) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "This server node " + landingService.getOwnServerName() + " is not processing any jobs today because: "+ex.getMessage(), "");
                reported = true;
            }
        }
    }
    
    public boolean getProcessMode() {
        String processString = System.getProperty(PROCESS_JOB_MODE);
        return Boolean.parseBoolean(processString);
    }
    
    public int getCronNum() {
        String maxJobString = System.getProperty(MAX_JOBS_PER_CRON);
        if (maxJobString == null || maxJobString.isEmpty()) {
            //System.setProperty(PROCESS_JOB_MODE, "false");
            //Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Sytem property " + MAX_JOBS_PER_CRON + " is not set", "");
            //errorMessage = "Sytem property " + MAX_JOBS_PER_CRON + " is not set";
            return -1;
        }
        return Integer.parseInt(maxJobString);
    }
    
}
