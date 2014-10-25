/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.messenger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 * Extension of the FacesMessage class to provide the following feature: -
 * Appending to existing messages in chunks at different time instead of just
 * setting the entire message at once. - Encoding of links.
 *
 * @author LeeKiatHaw
 */
public class MessengerMessage extends FacesMessage {

    private final String summaryTags = "strong";
    private final String summaryTagStyleClass = "";
    private final String detailTags = "";
    private final String detailTagStyleClass = "";
    private final String linkTags = "a";
    private final String linkStyleClass = "alert-link";
    private List<LinkMarker> linksSummary = new ArrayList<LinkMarker>();
    private List<LinkMarker> linksDetail = new ArrayList<LinkMarker>();
    
    public static enum LINK_TARGET{
        
    }

    private class LinkMarker {

        private int start_position;
        private int end_position;
        private String target;
        private String href;

        LinkMarker(String message, String link, String href, String target) {
            this.start_position = message.length();
            this.end_position = start_position + link.length();
            this.target = target;
            this.href = href;
        }

        String printLink(String message) {
            return message.substring(start_position, end_position);
        }
    }

    public MessengerMessage appendSummary(String message) {
        this.setSummary(this.getSummary()== null ? message : this.getSummary().concat(message));
        return this;
    }

    public MessengerMessage appendDetail(String message) {
        this.setDetail(this.getDetail()== null ? message : this.getDetail().concat(message));
        return this;
    }

    public MessengerMessage appendSummaryLink(String link, String href, String target) {
        //Capture position of link by capturing length of current Summary
        LinkMarker newMarker = new LinkMarker(this.getSummary(), link, target, href);
        this.linksSummary.add(newMarker);
        this.appendSummary(link);
        return this;
    }

    public MessengerMessage appendDetailLink(String link, String href, String target) {
        //Capture position of link by capturing length of current Summary
        LinkMarker newMarker = new LinkMarker(this.getDetail(), link, href, target);
        this.linksDetail.add(newMarker);
        this.appendDetail(link);
        return this;
    }

    public MessengerMessage clearSummary() {
        this.setSummary("");
        return this;
    }

    public MessengerMessage clearDetail() {
        this.setDetail("");
        return this;
    }

    public void encodeMessage(FacesContext context, UIComponent uicomponent) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        MessengerComponent component = (MessengerComponent) uicomponent;

        //Encode summary first
        if (component.isShowSummary()) {
            this.encodeMessageWithLink(context, component,
                    this.getSummary(), linksSummary, 
                    this.summaryTags, this.summaryTagStyleClass, 
                    this.linkTags, this.linkStyleClass);

        }

        //Encode details
        if (component.isShowDetail()) { //if there is no detail, this.getDetail() will return summary instead. WTF.
            this.encodeMessageWithLink(context, component,
                    this.getDetail(), this.linksDetail, 
                    this.detailTags, this.detailTagStyleClass, 
                    this.linkTags, this.linkStyleClass);
        }

    }
    
    private void encodeMessageWithLink(FacesContext context, UIComponent component, 
            String message, List<LinkMarker> linkMarkers,
            String messageTag, String messageStyleClass, 
            String linkTag, String linkStyleClass) throws IOException{
        
        ResponseWriter writer = context.getResponseWriter();
        if(messageTag != null && !messageTag.isEmpty())
            writer.startElement(messageTag, component);
        
        Iterator<LinkMarker> iLinks = linkMarkers.iterator();
        int startPositionDetail = 0;
        while (iLinks.hasNext()) {
            LinkMarker linkMarker = iLinks.next();

            //write since start_position to the start of the next linkMarker
            if (linkMarker.start_position > startPositionDetail) //only if marker start is at least 1 char away from startPosition
            {
                writer.write(message.substring(startPositionDetail,
                        linkMarker.start_position - 1));
            }
            //write the link
            if(linkTag != null && !linkTag.isEmpty()){
                writer.startElement(linkTag, component);
                writer.writeAttribute("class", linkStyleClass, null);
                writer.writeAttribute("href", linkMarker.href, null);
                writer.writeAttribute("target", linkMarker.target, null);
            }
            
            writer.write(linkMarker.printLink(message));
            
            if(linkTag != null && !linkTag.isEmpty()){
                writer.endElement(linkTag);
            }
            //move the cursor position
            startPositionDetail = linkMarker.end_position + 1;
        }
        if (startPositionDetail < message.length()) //if we are at least 1 char away from the end
        {
            writer.write(message.substring(startPositionDetail));//finish off the last chunk
        }
        if(messageTag != null && !messageTag.isEmpty())
            writer.endElement(messageTag);
    }
}
