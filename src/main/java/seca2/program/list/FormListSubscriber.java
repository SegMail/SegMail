/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.list;

import eds.entity.subscription.SubscriberAccount;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author LeeKiatHaw
 */
@Named("FormListSubscriber")
@RequestScoped
public class FormListSubscriber {
    @Inject private ProgramList programList;
    
    private boolean removed;
    
    private SubscriberAccount subscriber;
    
    @PostConstruct
    public void init(){
        //Can we use flash scope here?
        subscriber = new SubscriberAccount();
    }
    
    public void testFilter(AjaxBehaviorEvent e){
        
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }
    
    public void addSubscriber(){
        
    }

    public SubscriberAccount getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(SubscriberAccount subscriber) {
        this.subscriber = subscriber;
    }
    
    
}
