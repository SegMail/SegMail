/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.messenger;

import java.util.ArrayList;
import java.util.List;
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
    public static final String LINK_TAG_START = "<a>";
    
    //A non-html tag to represent the end of the link
    public static final String LINK_TAG_END = "</?link?>";
    
    private List<LinkMarker> links = new ArrayList<LinkMarker>();
    
    public class LinkMarker{
        int start_position;
        int end_position;
        
        LinkMarker(String message, String link){
            start_position = message.length();
            end_position = start_position+link.length();
        }
        
        String printLink(String message){
            return message.substring(start_position, end_position);
        }
    }
    
    public void appendSummary(String message){
        this.setSummary(this.getSummary().concat(message));
    }
    
    public void appendDetail(String message){
        this.setDetail(this.getDetail().concat(message));
    }
    
    public void appendSummaryLink(String link){
        //Capture position of link by capturing length of current Summary
        LinkMarker newMarker = new LinkMarker(this.getSummary(),link);
        this.links.add(newMarker);
        this.appendSummary(link);
    }
    
    public void appendDetailLink(String link){
        //Capture position of link by capturing length of current Summary
        LinkMarker newMarker = new LinkMarker(this.getDetail(),link);
        this.links.add(newMarker);
        this.appendDetail(link);
    }
    
    public void clearSummary(){
        this.setSummary("");
    }
    
    public void clearDetail(){
        this.setDetail("");
    }
}
