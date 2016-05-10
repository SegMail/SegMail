package eds.component.batch;

import eds.component.UpdateObjectService;
import eds.component.data.IncompleteDataException;
import eds.entity.batch.BATCH_JOB_RUN_STATUS;
import eds.entity.batch.BatchJob;
import eds.entity.batch.BatchJobStep;
import eds.entity.batch.BatchJobStepParam;
import eds.entity.batch.BatchJob_;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import seca2.component.landing.LandingServerGenerationStrategy;
import seca2.component.landing.LandingService;
import seca2.component.landing.ServerNodeType;
import seca2.entity.landing.ServerInstance;

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

    public static final int MAX_JOBS_PER_CRON = 5;
    
    @EJB
    UpdateObjectService updateService;
    
    @EJB
    LandingService landingService;


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
    public void processBatchJobQueue(java.sql.Timestamp scheduleTime) throws IncompleteDataException{
        List<BatchJob> nextNJobs = this.getNextNJobs(MAX_JOBS_PER_CRON, scheduleTime);
        
        //Debug
        System.out.println("Processing batch job in thread "+Thread.currentThread().getId()+" at time "+scheduleTime.toString());
        //Use LandingService to get the next few ERP servers
        //Only ERP servers can process batch jobs
        for(BatchJob job : nextNJobs){
            ServerInstance server = landingService.getNextServerInstance(LandingServerGenerationStrategy.ROUND_ROBIN, ServerNodeType.ERP);
            
            try {
                //http://stackoverflow.com/questions/17276176/client-for-remote-jms-queue
                Properties env = new Properties( );
                env.put(InitialContext.PROVIDER_URL, "ldap://"+server.getHOSTNAME()+":"+server.getPORT());
                
                InitialContext jmsContext = new InitialContext(env);
                ConnectionFactory connFactory = (ConnectionFactory) jmsContext.lookup("jms/__defaultConnectionFactory");
                
                Connection conn = connFactory.createConnection();
                Session session = conn.createSession(false,Session.DUPS_OK_ACKNOWLEDGE);
                Queue queue = session.createQueue("jms/Queue1");
                MessageProducer producer = session.createProducer(null);
                
                Message msg = session.createObjectMessage(job);
                producer.send(msg);
                
            } catch (NamingException ex) {
                Logger.getLogger(BatchProcessingService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JMSException ex) {
                Logger.getLogger(BatchProcessingService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * 
     * @param n
     * @param scheduleTime Sometimes this method can be called from a remote
     * server and the time may be different
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<BatchJob> getNextNJobs(int n, java.sql.Timestamp scheduleTime){
        
        CriteriaBuilder builder = updateService.getEm().getCriteriaBuilder();
        CriteriaQuery<BatchJob> query = builder.createQuery(BatchJob.class);
        Root<BatchJob> fromBatchJob = query.from(BatchJob.class);
        
        query.select(fromBatchJob);
        query.where(builder.and(builder.equal(fromBatchJob.get(BatchJob_.STATUS),BATCH_JOB_RUN_STATUS.SCHEDULED.label))//,
                //builder.lessThanOrEqualTo(fromBatchJob.get(BatchJob_.SCHEDULED_TIME), scheduleTime)
        );
        
        //query.orderBy(builder.asc(fromBatchJob.get(BatchJob_.SCHEDULED_TIME)));
        
        List<BatchJob> results = updateService.getEm().createQuery(query)
                .setFirstResult(0)
                .setMaxResults(n)
                //.setLockMode(LockModeType.PESSIMISTIC_READ) //The BatchExecutionService should be locking it, not this
                .getResultList();
        
        return results;
                
    }

    /*
    public static void main(String[] args) throws NoSuchMethodException, ClassNotFoundException, IOException, BatchProcessingException {
        BatchProcessingService bpService = new BatchProcessingService();
        MailService mailService = new MailService();

        Email email = new Email();
        email.setTRANSACTION_ID(123);
        email.setTRANSACTION_KEY("123");

        Object[] params = {email, true};

        BatchJobStep step = bpService.createJobStep(mailService.getClass().getName(), "sendEmailByAWS", params, this);

        for (BatchJobStepParam param : step.getPARAMS()) {
            System.out.println("String value: " + param.getSTRING_VALUE());
            System.out.println("Serialized value: " + param.getSERIALIZED_OBJECT());
        }
    }*/
}
