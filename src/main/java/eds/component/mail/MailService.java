/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.mail;

import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.data.DBConnectionException;
import eds.entity.mail.Email;
import javax.ejb.EJB;
import segmail.entity.subscription.connection.SMTPConnectionSES;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import org.hibernate.exception.GenericJDBCException;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class MailService {
    
    @EJB private GenericObjectService objectService;
    @EJB private UpdateObjectService updateService;
    /**
     * The maximum number of emails that wull be sent each time before any updates to 
     * the database will be flushed/committed. 
     * 
     */
    public static final int UPDATE_BATCH_SIZE = 100;
    
    /**
     * Sends 1 email and logs it in the database depending on the logging flag.
     * 
     * @param email The data structure representing an email.
     * @param conn
     * @param logging If logging is turned on, the email will be logged.
     */
    public void sendEmail(Email email, boolean logging){
        try{
            //1) Get the sender, subject and body from email
            String FROM = email.getAUTHOR().getAddress();
            String SUBJECT = email.getSUBJECT();
            String BODY = email.getBODY();
            
            //2) Create the AWS Content, Body, Message and SendEmailRequest objects
            Content textSubject = new Content().withData(SUBJECT);
            Content textBody = new Content().withData(BODY);
            Body body = new Body().withText(textBody);
            
            Message message = new Message().withBody(body).withSubject(textSubject);
            
            Destination destination = new Destination().withToAddresses(email.getRECIPIENTS());
            SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);
            
            // You will need to have AWS_ACCESS_KEY_ID and AWS_SECRET_KEY in your1111 
            // web.xml environmental variables
            AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient();
            
            // There is no way for me to set the region!
            client.sendEmail(request);
            
            //3) Log the email that was sent, if the logging flag was set
            if(!logging)
                return;
            
            updateService.getEm().persist(email);
            
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } catch (Exception ex) {
            throw new EJBException(ex);
        }
    }
    
    public void createSMTPConnection(){
        
    }
    
    /**
     * 
     * @param emailContent
     * @param listId
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String parseEmailContent(String emailContent, long listId){
        
        //1. Parse global codes
        
        //2. Parse list-defined fields
        
        return "";
    }
    
}
