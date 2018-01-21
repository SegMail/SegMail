/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.component.subscription.event;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author LeeKiatHaw
 */
public class SubscriberEventAction {
    
    protected String href;
    
    protected String text;
    
    protected String htmlClass;
    
    protected Map<String,String> datamap;

    public SubscriberEventAction() {
        datamap = new HashMap<>();
        // Mandatory for all
        datamap.put("toggle", "modal");
    }
    
    

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Map<String, String> getDatamap() {
        return datamap;
    }

    /*
    public void setDatamap(Map<String, String> datamap) {
        this.datamap = datamap;
    }
    */

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHtmlClass() {
        return htmlClass;
    }

    public void setHtmlClass(String htmlClass) {
        this.htmlClass = htmlClass;
    }
    
    
}
