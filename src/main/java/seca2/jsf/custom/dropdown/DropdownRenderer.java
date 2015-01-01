/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.jsf.custom.dropdown;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.SelectItem;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;
import org.primefaces.renderkit.SelectOneRenderer;

/**
 *
 * @author LeeKiatHaw
 */
@FacesRenderer(componentFamily = DropdownComponent.COMPONENT_FAMILY,
        rendererType = DropdownRenderer.RENDERER_TYPE)
public class DropdownRenderer extends SelectOneRenderer {

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
        writer.writeAttribute("class", component.getButtonClass() + " btn-"+component.getButtonColor(), null);
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
        
        this.encodeChildren(context, uicomponent);
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent uicomponent) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        DropdownComponent component = (DropdownComponent) uicomponent;
        
        //This is to retrieve all child components declared in the xhtml page
        List<SelectItem> listItems = this.getSelectItems(context, component);
        //This is to retrieve the value that has been tagged to this component, usually a List object
        Object values = this.getValues(component);
        Object submittedValues = getSubmittedValues(component);
        //Render empty list message
        
        
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

    

    @Override
    protected String getSubmitParam(FacesContext context, UISelectOne selectOne) {
        //Copied from primefaces
        return selectOne.getClientId(context) + "_input";
    }
}
