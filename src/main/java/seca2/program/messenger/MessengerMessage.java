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
    private final String detailTags = "";
    private final String linkTags = "a";
    private final String linkStyleClass = "alert-link";
    private List<LinkMarker> linksSummary = new ArrayList<LinkMarker>();
    private List<LinkMarker> linksDetail = new ArrayList<LinkMarker>();

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

    public void appendSummary(String message) {
        this.setSummary(this.getSummary().concat(message));
    }

    public void appendDetail(String message) {
        this.setDetail(this.getDetail().concat(message));
    }

    public void appendSummaryLink(String link, String href, String target) {
        //Capture position of link by capturing length of current Summary
        LinkMarker newMarker = new LinkMarker(this.getSummary(), link, target, href);
        this.linksSummary.add(newMarker);
        this.appendSummary(link);
    }

    public void appendDetailLink(String link, String href, String target) {
        //Capture position of link by capturing length of current Summary
        LinkMarker newMarker = new LinkMarker(this.getDetail(), link, href, target);
        this.linksDetail.add(newMarker);
        this.appendDetail(link);
    }

    public void clearSummary() {
        this.setSummary("");
    }

    public void clearDetail() {
        this.setDetail("");
    }

    public void encodeMessage(FacesContext context, UIComponent uicomponent) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        MessengerComponent component = (MessengerComponent) uicomponent;

        //Encode summary first
        if (component.isShowSummary()) {
            writer.startElement(this.summaryTags, component);
            Iterator<LinkMarker> iSummary = linksSummary.iterator();
            int startPositionSummary = 0;
            while (iSummary.hasNext()) {
                LinkMarker linkMarker = iSummary.next();

                //write since start_position to the start of the next linkMarker
                if (linkMarker.start_position > startPositionSummary) //only if marker start is at least 1 char away from startPosition
                {
                    writer.write(this.getDetail().substring(startPositionSummary,
                            linkMarker.start_position - 1));
                }
                //write the link
                writer.startElement(linkTags, component);
                writer.writeAttribute("class", this.linkStyleClass, null);
                writer.writeAttribute("href", linkMarker.href, null);
                writer.writeAttribute("target", linkMarker.target, null);
                writer.write(linkMarker.printLink(this.getDetail()));
                writer.endElement(linkTags);

                //move the cursor position
                startPositionSummary = linkMarker.end_position + 1;
            }
            if (startPositionSummary < this.getDetail().length()) //if we are at least 1 char away from the end
            {
                writer.write(this.getDetail().substring(startPositionSummary));//finish off the last chunk
            }
            writer.endElement(this.summaryTags);

        }

        //Encode details
        if (component.isShowDetail()) {
            writer.startElement(this.detailTags, component);
            Iterator<LinkMarker> iDetails = linksDetail.iterator();
            int startPositionDetail = 0;
            while (iDetails.hasNext()) {
                LinkMarker linkMarker = iDetails.next();

                //write since start_position to the start of the next linkMarker
                if (linkMarker.start_position > startPositionDetail) //only if marker start is at least 1 char away from startPosition
                {
                    writer.write(this.getDetail().substring(startPositionDetail,
                            linkMarker.start_position - 1));
                }
                //write the link
                writer.startElement(linkTags, component);
                writer.writeAttribute("class", this.linkStyleClass, null);
                writer.writeAttribute("href", linkMarker.href, null);
                writer.writeAttribute("target", linkMarker.target, null);
                writer.write(linkMarker.printLink(this.getDetail()));
                writer.endElement(linkTags);

                //move the cursor position
                startPositionDetail = linkMarker.end_position + 1;
            }
            if (startPositionDetail < this.getDetail().length()) //if we are at least 1 char away from the end
            {
                writer.write(this.getDetail().substring(startPositionDetail));//finish off the last chunk
            }
            writer.endElement(this.detailTags);
        }

    }
}
