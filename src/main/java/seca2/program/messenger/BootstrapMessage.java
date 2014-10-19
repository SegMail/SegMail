/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.messenger;

import javax.faces.application.FacesMessage;

/**
 * Extension of the FacesMessage class to provide the following feature:
 * - Appending to existing messages in chunks at different time instead of just 
 *   setting the entire message at once.
 * - Encoding of links.
 * 
 * @author LeeKiatHaw
 */
public class BootstrapMessage extends FacesMessage {
    
    //A non-html tag to represent the start of the link
    public static final String LINK_TAG_START = "<?link?>";
    
    //A non-html tag to represent the end of the link
    public static final String LINK_TAG_END = "</?link?>";
    
    public void appendSummary(String message){
        this.setSummary(this.getSummary().concat(message));
    }
    
    public void appendDetail(String message){
        this.setDetail(this.getDetail().concat(message));
    }
    
    public void appendSummaryLink(String link){
        this.appendSummary(LINK_TAG_START.concat(link).concat(LINK_TAG_END));
    }
    
    public void appendDetailLink(String link){
        this.appendDetail(LINK_TAG_START.concat(link).concat(LINK_TAG_END));
    }
}
