/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.jsf.custom.dropdown;

import com.sun.faces.renderkit.Attribute;
import com.sun.faces.renderkit.AttributeManager;
import com.sun.faces.renderkit.html_basic.MenuRenderer;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import seca2.jsf.TagCloser;

/**
 * Is it a good idea to use Primefaces' API? Or customize our own?
 * 
 * @author LeeKiatHaw
 */
@FacesRenderer(componentFamily = DropdownComponent.COMPONENT_FAMILY,
        rendererType = DropdownRenderer.RENDERER_TYPE)
public class DropdownRenderer extends MenuRenderer {

    public static final String RENDERER_TYPE = "seca2.jsf.custom.dropdown.DropdownRenderer";
    
    private TagCloser tagCloser = new TagCloser();
    
    private static final Attribute[] ATTRIBUTES =
          AttributeManager.getAttributes(AttributeManager.Key.SELECTMANYMENU);

    /*
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
        
        //this.encodeChildren(context, uicomponent);
        
    }
    
    */
    
    @Override
    protected void renderSelect(FacesContext context,
                                UIComponent uicomponent) throws IOException {
        
        /*DropdownComponent component = (DropdownComponent) uicomponent;
        //Add the bootstrap color 
        String styleClass = (String) component.getAttributes().get("styleClass");
        styleClass = styleClass.concat("btn-").concat(component.getButtonColor());
        component.setStyleClass(styleClass);*/
        super.renderSelect(context, uicomponent);
    }


    @Override
    public void encodeEnd(FacesContext context, UIComponent uicomponent) throws IOException {
        super.encodeEnd(context, uicomponent); //To change body of generated methods, choose Tools | Templates.
        
        //render the javascript code to convert the select element into a button
        ResponseWriter writer = context.getResponseWriter();
        DropdownComponent component = (DropdownComponent) uicomponent;
        
        String buttonColor = "btn-"+component.getButtonColor();
        
        /*
        writer.startElement("script", component);
        writer.writeText("$(document).ready(function() {\n",component,null);
        writer.writeText("\tloadButton"+component.getId()+"();\n",component,null);
        writer.writeText("});\n",component,null);
        writer.writeText("function loadButton"+component.getId()+"() {\n",component,null);
        writer.writeText("$('#"+component.getNamingContainer().getParent().getClientId()+"\\\\:"
                + component.getId() + "')"
                + ".selectpicker({style: 'btn btn-wide "
                + buttonColor
                + "', "
                + "menuStyle: 'dropdown-inverse'});\n",
                component,null);
        writer.writeText("};\n",component,null);
        writer.endElement("script");
        */
        writer.startElement("script", component);
        writer.writeText("$(document).ready(function() {\n",component,null);
        writer.writeText("$('#"+component.getNamingContainer().getParent().getClientId()+"\\\\:"
                + component.getId() + "')"
                + ".selectpicker({style: 'btn btn-wide "
                + buttonColor
                + "', "
                + "menuStyle: 'dropdown-inverse'});\n",
                component,null);
        writer.writeText("});\n",component,null);
        writer.endElement("script");
    }
    
    
    
    
}
