/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.batch;

import eds.component.UpdateObjectService;
import eds.component.data.EntityNotFoundException;
import static eds.entity.batch.BATCH_JOB_STATUS.SCHEDULED;
import eds.entity.batch.BatchJob;
import eds.entity.batch.BatchJobStep;
import eds.entity.batch.BatchJobStepParam;
import java.io.IOException;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import seca2.component.landing.LandingService;
import seca2.entity.landing.ServerInstance;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class BatchSchedulingService {

    @EJB
    UpdateObjectService updateService;
    
    @EJB
    LandingService landingService;
    /**
     * Schedule a single step job and assign it to a new or existing batch job.
     *
     * @param serviceName Full class name, with package, of the EJB class eg.
     * eds.component.data.ObjectService
     * @param serviceMethod
     * @param params
     * @param batchJobId if found, step will be added to the existing batch job,
     * else a new batch job will be created.
     * @param serverId
     * @return 
     * @throws eds.component.batch.BatchProcesingException 
     * @throws eds.component.data.EntityNotFoundException 
     */
    public BatchJobStep createJobStep(String serviceName, String serviceMethod, Object[] params, long batchJobId, long serverId) 
            throws BatchProcesingException, EntityNotFoundException {
        try {
            ServerInstance server = landingService.getServerInstance(serverId);
            if(server == null)
                throw new EntityNotFoundException(ServerInstance.class,serverId);
            
            BatchJob newBatchJob = updateService.getEm().find(BatchJob.class, batchJobId);
            if (newBatchJob == null) {
                newBatchJob = new BatchJob();
                newBatchJob.setSTATUS(SCHEDULED);
            }
            newBatchJob.setSERVER(server);
            updateService.getEm().persist(newBatchJob);
            
            BatchJobStep newBatchJobStep = new BatchJobStep();
            newBatchJobStep.setBATCH_JOB(newBatchJob);
            newBatchJobStep.setSERVICE_NAME(serviceName);
            newBatchJobStep.setSERVICE_METHOD(serviceMethod);
            updateService.getEm().persist(newBatchJobStep);
            
            for (int i = 0; params != null && i < params.length; i++) {
                
                BatchJobStepParam newParam = new BatchJobStepParam();
                newParam.setSNO(i);
                newParam.setBATCH_JOB_STEP(newBatchJobStep);
                updateService.getEm().persist(newParam);
                
                newBatchJobStep.addPARAMS(newParam);
                
                Object obj = params[i];
                Class clazz = obj.getClass();
                if (Serializable.class.isAssignableFrom(clazz)) {
                    Serializable s = (Serializable) obj;
                    newParam.setSERIALIZED_OBJECT(s);
                    continue;
                }
                newParam.setSTRING_VALUE(obj.toString());
            }
            
            newBatchJob.addSTEP(newBatchJobStep);
            
            updateService.getEm().flush();
            return newBatchJobStep;
            
        } catch (SecurityException ex) {
            throw new BatchProcesingException("Batch processing failed:", ex);
        } catch (IOException ex) {
            throw new BatchProcesingException("Batch processing failed:", ex);
        }
    }
    
}
