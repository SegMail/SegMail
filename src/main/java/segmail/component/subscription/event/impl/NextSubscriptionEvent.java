/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription.event.impl;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import segmail.component.subscription.event.SubscriberEvent;
import segmail.component.subscription.event.SubscriberEventAction;
import segmail.entity.subscription.SUBSCRIPTION_STATUS;
import segmail.entity.subscription.Subscription;

/**
 *
 * @author LeeKiatHaw
 */
public class NextSubscriptionEvent implements SubscriberEvent {
    
    public final String DATE_PATTERN = "dd.MM.yyyy";
    
    public final String subscAlias = "sub";
    
    protected Subscription sub;
    
    public NextSubscriptionEvent(){
        
    }

    public NextSubscriptionEvent(Subscription sub) {
        this.sub = sub;
    }

    @Override
    public String title() {
        return "Subscribed to " + sub.getTARGET().getLIST_NAME();
    }

    @Override
    public String body() {
        if(SUBSCRIPTION_STATUS.NEW.equals(sub.STATUS())) {
            return "Awaiting confirmation...";
        }
        if(SUBSCRIPTION_STATUS.CONFIRMED.equals(sub.STATUS())) {
            DateTime dt = new DateTime(sub.getDATE_CHANGED());
            
            return "Confirmed on " + dt.toString(DATE_PATTERN);
        }
        if(SUBSCRIPTION_STATUS.REMOVED.equals(sub.STATUS())) {
            DateTime dt = new DateTime(sub.getDATE_CHANGED());
            
            return "Removed on " + dt.toString(DATE_PATTERN);
        }
        if(SUBSCRIPTION_STATUS.UNSUBSCRIBED.equals(sub.STATUS())) {
            DateTime dt = new DateTime(sub.getDATE_CHANGED());
            
            return "Unsubscribed on " + dt.toString(DATE_PATTERN);
        }
        if(SUBSCRIPTION_STATUS.BOUNCED.equals(sub.STATUS())) {
            DateTime dt = new DateTime(sub.getDATE_CHANGED());
            
            return "Bounced on " + dt.toString(DATE_PATTERN);
        }
        
        return "";// default
    }

    @Override
    public String datetime() {
        DateTime dt = new DateTime(sub.getDATE_CREATED());
        
        return dt.toString(DATE_PATTERN);
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
        return "<i class=\"fa fa-check\"></i>";
    }

    @Override
    public List<SubscriberEventAction> actions() {
        List<SubscriberEventAction> actions = new ArrayList<>();
        
        if(SUBSCRIPTION_STATUS.NEW.equals(sub.STATUS())) {
            // Resend confirmation email
            SubscriberEventAction resendAction = new SubscriberEventAction();
            resendAction.setHref("resend_confirmation_popup");
            resendAction.setHtmlClass("resend-email");
            resendAction.setText("<i class='fa fa-envelope'></i> Resend Confirmation");
            
            resendAction.getDatamap().put("source-id", ""+sub.getSOURCE().getOBJECTID());
            resendAction.getDatamap().put("target-id", ""+sub.getTARGET().getOBJECTID());
            resendAction.getDatamap().put("sent", ""+sub.getNUM_CONFIRM_SENT());
            
            actions.add(resendAction);
        }
        if(SUBSCRIPTION_STATUS.CONFIRMED.equals(sub.STATUS())) {
            // Allow remove
            SubscriberEventAction removeAction = new SubscriberEventAction();
            removeAction.setHref("remove_subscription_popup");
            removeAction.setHtmlClass("remove-sub");
            removeAction.setText("<i class='fa fa-trash-o'></i> Remove");
            //removeAction.setDatamap(new HashMap<>());
            removeAction.getDatamap().put("unsubkey", ""+sub.getUNSUBSCRIBE_KEY());
            removeAction.getDatamap().put("list-name", ""+sub.getTARGET().getLIST_NAME());
            
            actions.add(removeAction);
        }
        if(SUBSCRIPTION_STATUS.REMOVED.equals(sub.STATUS())) {
            // Allow restore
            SubscriberEventAction restoreAction = new SubscriberEventAction();
            restoreAction.setHref("restore_subscription_popup");
            restoreAction.setHtmlClass("restore-sub");
            restoreAction.setText("<i class='fa fa-reply'></i> Restore");
            //restoreAction.setDatamap(new HashMap<>());
            restoreAction.getDatamap().put("unsubkey", ""+sub.getUNSUBSCRIBE_KEY());
            restoreAction.getDatamap().put("list-name", ""+sub.getTARGET().getLIST_NAME());
            
            actions.add(restoreAction);
        }
        if(SUBSCRIPTION_STATUS.UNSUBSCRIBED.equals(sub.STATUS())) {
            // No action available after subscriber has unsubscribed
        }
        if(SUBSCRIPTION_STATUS.BOUNCED.equals(sub.STATUS())) {
            // No action available after subscriber has bounced
        }
        return actions;
    }

    @Override
    public String selectSQL() {
        String sql = subscAlias;
        
        return sql;
    }

    @Override
    public String joinSQL() {
        String subc = Subscription.class.getSimpleName();
        
        return subc + " " + subscAlias;
    }

    @Override
    public String whereSQL(Object... params) {
        long sourceId = (long) params[0];
        
        String sql = subscAlias + ".SOURCE = " + sourceId;
        
        return sql;
    }

    @Override
    public String orderBySQL() {
        String sql = subscAlias + ".DATE_CREATED ASC";
        
        return sql;
    }

    @Override
    public int[] limitSQL() {
        return new int[]{1};
    }
    
}
