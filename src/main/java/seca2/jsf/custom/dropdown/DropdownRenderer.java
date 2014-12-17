/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.jsf.custom.dropdown;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

/**
 *
 * @author LeeKiatHaw
 */
@FacesRenderer(componentFamily = DropdownComponent.COMPONENT_FAMILY,
        rendererType = DropdownRenderer.RENDERER_TYPE)
public class DropdownRenderer extends Renderer {

    public static final String RENDERER_TYPE = "seca2.jsf.custom.dropdown.DropdownRenderer";

    @Override
    public void encodeBegin(FacesContext context, UIComponent uicomponent) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        DropdownComponent component = (DropdownComponent) uicomponent;
        
        //Render opening tags
        writer.startElement("div", component);
        writer.writeAttribute("id", component.getClientId(), "id");
        writer.writeAttribute("class", component.getDropdownClass(), null);
        
        //Render button appearance
        writer.startElement("button", component);
        writer.writeAttribute("class", component.getButtonClass(), null);
        writer.writeAttribute("data-toggle", "dropdown", null);
        
        //Render caret
        writer.startElement("span", component);
        writer.writeAttribute("class", "caret", null);
        writer.endElement("span");
        writer.endElement("button");
        
        //Render arrow
        writer.startElement("span", component);
        writer.writeAttribute("class", component.getArrowClass(), null);
        writer.endElement("span");
        
        //Render list
        writer.startElement("ul", component);
        writer.writeAttribute("class", component.getListClass(), null);
        
        
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent uicomponent) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        DropdownComponent component = (DropdownComponent) uicomponent;
        
        //Render empty list message
        if(this.hasNoList(component)){
            writer.startElement("li", component);
            writer.startElement("a", component);
            writer.write(component.getEmptyMessage());
            writer.endElement("a");
            writer.endElement("li");
        } else{
            super.encodeChildren(context, uicomponent); //To change body of generated methods, choose Tools | Templates.
        }
        
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent uicomponent) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        DropdownComponent component = (DropdownComponent) uicomponent;

        //close all tags
        writer.endElement("ul");
        writer.endElement("button");
        writer.endElement("div");
    }

    //Helper
    private boolean hasNoList(DropdownComponent uicomponent){
        //get list items
        List items = uicomponent.getListItems();
        
        //If list is empty, print out 
        if(items == null || items.size() <= 0){
            return true;
        }
        
        return false;
    }
}
