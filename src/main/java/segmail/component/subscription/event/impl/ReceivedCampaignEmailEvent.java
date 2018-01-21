/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription.event.impl;

import eds.entity.mail.EMAIL_PROCESSING_STATUS;
import eds.entity.mail.Email;
import eds.entity.mail.SentEmail;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Table;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import segmail.component.subscription.event.SubscriberEvent;
import segmail.component.subscription.event.SubscriberEventAction;
import segmail.entity.campaign.CampaignActivity;
import segmail.entity.campaign.Trigger_Email_Activity;

/**
 *
 * @author LeeKiatHaw
 */
public class ReceivedCampaignEmailEvent implements SubscriberEvent {
    
    public final String DATE_PATTERN = "HH:mm dd.MM.yyyy";
    public final String PREVIEW_POPUP = "preview_campaign_popup";
    
    protected Trigger_Email_Activity trigger;
    protected CampaignActivity activity;
    protected SentEmail email;
    
    /**
     * For placeholder object
     */
    public ReceivedCampaignEmailEvent() {
        
    }

    public ReceivedCampaignEmailEvent(
            Trigger_Email_Activity trigger,
            CampaignActivity activity,
            SentEmail email
        ) {
        this.trigger = trigger;
        this.activity = activity;
        this.email = email;
    }
    

    @Override
    public String title() {
        if(email != null && EMAIL_PROCESSING_STATUS.SENT.equals(email.PROCESSING_STATUS()))
            return "<i class='fa fa-envelope'></i> Received campaign email";
        
        return "<i class='fa  fa-clock-o'></i> Scheduled campaign email";
    }

    @Override
    public String body() {
        return "Subject: " + activity.getACTIVITY_NAME();
    }

    @Override
    public String datetime() {
        DateTime changed = new DateTime(trigger.getDATETIME_CREATED());
        
        if(email != null && EMAIL_PROCESSING_STATUS.SENT.equals(email.PROCESSING_STATUS()))
            changed = new DateTime(email.getDATETIME_CHANGED());
        
        return changed.toString(DATE_PATTERN);
    }

    @Override
    public List<SubscriberEventAction> actions() {
        List<SubscriberEventAction> actions = new ArrayList<>();
        
        SubscriberEventAction preview = new SubscriberEventAction();
        preview.setHref(PREVIEW_POPUP);
        preview.setHtmlClass("preview-email");
        //preview.setDatamap(new HashMap<>());
        if(email != null) {
            preview.setText("<i class='fa fa-envelope'></i> Preview email sent");
            preview.getDatamap().put("email-key", email.getTRANSACTION_KEY());
        } else {
            preview.setText("<i class='fa fa-file-o'></i> Preview campaign contents");
            preview.getDatamap().put("campaign-act-id", ""+activity.getOBJECTID());
        }
        actions.add(preview);
        
        return actions;
    }

    @Override
    public String joinSQL() {
        String trigg = Trigger_Email_Activity.class.getSimpleName();
        String ca = CampaignActivity.class.getSimpleName();
        String sent = SentEmail.class.getSimpleName();
        // Little hack for the legacy email table
        String email = Email.class.getSimpleName();
        
        String triggCId = "TRIGGERING_OBJECT";
        String cId = "OBJECTID";
        String triggKey = "TRIGGERED_TRANSACTION";
        String txKey = "TRANSACTION_KEY";
        
        String sql = trigg + " trigg "
                + "LEFT JOIN " + ca + " ca "
                    + "ON trigg." + triggCId + " = ca." + cId + " "
                + "LEFT JOIN " + sent + " sent "
                    + "ON trigg." + triggKey + " = sent." + txKey + " "
                ;
        return sql;
    }

    @Override
    public String whereSQL(Object... param) {
        // criteria
        String accId = "SUBSCRIBER_ID";
        String trigg = Trigger_Email_Activity.class.getAnnotation(Table.class).name();
        
        String sql = "trigg." + accId + " = " + param[0];
        
        return sql;
    }

    @Override
    public String selectSQL() {
        String trigg = "trigg";
        String ca = "ca";
        String sent = "sent";
        // Little hack for the legacy email table
        String mail = "mail";
        
        String sql = 
                trigg + ", " 
                + ca + ", " 
                + sent + " " 
                ;
        
        return sql;
    }

    @Override
    public String datetimePattern() {
        return DATE_PATTERN;
    }

    @Override
    public String isoDatetime() {
        DateTime dt = DateTime.parse(this.datetime(),DateTimeFormat.forPattern(this.datetimePattern()));
        
        return dt.toString();
    }

    @Override
    public String eventIcon() {
        return "<i class=\"fa fa-envelope\"></i>";
    }

    @Override
    public String orderBySQL() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int[] limitSQL() {
        return new int[]{};
    }
    
    
}
