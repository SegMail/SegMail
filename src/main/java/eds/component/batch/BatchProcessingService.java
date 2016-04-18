package eds.component.batch;

import eds.component.UpdateObjectService;
import eds.component.mail.MailService;
import eds.entity.batch.BatchJob;
import eds.entity.batch.BatchJobStep;
import eds.entity.batch.BatchJobStepParam;
import eds.entity.mail.Email;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class BatchProcessingService {

    @EJB
    UpdateObjectService updateService;

    /**
     * Schedule a single step job.
     *
     * @param serviceName Full class name, with package, of the EJB class eg.
     * eds.component.data.ObjectService
     * @param serviceMethod
     * @param params
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BatchJobStep createJobStep(String serviceName, String serviceMethod, Object[] params)
            throws BatchProcesingException {

        try {
            BatchJob newBatchJob = new BatchJob();
            updateService.getEm().persist(newBatchJob);

            BatchJobStep newBatchJobStep = new BatchJobStep();
            newBatchJobStep.setBATCH_JOB(newBatchJob);
            newBatchJobStep.setSERVICE_NAME(serviceName);
            newBatchJobStep.setSERVICE_METHOD(serviceMethod);
            updateService.getEm().persist(newBatchJobStep);

            //Do a basic level checking of the parameters by using reflection
            //Use the POJO class, no EJB methods
            Class[] parameterTypes = new Class[params.length];
            for (int i = 0; i < params.length; i++) {
                parameterTypes[i] = params[i].getClass();
            }

            //Method method = Class.forName(serviceName).getMethod(serviceName, parameterTypes);
            for (int i = 0; i < params.length; i++) {
                BatchJobStepParam newParam = new BatchJobStepParam();
                newParam.setSNO(i);
                newParam.setBATCH_JOB_STEP(newBatchJobStep);
                updateService.getEm().persist(newParam);

                newBatchJobStep.addPARAMS(newParam);

                Object obj = params[i];
                Class clazz = obj.getClass();
                //If parameter is serializable, serialize it and store it
                if (Serializable.class.isAssignableFrom(clazz)) {
                    Serializable s = (Serializable) obj;

                    newParam.setSERIALIZED_OBJECT(s);
                    continue;
                }
                newParam.setSTRING_VALUE(obj.toString());
            }
            newBatchJob.addSTEP(newBatchJobStep);

            updateService.getEm().flush();//Just to test rollback

            return newBatchJobStep;
        } catch (SecurityException ex) {
            throw new BatchProcesingException("Batch processing failed:", ex);
        } catch (IOException ex) {
            throw new BatchProcesingException("Batch processing failed:", ex);
        }
    }

    public void executeJobStep(BatchJobStep batchJobStep) throws BatchProcesingException {
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
            
            Object ejb = InitialContext.doLookup("java:module/"+batchJobStep.getSERVICE_NAME());
            System.out.println(ejb.getClass().getName());
            
            Method[] methodArray = ejb.getClass().getMethods();
            for(int i=0; i<methodArray.length; i++){
                if(batchJobStep.getSERVICE_METHOD().equals(methodArray[i].getName())){
                    Method method = methodArray[i];
                    method.invoke(ejb, params);
                }
            }
            
        } catch (ClassNotFoundException ex) {
            throw new BatchProcesingException("Batch processing failed:", ex);
        } catch (IOException ex) {
            throw new BatchProcesingException("Batch processing failed:", ex);
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

    public static void main(String[] args) throws NoSuchMethodException, ClassNotFoundException, IOException, BatchProcesingException {
        BatchProcessingService bpService = new BatchProcessingService();
        MailService mailService = new MailService();

        Email email = new Email();
        email.setTRANSACTION_ID(123);
        email.setTRANSACTION_KEY("123");

        Object[] params = {email, true};

        BatchJobStep step = bpService.createJobStep(mailService.getClass().getName(), "sendEmailByAWS", params);

        for (BatchJobStepParam param : step.getPARAMS()) {
            System.out.println("String value: " + param.getSTRING_VALUE());
            System.out.println("Serialized value: " + param.getSERIALIZED_OBJECT());
        }
    }
}
