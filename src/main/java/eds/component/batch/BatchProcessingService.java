package eds.component.batch;

import eds.component.UpdateObjectService;
import eds.component.data.IncompleteDataException;
import eds.entity.batch.BATCH_JOB_RUN_STATUS;
import eds.entity.batch.BatchJobRun;
import eds.entity.batch.BatchJobRun_;
import eds.entity.batch.BatchJobStep;
import eds.entity.batch.BatchJobStepParam;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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
    UpdateObjectService updateService;

    @EJB
    LandingService landingService;

    @EJB
    BatchExecutionService execService;

    @PostConstruct
    public void init() {
        reported = false;
        
    }

    public void executeJobStep(BatchJobStep batchJobStep) throws BatchProcessingException {
        try {
            int numParams = batchJobStep.getPARAMS().size();
            Object[] params = new Object[numParams];

            for (int i = 0; i < numParams; i++) {
                BatchJobStepParam param = batchJobStep.getPARAMS().get(i);

                if (param.getSERIALIZED_OBJECT() != null && !param.getSERIALIZED_OBJECT().isEmpty()) {
                    Object obj = param.SERIALIZED_OBJECT();
                    Class clazz = obj.getClass();
                    params[i] = obj;
                    continue;
                }
                params[i] = param.getSTRING_VALUE();
            }

            Object ejb = InitialContext.doLookup("java:module/" + batchJobStep.getSERVICE_NAME());
            System.out.println(ejb.getClass().getName());

            Method[] methodArray = ejb.getClass().getMethods();
            for (int i = 0; i < methodArray.length; i++) {
                if (batchJobStep.getSERVICE_METHOD().equals(methodArray[i].getName())) {
                    Method method = methodArray[i];
                    method.invoke(ejb, params);
                }
            }

        } catch (ClassNotFoundException ex) {
            throw new BatchProcessingException("Batch processing failed:", ex);
        } catch (IOException ex) {
            throw new BatchProcessingException("Batch processing failed:", ex);
        } catch (NamingException ex) {
            Logger.getLogger(BatchProcessingService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BatchProcessingService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BatchProcessingService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(BatchProcessingService.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace(System.out);
        }
    }

    /**
     * Retrieves the next required jobs and schedules them by sending a message
     * over JMS to the next N servers.
     *
     * @param scheduleTime
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void processBatchJobQueue(DateTime dt) throws IncompleteDataException {
        String maxJobString = System.getProperty(MAX_JOBS_PER_CRON);
        if (maxJobString == null || maxJobString.isEmpty()) {
            System.setProperty(PROCESS_JOB_MODE, "false");
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Sytem property " + MAX_JOBS_PER_CRON + " is not set", "");
            return;
        }
        int maxJobs = Integer.parseInt(maxJobString);
        List<BatchJobRun> nextNJobs = this.getNextNJobRuns(maxJobs, dt);

        for (BatchJobRun run : nextNJobs) {
            execService.executeJob(run);
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

        CriteriaBuilder builder = updateService.getEm().getCriteriaBuilder();
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

        List<BatchJobRun> results = updateService.getEm().createQuery(query)
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
                //System.out.println("This server node "+landingService.getOwnServerName()+" is not processing any jobs today!");
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
