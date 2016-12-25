/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.mail;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import eds.component.GenericObjectService;
import eds.component.UpdateObjectService;
import eds.component.client.ClientAWSService;
import eds.component.data.DataValidationException;
import eds.entity.client.Client;
import eds.entity.client.VerifiedSendingAddress;
import eds.entity.mail.EMAIL_PROCESSING_STATUS;
import eds.entity.mail.Email;
import eds.entity.mail.Email_;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.commons.validator.routines.EmailValidator;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class MailServiceOutbound {

    @EJB
    private GenericObjectService objService;
    @EJB
    private UpdateObjectService updateService;
    @EJB
    private ClientAWSService clientAWSService;

    @Inject
    @Password
    BasicAWSCredentials awsCredentials;
    /**
     * The maximum number of emails that wull be sent each time before any
     * updates to the database will be flushed/committed.
     *
     */
    public static final int UPDATE_BATCH_SIZE = 100;

    /**
     * Sends 1 email and logs it in the database depending on the logging flag.
     *
     * @param email The data structure representing an email.
     * @param logging If logging is turned on, the email will be logged.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void sendEmailNow(Email email, boolean logging) {
        try {
            //Validate
            validateEmail(email);
            /**
             * Update the status first, because there is a higher chance of
             * error during sending than during updating, hence by updating
             * first, we ensure that a sending error will rollback the
             * transaction and leave the status as QUEUE rather than
             * successfully send out an email and leave the status as QUEUE
             * because of a JPA update error.
             */
            if (logging) {
                email.PROCESSING_STATUS(EMAIL_PROCESSING_STATUS.SENT);
                updateService.getEm().merge(email);
                updateService.getEm().flush();
            }
            
            // Get the sender, subject and body from email
            String FROM_ADDRESS = email.getSENDER_ADDRESS();
            String FROM_NAME = email.getSENDER_NAME();
            String SUBJECT = email.getSUBJECT();
            String BODY = email.getBODY();
            Set<String> TO = email.getRECIPIENTS();
            String FROM = (FROM_NAME == null) ? FROM_ADDRESS : FROM_NAME + " <" + FROM_ADDRESS + ">";
            
            email.addReplyTo(email.getSENDER_ADDRESS());
            Set<String> REPLY_TO = email.getREPLY_TO_ADDRESSES();

            // Validate all email addresses before sending
            /*if (!EmailValidator.getInstance().isValid(FROM_ADDRESS)) {
                throw new InvalidEmailException("FROM address " + FROM_ADDRESS + " is not valid.");
            }
            for (String to : TO) {
                if (!EmailValidator.getInstance().isValid(to)) {
                    throw new InvalidEmailException("TO address " + to + " is not valid.");
                }
            }*/

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
            
            String defaultBounceAddress = getDefaultBounceAddress();
            if(defaultBounceAddress != null && !defaultBounceAddress.isEmpty()) {
                request = request.withReturnPath(defaultBounceAddress);
            }
            
            // You will need to have AWS_ACCESS_KEY_ID and AWS_SECRET_KEY in your1111 
            // web.xml environmental variables
            AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(awsCredentials);
            client.setEndpoint(clientAWSService.getSESEndpoint());

            SendEmailResult result = client.sendEmail(request);
            String messageId = result.getMessageId();
            email.setAWS_SES_MESSAGE_ID(messageId);

        } catch (InvalidEmailException | IllegalArgumentException ex) {
            Logger.getLogger(MailServiceOutbound.class.getName()).log(Level.SEVERE, null, ex);
            email.PROCESSING_STATUS(EMAIL_PROCESSING_STATUS.ERROR);
        } catch (AmazonClientException ex) {
            Logger.getLogger(MailServiceOutbound.class.getName()).log(Level.SEVERE, null, ex);
            //Retry
            email.PROCESSING_STATUS(EMAIL_PROCESSING_STATUS.QUEUED);
            email.setRETRIES(email.getRETRIES() + 1);
        } catch (Throwable ex) {
            Logger.getLogger(MailServiceOutbound.class.getName()).log(Level.SEVERE, null, ex);
            email.PROCESSING_STATUS(EMAIL_PROCESSING_STATUS.ERROR);
            //must log an error entity here
        } finally {
            if (logging) {
                updateService.getEm().merge(email);
                updateService.getEm().flush(); //Redundant
            }
        }
    }

    
    /**
     *
     * @param emailContent
     * @param listId
     * @return
     */
    
    public String parseEmailContent(String emailContent, long listId) {

        //1. Parse global codes
        //2. Parse list-defined fields
        return "";
    }

    /**
     * Puts Email in QUEUED status with scheduleTime. If the processEmailQueue(DateTime) 
     * or processEmailQueue() service methods are scheduled, the email will be 
     * picked up at the soonest after scheduleTime to be sent out.
     * 
     * @param email
     * @param scheduledTime
     * @throws DataValidationException if either sender or recipient email is missing.
     * @throws InvalidEmailException if validateEmail(Email) throws one.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void queueEmail(Email email, DateTime scheduledTime) 
            throws DataValidationException, InvalidEmailException {
        //Validate email
        validateEmail(email);

        email.PROCESSING_STATUS(EMAIL_PROCESSING_STATUS.QUEUED);
        email.setSCHEDULED_DATETIME(new Timestamp(scheduledTime.getMillis()));
        updateService.getEm().persist(email);
    }

    
    public List<Email> getNextNEmailsInQueue(DateTime processTime, int nextNEmails) {
        Timestamp nowTS = new Timestamp(processTime.getMillis());
        // Get all queued email sorted by their DATE_CHANGED
        CriteriaBuilder builder = updateService.getEm().getCriteriaBuilder();
        CriteriaQuery<Email> query = builder.createQuery(Email.class);
        Root<Email> fromEmail = query.from(Email.class);

        query.select(fromEmail);
        query.where(
                builder.and(
                        builder.equal(fromEmail.get(Email_.PROCESSING_STATUS), EMAIL_PROCESSING_STATUS.QUEUED.label),
                        builder.lessThanOrEqualTo(fromEmail.get(Email_.SCHEDULED_DATETIME), nowTS)
                )
        );
        query.orderBy(builder.asc(fromEmail.get(Email_.DATETIME_CHANGED)));

        List<Email> results = updateService.getEm().createQuery(query)
                .setFirstResult(0)
                .setMaxResults(nextNEmails)
                .getResultList();

        return results;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void processEmailQueue(DateTime processTime) {

        List<Email> emails = this.getNextNEmailsInQueue(processTime, UPDATE_BATCH_SIZE);

        for (Email email : emails) {
            sendEmailNow(email, true);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void processEmailQueueNow() {
        DateTime now = DateTime.now();
        processEmailQueue(now);
    }
    
    /**
     * 
     * @param email
     * @throws DataValidationException if email is missing
     * @throws InvalidEmailException if either Sender's email address or any of
     * the Recipients' email addresses are not valid based on org.apache.commons.validator.routines.EmailValidator
     */
    public void validateEmail(Email email) throws DataValidationException, InvalidEmailException {
        if (email.getSENDER_ADDRESS() == null || email.getSENDER_ADDRESS().isEmpty()) {
            throw new DataValidationException("Emails must have sender's address.");
        }

        //If sender's name is not set, copy the sender's address over
        if (email.getSENDER_NAME() == null || email.getSENDER_NAME().isEmpty()) {
            email.setSENDER_NAME(email.getSENDER_ADDRESS());
        }

        if (email.getRECIPIENTS() == null || email.getRECIPIENTS().isEmpty()) {
            throw new DataValidationException("Emails must have at least 1 recipient.");
        }

        // Validate all email addresses before sending
        if (!EmailValidator.getInstance().isValid(email.getSENDER_ADDRESS())) {
            throw new InvalidEmailException("Sender's address " + email.getSENDER_ADDRESS() + " is not valid.");
        }
        for (String to : email.getRECIPIENTS()) {
            if (!EmailValidator.getInstance().isValid(to)) {
                throw new InvalidEmailException("TO address " + to + " is not valid.");
            }
        }
    }
    
    public String getDefaultBounceAddress() {
        List<Client> bounceClients = objService.getEnterpriseObjectsByName("bounce", Client.class);
        if(bounceClients == null || bounceClients.isEmpty()) {
            return null;
        }
        
        Client bounceClient = bounceClients.get(0);
        List<VerifiedSendingAddress> bounceAddresses = objService.getEnterpriseData(bounceClient.getOBJECTID(), VerifiedSendingAddress.class);
        if(bounceAddresses == null || bounceAddresses.isEmpty()) {
            return null;
        }
        
        VerifiedSendingAddress add = bounceAddresses.get(0);
        
        return add.getVERIFIED_ADDRESS();
    }
}
