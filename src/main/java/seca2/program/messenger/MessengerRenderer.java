/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.messenger;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

/**
 * FacesMessage has 4 severity levels:
 * - SEVERITY_INFO
 * - SEVERITY_WARN
 * - SEVERITY_ERROR
 * - SEVERITY_FATAL
 * 
 * Our component is based on Bootstrap (Twitter's not ours), so there are 4 corresponding mappings:
 * - alert-info
 * - alert-warn
 * - alert-danger
 * - alert-success
 * 
 * If a FacesMessage can be dismissed, there is a Bootstrap class for rendering a
 * close button at the side of the message by javascript.
 * - alert-dismissable
 * 
 * If a link is required to be displayed in the error message box, a<div> with 
 * the following class can be added:
 * - alert-link
 * 
 * @author LeeKiatHaw
 */
@FacesRenderer(componentFamily = MessengerComponent.COMPONENT_FAMILY,
        rendererType = MessengerRenderer.RENDERER_TYPE)
public class MessengerRenderer extends Renderer {

    public static final String RENDERER_TYPE = "seca2.program.messenger.MessengerRenderer";

    
    @Override
    public void encodeEnd(FacesContext context, UIComponent uicomponent) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        MessengerComponent component = (MessengerComponent) uicomponent;
        
        //Get the client ID in order to retrieve all messages for the client
        String clientId = component.getClientId(context);
        String _for = component.getFor();
        
        //Retrieve all messages that were "for" this component using the client ID
        List<FacesMessage> myMessages = context.getMessageList(_for);
        
        try{
            //Start encoding the messages
            writer.startElement("div", component);
            writer.writeAttribute("id", component.getClientId(), "id");
           
            //start writing content
            for(FacesMessage message : myMessages){
                writer.startElement("div", component);
                
                /**StyleClass should be set at the end as various attributes of the FacesMessage 
                 * will determine the styleClass
                 */
                String messageStyleClass = "alert";
                
                if(message.getSeverity().equals(FacesMessage.SEVERITY_INFO)){
                    messageStyleClass.concat(" alert-info");
                } else if (message.getSeverity().equals(FacesMessage.SEVERITY_WARN)){
                    messageStyleClass.concat(" alert-warning");
                } else if (message.getSeverity().equals(FacesMessage.SEVERITY_ERROR)){
                    messageStyleClass.concat(" alert-danger");
                } else if (message.getSeverity().equals(FacesMessage.SEVERITY_FATAL)){
                    messageStyleClass.concat(" alert-success"); 
                    //this is the only exceptional case that you will find unintuitive,
                    //but the rest are ok...
                }
                
                //Check if closable, put in the Bootstrap dismissible class
                if(component.isClosable()){
                    messageStyleClass.concat(" alert-dismissible");
                }
                
                //Are we ready to set the style of the message?
                
                //How to style the links? Which attribute of FacesMessage to depend on?
                //Solution 1: Custom message class extended from FacesMessage
                //Solution 2: Scan through the FacesMessage summary and detail fields to 
                //  find the <a> tag and append its class atttribute.
                                
            }
            writer.write(component.testComponentMethod());
            writer.endElement("div");
        } catch (IOException ex){
            System.out.println("Could not generate markup");
        }
    }
    
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        super.encodeBegin(context, component); //To change body of generated methods, choose Tools | Templates.
    }
    
}
