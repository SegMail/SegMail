/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.batch;

import eds.component.DBService;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.joda.time.DateTime;

/**
 * This helper class is necessary because when a REQUIRES_NEW transaction is invoked,
 * if the invoked method is in the same class, the same transaction will be used
 * and will not flush and commit even after the invocation ends.
 * 
 * @author LeeKiatHaw
 */
@Stateless
public class BatchJobContainerHelper extends DBService {
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BatchJobRun insertRun(BatchJobRun job) {
        em.persist(job);
        return job;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BatchJobRun updateRun(BatchJobRun job) {
        return em.merge(job);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BatchJobRun pushToStartStatus(BatchJobRun job, DateTime startTime) {
        job = em.merge(job);
        job.start(startTime);
        
        return job;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BatchJobRunLog insertLog(BatchJobRunLog log) {
        em.persist(log);
        
        return log;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BatchJobRun logErrors(BatchJobRun job, Throwable ex) {
        BatchJobRunError newError = new BatchJobRunError(job, ex);
        em.persist(newError);
        
        job.fail(DateTime.now());
        
        return em.merge(job);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BatchJobRun pushToCompleted(BatchJobRun job, DateTime startTime) {
        job.complete(startTime);
        job = em.merge(job);
        
        return job;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BatchJobRun pushToScheduled(BatchJobRun job, DateTime scheduledTime) {
        job.schedule(scheduledTime);
        job = em.merge(job);
        
        return job;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean checkConditions(List<BatchJobCondition> conditions) {
        //Loop through and execute
        //If any condition returns false, don't schedule this batch job
        for(BatchJobCondition cond : conditions) {
            if(!cond.continueNextRun()) //Possible long run
                return false;
        }
        return true;
    }
    
    /**
     * Get the latest N job runs for batch job.
     * 
     * @param batchJobId
     * @param statuses null for retrieving all statuses
     * @param n <= 0 for retrieving all BatchJobRuns
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<BatchJobRun> getLastNBatchRuns(long batchJobId, List<BATCH_JOB_RUN_STATUS> statuses, int n) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<BatchJobRun> query = builder.createQuery(BatchJobRun.class);
        Root<BatchJobRun> fromRun = query.from(BatchJobRun.class);
        
        query.select(fromRun);
        List<Predicate> andCriteria = new ArrayList<>();
        
        andCriteria.add(builder.equal(fromRun.get(BatchJobRun_.BATCH_JOB), batchJobId));
        
        if(statuses != null && !statuses.isEmpty()) {
            List<String> statusString = new ArrayList<>();
            for(BATCH_JOB_RUN_STATUS status : statuses) {
                statusString.add(status.label);
            }
            andCriteria.add(fromRun.get(BatchJobRun_.STATUS).in(statusString));
        }
        query.where(andCriteria.toArray(new Predicate[]{}));
        
        query.orderBy(
                builder.desc(fromRun.get(BatchJobRun_.SCHEDULED_TIME)),
                builder.desc(fromRun.get(BatchJobRun_.START_TIME)),
                builder.desc(fromRun.get(BatchJobRun_.END_TIME))
                );
        TypedQuery<BatchJobRun> typedQuery = em.createQuery(query);
        if(n > 0)
            typedQuery.setMaxResults(n);
        
        List<BatchJobRun> results = typedQuery.getResultList();
        
        return results;
    }
}
