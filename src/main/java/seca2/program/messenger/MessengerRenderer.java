/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.messenger;

import java.io.IOException;
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
        
        
        try{
            writer.startElement("div", component);
            writer.writeAttribute("id", component.getClientId(), "id");
           
            //start writing content
            if(component.isClosable()){
                
                writer.write("This component is closable");
            }
            else{
                writer.startElement("div", component);
                writer.write("This component is NOT closable");
                writer.endElement("div");
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
