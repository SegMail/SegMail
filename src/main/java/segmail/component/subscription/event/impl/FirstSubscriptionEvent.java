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
import segmail.entity.subscription.Subscription;

/**
 *
 * @author LeeKiatHaw
 */
public class FirstSubscriptionEvent implements SubscriberEvent{
    
    public final String DATE_PATTERN = "dd.MM.yyyy";
    
    public final String subscAlias = "sub";
    
    protected Subscription subscription;
    
    public FirstSubscriptionEvent(){
        
    }

    public FirstSubscriptionEvent(Subscription sub) {
        this.subscription = sub;
    }

    @Override
    public String title() {
        return "First subscription!";
    }

    @Override
    public String body() {
        return "Subscribed to <strong>" + subscription.getTARGET().getLIST_NAME()
                + "</strong> on <u>" + datetime() + "</u>";
    }

    @Override
    public String datetime() {
        DateTime created = new DateTime(subscription.getDATE_CREATED());
        
        return created.toString(DATE_PATTERN);
    }

    @Override
    public List<SubscriberEventAction> actions() {
        return new ArrayList<>();
    }

    /*
    @Override
    public Map<String, List<String>> fields() {
        
    }*/

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
    public String selectSQL() {
        String sql = subscAlias;
        
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
        return "<i class=\"fa fa-file\"></i>";
    }

    @Override
    public String orderBySQL() {
        String sql = subscAlias + ".DATE_CREATED ASC";
        
        return sql;
    }

    @Override
    public int[] limitSQL() {
        return new int[]{0,1};
    }
    
}
