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
import eds.component.transaction.TransactionLockingException;
import eds.component.transaction.TransactionService;
import eds.entity.client.Client;
import eds.entity.client.VerifiedSendingAddress;
import eds.entity.mail.EMAIL_PROCESSING_STATUS;
import eds.entity.mail.Email;
import eds.entity.mail.Email_;
import eds.entity.mail.QueuedEmail;
import java.sql.Timestamp;
import java.util.ArrayList;
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

    final int NUM_RETRIES = 3; // default
    final String NUM_RETRIES_KEY = "NUM_RETRIES";

    @EJB
    private GenericObjectService objService;
    @EJB
    private UpdateObjectService updService;
    @EJB
    private ClientAWSService clientAWSService;
    @EJB
    private TransactionService txService;

    @Inject
    @Password
    BasicAWSCredentials awsCredentials;
    /**
     * The maximum number of emails that will be sent each time before any
     * updates to the database will be flushed/committed.
     *
     */
    public static final int UPDATE_BATCH_SIZE = 100;

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public <E extends Email> void sendEmailNow(List<E> emails, boolean logging) {
        // You will need to have AWS_ACCESS_KEY_ID and AWS_SECRET_KEY in your
        // web.xml environmental variables
        // Initiate the client resource once
        AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(awsCredentials);
        client.setEndpoint(clientAWSService.getSESEndpoint());
        for (Email queuedMail : emails) {
            // Use this to decide what to do in finally {}
            EMAIL_PROCESSING_STATUS processingResult = null;

            try {
                // Lock it so that other processes cannot touch this email
                queuedMail = txService.transitTx(queuedMail, EMAIL_PROCESSING_STATUS.SENT, DateTime.now());

                //Validate
                validateEmail(queuedMail);

                // Get the sender, subject and body from queuedMail
                String FROM_ADDRESS = queuedMail.getSENDER_ADDRESS();
                String FROM_NAME = queuedMail.getSENDER_NAME();
                String SUBJECT = queuedMail.getSUBJECT();
                String BODY = queuedMail.getBODY();
                Set<String> TO = queuedMail.getRECIPIENTS();
                String FROM = (FROM_NAME == null) ? FROM_ADDRESS : FROM_NAME + " <" + FROM_ADDRESS + ">";

                Set<String> REPLY_TO = queuedMail.getREPLY_TO_ADDRESSES();

                // Create the AWS Content, Body, Message and SendEmailRequest objects
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
                if (defaultBounceAddress != null && !defaultBounceAddress.isEmpty()) {
                    request = request.withReturnPath(defaultBounceAddress);
                }

                SendEmailResult result = client.sendEmail(request);
                String messageId = result.getMessageId();
                queuedMail.setAWS_SES_MESSAGE_ID(messageId);
                processingResult = EMAIL_PROCESSING_STATUS.SENT;

            } catch (InvalidEmailException | IllegalArgumentException ex) {
                Logger.getLogger(MailServiceOutbound.class.getName()).log(Level.SEVERE, null, ex);
                processingResult = EMAIL_PROCESSING_STATUS.ERROR;
            } catch (AmazonClientException ex) {
                Logger.getLogger(MailServiceOutbound.class.getName()).log(Level.SEVERE, null, ex);
                //Retry for NUM_RETRIES times
                int retries = Integer.parseInt(System.getProperty(NUM_RETRIES_KEY, "" + NUM_RETRIES));
                queuedMail.setRETRIES(queuedMail.getRETRIES() + 1);
                if (queuedMail.getRETRIES() >= retries) {
                    processingResult = EMAIL_PROCESSING_STATUS.ERROR;
                } else {
                    processingResult = EMAIL_PROCESSING_STATUS.QUEUED; // Re-Queue
                }
            } catch (TransactionLockingException ex) {
                // Don't do anything in this case
                Logger.getLogger(MailServiceOutbound.class.getName()).log(Level.SEVERE, null, ex);
                processingResult = EMAIL_PROCESSING_STATUS.ERROR;
                continue;

            } catch (Throwable ex) {
                Logger.getLogger(MailServiceOutbound.class.getName()).log(Level.SEVERE, null, ex);
                processingResult = EMAIL_PROCESSING_STATUS.ERROR;
                //must log an error entity here
            } finally {
                // Update sentMail again here
                // at this point of time, it could be in any status 
                try {
                    switch (processingResult) {
                        case SENT: {
                            queuedMail = (Email) updService.merge(queuedMail);
                            break;
                        }
                        default:  {
                            queuedMail = txService.transitTx(queuedMail, processingResult, DateTime.now());
                            break;
                        }
                    }
                } catch (TransactionLockingException ex) {
                    Logger.getLogger(MailServiceOutbound.class.getName()).log(Level.SEVERE, null, ex);
                    // Forget about it...
                }
            }
        }
        client.shutdown(); // Always good to close resources after use!
    }

    /**
     * Puts Email in QUEUED status with scheduleTime. If the
     * processEmailQueue(DateTime) or processEmailQueue() service methods are
     * scheduled, the queuedMail will be picked up at the soonest after
     * scheduleTime to be sent out.
     *
     * @param email
     * @param scheduledTime
     * @return the email that was persisted - to be safe
     * @throws DataValidationException if either sender or recipient queuedMail
     * is missing.
     * @throws InvalidEmailException if validateEmail(Email) throws one.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Email queueEmail(Email email, DateTime scheduledTime)
            throws DataValidationException, InvalidEmailException {
        //Validate queuedMail
        validateEmail(email);

        QueuedEmail qEmail = new QueuedEmail(email, scheduledTime);
        updService.persist(qEmail);
        
        return qEmail;
    }

    public List<QueuedEmail> getNextNEmailsInQueue(DateTime processTime, int nextNEmails) {
        Timestamp nowTS = new Timestamp(processTime.getMillis());
        // Get all queued queuedMail sorted by their DATE_CHANGED
        CriteriaBuilder builder = updService.getEm().getCriteriaBuilder();
        CriteriaQuery<QueuedEmail> query = builder.createQuery(QueuedEmail.class);
        Root<QueuedEmail> fromQ = query.from(QueuedEmail.class);

        query.select(fromQ);
        query.where(builder.and(builder.equal(fromQ.get(Email_.PROCESSING_STATUS), EMAIL_PROCESSING_STATUS.QUEUED.label),
                builder.lessThanOrEqualTo(fromQ.get(Email_.SCHEDULED_DATETIME), nowTS)
        )
        );
        query.orderBy(builder.asc(fromQ.get(Email_.DATETIME_CHANGED)));

        List<QueuedEmail> results = updService.getEm().createQuery(query)
                .setFirstResult(0)
                .setMaxResults(nextNEmails)
                .getResultList();

        return results;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void processEmailQueue(DateTime processTime) {

        List<QueuedEmail> emails = this.getNextNEmailsInQueue(processTime, UPDATE_BATCH_SIZE);

        sendEmailNow(emails, true);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void processEmailQueueNow() {
        DateTime now = DateTime.now();
        processEmailQueue(now);
    }

    /**
     *
     * @param email
     * @throws DataValidationException if queuedMail is missing
     * @throws InvalidEmailException if either Sender's queuedMail address or
     * any of the Recipients' queuedMail addresses are not valid based on
     * org.apache.commons.validator.routines.EmailValidator
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

        // Validate all queuedMail addresses before sending
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
        if (bounceClients == null || bounceClients.isEmpty()) {
            return null;
        }

        Client bounceClient = bounceClients.get(0);
        List<VerifiedSendingAddress> bounceAddresses = objService.getEnterpriseData(bounceClient.getOBJECTID(), VerifiedSendingAddress.class);
        if (bounceAddresses == null || bounceAddresses.isEmpty()) {
            return null;
        }

        VerifiedSendingAddress add = bounceAddresses.get(0);

        return add.getVERIFIED_ADDRESS();
    }

    public void sendQuickMail(
            String subject,
            String body,
            String sender,
            DateTime dt,
            boolean logging,
            String... recipients) {

        Email email = new QueuedEmail();
        email.setSUBJECT(subject);
        email.setSENDER_ADDRESS(sender);
        email.setBODY(body);
        email.setSCHEDULED_DATETIME(new java.sql.Timestamp(dt.getMillis()));

        for (String recipient : recipients) {
            email.addRecipient(recipient);
        }
        // Must persist! Since https://github.com/SegMail/SegMail/issues/180
        // as calling sendEmailNow will require a stored copy of the QueuedEmail 
        // we have created here
        email = (Email) updService.persist(email);

        // Switch to the new method
        List<Email> emails = new ArrayList<>();
        emails.add(email);
        sendEmailNow(emails, logging);
    }
}
