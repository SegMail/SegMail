/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.batch;

import eds.component.DBService;
import eds.entity.batch.BATCH_JOB_RUN_STATUS;
import eds.entity.batch.BatchJobRun;
import eds.entity.batch.BatchJobRun_;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class BatchJobTransitionServiceHelper extends DBService {
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int removeRun(String key, BATCH_JOB_RUN_STATUS status) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaDelete<BatchJobRun> delCriteria = builder.createCriteriaDelete(BatchJobRun.class);
        Root<BatchJobRun> root = delCriteria.from(BatchJobRun.class);
        
        delCriteria.where(builder.and(
                builder.equal(root.get(BatchJobRun_.RUN_KEY), key),
                builder.equal(root.get(BatchJobRun_.STATUS), status.label)
        ));
        
        int result = em.createQuery(delCriteria).executeUpdate();
        
        return result;
    } 
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int removeRun2(String key, BATCH_JOB_RUN_STATUS status) {
        String sql = "DELETE FROM " + status.tableName + 
                " WHERE RUN_KEY = '" + key + "' AND STATUS = '" + status.label + "'";
        Query query = em.createNativeQuery(sql);
        int result = query.executeUpdate();
        
        return result;
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobRun reInsertRun(BatchJobRun run) {
        em.persist(run);
        
        return run;
    }
}
