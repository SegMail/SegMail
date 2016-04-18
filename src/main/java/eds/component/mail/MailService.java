/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.mail;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.data.DBConnectionException;
import eds.component.data.IncompleteDataException;
import eds.entity.mail.EMAIL_PROCESSING_STATUS;
import eds.entity.mail.Email;
import eds.entity.mail.Email_;
import eds.entity.transaction.EnterpriseTransaction_;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.exception.GenericJDBCException;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class MailService {

    @EJB
    private GenericObjectService objectService;
    @EJB
    private UpdateObjectService updateService;
    
    @Inject @Password
    BasicAWSCredentials awsCredentials;
    /**
     * The maximum number of emails that wull be sent each time before any
     * updates to the database will be flushed/committed.
     *
     */
    public static final int UPDATE_BATCH_SIZE = 100;
    
    public static final String DEFAULT_SMTP_ENDPOINT = "email-smtp.us-east-1.amazonaws.com";
    public static final String DEFAULT_HTTPS_ENDPOINT = "email.us-east-1.amazonaws.com";

    /**
     * Sends 1 email and logs it in the database depending on the logging flag.
     *
     * @param email The data structure representing an email.
     * @param logging If logging is turned on, the email will be logged.
     * @throws eds.component.mail.InvalidEmailException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendEmailNow(Email email, boolean logging) 
            throws InvalidEmailException {
        try {
            // Get the sender, subject and body from email
            String FROM_ADDRESS = email.getSENDER_ADDRESS();
            String FROM_NAME = email.getSENDER_NAME();
            String SUBJECT = email.getSUBJECT();
            String BODY = email.getBODY();
            Set<String> TO = email.getRECIPIENTS();
            String FROM = (FROM_NAME == null) ? FROM_ADDRESS: FROM_NAME+" <"+FROM_ADDRESS+">";
            Set<String> REPLY_TO = email.getREPLY_TO_ADDRESSES();
            
            // Validate all email addresses before sending
            if(!EmailValidator.getInstance().isValid(FROM_ADDRESS))
                throw new InvalidEmailException("FROM address "+FROM_ADDRESS+" is not valid.");
            for(String to : TO) {
                if(!EmailValidator.getInstance().isValid(to))
                    throw new InvalidEmailException("TO address "+to+" is not valid.");
            }
            
            //3) Create the AWS Content, Body, Message and SendEmailRequest objects
            Content textSubject = new Content().withData(SUBJECT);
            Content textBody = new Content().withData(BODY);
            Body body = new Body().withHtml(textBody);
           
            Message message = new Message().withBody(body).withSubject(textSubject);

            Destination destination = new Destination().withToAddresses(TO);
            SendEmailRequest request = new SendEmailRequest()
                    .withSource(FROM)
                    .withReplyToAddresses(REPLY_TO)
                    .withDestination(destination)
                    .withMessage(message);

            // You will need to have AWS_ACCESS_KEY_ID and AWS_SECRET_KEY in your1111 
            // web.xml environmental variables
            AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(awsCredentials);
            client.setEndpoint(DEFAULT_HTTPS_ENDPOINT);
            
            client.sendEmail(request);
            // Log the email that was sent, if the logging flag was set
            // Log it before sending, because once sent out but something happens to this update
            // then it would not be correct.
            email.PROCESSING_STATUS(EMAIL_PROCESSING_STATUS.SENT);
            if (logging) {
                updateService.getEm().merge(email);
            }
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        }
    }

    
    public void sendEmailBySMTP(Email email) {

        try {
            //Checks
            if(email.getRECIPIENTS() == null || email.getRECIPIENTS().isEmpty())
                throw new IncompleteDataException("Emails must have a recipient.");
            if(email.getBODY() == null || email.getBODY().isEmpty())
                throw new IncompleteDataException("Emails must have a body.");
            if(email.getSUBJECT()== null || email.getSUBJECT().isEmpty())
                throw new IncompleteDataException("Emails must have a subject.");
            if(email.getSENDER_ADDRESS()== null || email.getSENDER_ADDRESS().isEmpty())
                throw new IncompleteDataException("Emails must have a sender.");
            
            // Create a Properties object to contain connection configuration information.
            Properties props = System.getProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.port", 25);

            // Set properties indicating that we want to use STARTTLS to encrypt the connection.
            // The SMTP session will begin on an unencrypted connection, and then the client
            // will issue a STARTTLS command to upgrade to an encrypted connection.
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");

            // Create a Session object to represent a mail session with the specified properties. 
            Session session = Session.getDefaultInstance(props);

            // Create a message with the specified information. 
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(email.getSENDER_ADDRESS()));
            for (String recipient : email.getRECIPIENTS()) {
                msg.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipient));
            }
            msg.setSubject(email.getSUBJECT());
            msg.setContent(email.getBODY(), "text/html");

            // Create a transport.        
            Transport transport = session.getTransport();

            // Send the message.
            System.out.println("Attempting to send an email through the Amazon SES SMTP interface...");

            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect(
                    DEFAULT_SMTP_ENDPOINT, 
                    awsCredentials.getAWSAccessKeyId(),
                    awsCredentials.getAWSSecretKey());

            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
            System.out.println("Email sent!");
        } catch (Exception ex) {
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
            throw new EJBException(ex);
        } finally {
            // Close and terminate the connection.
            //transport.close();
        }
    }

    public void createSMTPConnection() {

    }

    /**
     *
     * @param emailContent
     * @param listId
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String parseEmailContent(String emailContent, long listId) {

        //1. Parse global codes
        //2. Parse list-defined fields
        return "";
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void queueEmail(Email email) {
        email.PROCESSING_STATUS(EMAIL_PROCESSING_STATUS.QUEUED);
        updateService.getEm().persist(email);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void processEmailQueue() {
        // Get all queued email sorted by their DATE_CHANGED
        CriteriaBuilder builder = updateService.getEm().getCriteriaBuilder();
        CriteriaQuery<Email> query = builder.createQuery(Email.class);
        Root<Email> fromEmail = query.from(Email.class);
        
        query.select(fromEmail);
        query.where(builder.equal(fromEmail.get(Email_.PROCESSING_STATUS), EMAIL_PROCESSING_STATUS.QUEUED.label));
        query.orderBy(builder.asc(fromEmail.get(Email_.DATETIME_CHANGED)));
        
        List<Email> results = updateService.getEm().createQuery(query)
                .setFirstResult(0)
                .setMaxResults(UPDATE_BATCH_SIZE)
                .getResultList();
        
        for(Email email : results ) {
            try {
                this.sendEmailNow(email, true);
            } catch (InvalidEmailException ex) {
                Logger.getLogger(MailService.class.getName()).log(Level.SEVERE, null, ex);
                email.PROCESSING_STATUS(EMAIL_PROCESSING_STATUS.ERROR);
                updateService.getEm().merge(email);
            }
        }
    }
}
