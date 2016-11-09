/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.client;

import com.amazonaws.services.simpleemail.model.NotificationType;
import eds.entity.data.EnterpriseData;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="VERIFIED_SENDING_ADDRESS")
public class VerifiedSendingAddress extends EnterpriseData<Client> {
    
    private String VERIFIED_ADDRESS;
    
    private String AWS_SQS_BOUNCE_QUEUE_ARN;
    
    private String AWS_SNS_BOUNCE_TOPIC_ARN;
    
    private String AWS_SNS_BOUNCE_SUBSCRIPTION_ARN;
    
    private String AWS_SQS_COMPLAINT_QUEUE_ARN;
    
    private String AWS_SNS_COMPLAINT_TOPIC_ARN;
    
    private String AWS_SNS_COMPLAINT_SUBSCRIPTION_ARN;

    public String getVERIFIED_ADDRESS() {
        return VERIFIED_ADDRESS;
    }

    public void setVERIFIED_ADDRESS(String VERIFIED_ADDRESS) {
        this.VERIFIED_ADDRESS = VERIFIED_ADDRESS;
    }

    public String getAWS_SQS_BOUNCE_QUEUE_ARN() {
        return AWS_SQS_BOUNCE_QUEUE_ARN;
    }

    public void setAWS_SQS_BOUNCE_QUEUE_ARN(String AWS_SQS_BOUNCE_QUEUE_ARN) {
        this.AWS_SQS_BOUNCE_QUEUE_ARN = AWS_SQS_BOUNCE_QUEUE_ARN;
    }

    public String getAWS_SNS_BOUNCE_SUBSCRIPTION_ARN() {
        return AWS_SNS_BOUNCE_SUBSCRIPTION_ARN;
    }

    public void setAWS_SNS_BOUNCE_SUBSCRIPTION_ARN(String AWS_SNS_BOUNCE_SUBSCRIPTION_ARN) {
        this.AWS_SNS_BOUNCE_SUBSCRIPTION_ARN = AWS_SNS_BOUNCE_SUBSCRIPTION_ARN;
    }

    public String getAWS_SQS_COMPLAINT_QUEUE_ARN() {
        return AWS_SQS_COMPLAINT_QUEUE_ARN;
    }

    public void setAWS_SQS_COMPLAINT_QUEUE_ARN(String AWS_SQS_COMPLAINT_QUEUE_ARN) {
        this.AWS_SQS_COMPLAINT_QUEUE_ARN = AWS_SQS_COMPLAINT_QUEUE_ARN;
    }

    public String getAWS_SNS_COMPLAINT_SUBSCRIPTION_ARN() {
        return AWS_SNS_COMPLAINT_SUBSCRIPTION_ARN;
    }

    public void setAWS_SNS_COMPLAINT_SUBSCRIPTION_ARN(String AWS_SNS_COMPLAINT_SUBSCRIPTION_ARN) {
        this.AWS_SNS_COMPLAINT_SUBSCRIPTION_ARN = AWS_SNS_COMPLAINT_SUBSCRIPTION_ARN;
    }

    public String getAWS_SNS_BOUNCE_TOPIC_ARN() {
        return AWS_SNS_BOUNCE_TOPIC_ARN;
    }

    public void setAWS_SNS_BOUNCE_TOPIC_ARN(String AWS_SNS_BOUNCE_TOPIC_ARN) {
        this.AWS_SNS_BOUNCE_TOPIC_ARN = AWS_SNS_BOUNCE_TOPIC_ARN;
    }

    public String getAWS_SNS_COMPLAINT_TOPIC_ARN() {
        return AWS_SNS_COMPLAINT_TOPIC_ARN;
    }

    public void setAWS_SNS_COMPLAINT_TOPIC_ARN(String AWS_SNS_COMPLAINT_TOPIC_ARN) {
        this.AWS_SNS_COMPLAINT_TOPIC_ARN = AWS_SNS_COMPLAINT_TOPIC_ARN;
    }
    
    public String getQueueARN(NotificationType type) {
        switch(type) {
            case Bounce : return getAWS_SQS_BOUNCE_QUEUE_ARN();
            case Complaint : return getAWS_SQS_COMPLAINT_QUEUE_ARN();
            default : return "";
        }
    }
    
    public void setQueueARN(String arn, NotificationType type) {
        switch(type) {
            case Bounce : setAWS_SQS_BOUNCE_QUEUE_ARN(arn); return;
            case Complaint : setAWS_SQS_COMPLAINT_QUEUE_ARN(arn); return;
            default : return;
        }
    }
    
    public String getTopicARN(NotificationType type) {
        switch(type) {
            case Bounce : return getAWS_SNS_BOUNCE_TOPIC_ARN();
            case Complaint : return getAWS_SNS_COMPLAINT_TOPIC_ARN();
            default : return "";
        }
    }
    
    public void setTopicARN(String arn, NotificationType type) {
        switch(type) {
            case Bounce : setAWS_SNS_BOUNCE_TOPIC_ARN(arn); return;
            case Complaint : setAWS_SNS_COMPLAINT_TOPIC_ARN(arn); return;
            default : return;
        }
    }
    
    public String getSubsriptionARN(NotificationType type) {
        switch(type) {
            case Bounce : return getAWS_SNS_BOUNCE_SUBSCRIPTION_ARN();
            case Complaint : return getAWS_SNS_COMPLAINT_SUBSCRIPTION_ARN();
            default : return "";
        }
    }
    
    public void setSubsriptionARN(String arn, NotificationType type) {
        switch(type) {
            case Bounce : setAWS_SNS_BOUNCE_SUBSCRIPTION_ARN(arn);
            case Complaint : setAWS_SNS_COMPLAINT_SUBSCRIPTION_ARN(arn);
            default : return;
        }
    }

    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String HTMLName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
