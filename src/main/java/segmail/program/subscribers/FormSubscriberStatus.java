/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.program.subscribers;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import segmail.entity.subscription.SUBSCRIBER_STATUS;

/**
 *
 * @author LeeKiatHaw
 */
@RequestScoped
@Named("FormSubscriberStatus")
public class FormSubscriberStatus {
    
    @Inject ProgramSubscribers program;
    @Inject FormSubscriberTable formSubscriberTable;
    
    public SUBSCRIBER_STATUS[] getAllStatuses() {
        return program.getAllStatuses();
    }

    public List<String> getSubscriberStatus() {
        return program.getSubscriberStatus();
    }

    public void setSubscriberStatus(List<String> subscriberStatus) {
        program.setSubscriberStatus(subscriberStatus);
    }
    
    public List<SUBSCRIBER_STATUS> getConvertedSubscriberStatus() {
        return program.getConvertedSubscriberStatus();
    }

    public void setConvertedSubscriberStatus(List<SUBSCRIBER_STATUS> convertedSubscriberStatus) {
        program.setConvertedSubscriberStatus(convertedSubscriberStatus);
    }
    
    public void statusUpdate() {
        setConvertedSubscriberStatus(new ArrayList<SUBSCRIBER_STATUS>());
        for(String name : getSubscriberStatus()) {
            SUBSCRIBER_STATUS status = SUBSCRIBER_STATUS.valueOf(name);
            getConvertedSubscriberStatus().add(status);
        }
        formSubscriberTable.loadPage(1);
    }
}
