/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.jsf.custom.dropdown;

import com.sun.faces.io.FastStringWriter;
import com.sun.faces.renderkit.Attribute;
import com.sun.faces.renderkit.AttributeManager;
import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.SelectItemsIterator;
import com.sun.faces.renderkit.html_basic.MenuRenderer;
import com.sun.faces.util.RequestStateManager;
import com.sun.faces.util.Util;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
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
    protected void renderSelect(FacesContext context, UIComponent uicomponent) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        DropdownComponent component = (DropdownComponent) uicomponent;
        
        assert(writer != null);

        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Rendering 'select'");
        }
        
        //writer.startElement("select", component); 
        writer.startElement("div", component);
        this.tagCloser.openTag("div");
        writeIdAttributeIfNecessary(context, writer, component);
        writer.writeAttribute("class", component.getDropdownClass(), null);
        
        
        writer.writeAttribute("name", component.getClientId(context),
                              "clientId");
        // render styleClass attribute if present.
        String styleClass;
        if (null !=
            (styleClass =
                  (String) component.getAttributes().get("styleClass"))) {
            writer.writeAttribute("class", styleClass, "styleClass");
        }
        if (!getMultipleText(component).equals("")) {
            writer.writeAttribute("multiple", true, "multiple");
        }
        
        //Render button appearance
        writer.startElement("button", component);
        this.tagCloser.openTag("button");
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

        // Determine how many option(s) we need to render, and update
        // the component's "size" attribute accordingly;  The "size"
        // attribute will be rendered as one of the "pass thru" attributes
        SelectItemsIterator<SelectItem> items = RenderKitUtils.getSelectItems(context, component);

        // render the options to a buffer now so that we can determine
        // the size
        FastStringWriter bufferedWriter = new FastStringWriter(128);
        context.setResponseWriter(writer.cloneWithWriter(bufferedWriter));
        int count = renderOptions(context, component, items);
        context.setResponseWriter(writer);
        // If "size" is *not* set explicitly, we have to default it correctly
        Integer size = (Integer) component.getAttributes().get("size");
        if (size == null || size == Integer.MIN_VALUE) {
            size = count;
        }
        writeDefaultSize(writer, size);

        RenderKitUtils.renderPassThruAttributes(context,
                                                writer,
                                                component,
                                                ATTRIBUTES,
                                                getNonOnChangeBehaviors(component));
        RenderKitUtils.renderXHTMLStyleBooleanAttributes(writer,
                                                         component);

        RenderKitUtils.renderOnchange(context, component, false);

        // Now, write the buffered option content
        writer.write(bufferedWriter.toString());
        
        this.tagCloser.closeAllRemainingTags(writer);
    }

    @Override
    protected int renderOptions(FacesContext context, UIComponent component, SelectItemsIterator<SelectItem> items) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        assert(writer != null);

        Converter converter = null;
        if(component instanceof ValueHolder) {
            converter = ((ValueHolder)component).getConverter();
        }
        int count = 0;
        Object currentSelections = getCurrentSelectedValues(component);
        Object[] submittedValues = getSubmittedSelectedValues(component);
        Map<String,Object> attributes = component.getAttributes();
        boolean componentDisabled = Util.componentIsDisabled(component);

        OptionComponentInfo optionInfo =
              new OptionComponentInfo((String) attributes.get("disabledClass"),
                                      (String) attributes.get("enabledClass"),
                                      componentDisabled,
                                      isHideNoSelection(component));
        RequestStateManager.set(context,
                                RequestStateManager.TARGET_COMPONENT_ATTRIBUTE_NAME,
                                component);
        while (items.hasNext()) {
            SelectItem item = items.next();
            UIComponent selectComponent = items.currentSelectComponent();

            if (item instanceof SelectItemGroup) {
                // render OPTGROUP
                writer.startElement("optgroup", (null != selectComponent) ? selectComponent : component);
                writer.writeAttribute("label", item.getLabel(), "label");

                // if the component is disabled, "disabled" attribute would be rendered
                // on "select" tag, so don't render "disabled" on every option.
                if ((!componentDisabled) && item.isDisabled()) {
                    writer.writeAttribute("disabled", true, "disabled");
                }
                count++;
                // render options of this group.
                SelectItem[] itemsArray =
                      ((SelectItemGroup) item).getSelectItems();
                for (int i = 0; i < itemsArray.length; ++i) {
                    if (renderOption(context,
                                     component,
                                     selectComponent,
                                     converter,
                                     itemsArray[i],
                                     currentSelections,
                                     submittedValues,
                                     optionInfo)) {
                        count++;
                    }
                }
                writer.endElement("optgroup");
            } else {
                if (renderOption(context,
                                 component,
                                 selectComponent,
                                 converter,
                                 item,
                                 currentSelections,
                                 submittedValues,
                                 optionInfo)) {
                    count ++;
                }
            }
        }

        return count;

    }

    @Override
    protected boolean renderOption(FacesContext context, UIComponent component, UIComponent selectComponent, Converter converter, SelectItem curItem, Object currentSelections, Object[] submittedValues, OptionComponentInfo optionInfo) throws IOException {
        Object valuesArray;
        Object itemValue;
        String valueString = getFormattedValue(context, component,
                                               curItem.getValue(), converter);
        boolean containsValue;
        if (submittedValues != null) {
            containsValue = containsaValue(submittedValues);
            if (containsValue) {
                valuesArray = submittedValues;
                itemValue = valueString;
            } else {
                valuesArray = currentSelections;
                itemValue = curItem.getValue();
            }
        } else {
            valuesArray = currentSelections;
            itemValue = curItem.getValue();
        }

        boolean isSelected = isSelected(context, component, itemValue, valuesArray, converter);
        if (optionInfo.isHideNoSelection()
                && curItem.isNoSelectionOption()
                && currentSelections != null
                && !isSelected) {
            return false;
        }

        ResponseWriter writer = context.getResponseWriter();
        assert (writer != null);
        writer.writeText("\t", component, null);
        writer.startElement("option", (null != selectComponent) ? selectComponent : component);
        writer.writeAttribute("value", valueString, "value");

        if (isSelected) {
            writer.writeAttribute("selected", true, "selected");
        }

        // if the component is disabled, "disabled" attribute would be rendered
        // on "select" tag, so don't render "disabled" on every option.
        if ((!optionInfo.isDisabled()) && curItem.isDisabled()) {
            writer.writeAttribute("disabled", true, "disabled");
        }

        String labelClass;
        if (optionInfo.isDisabled() || curItem.isDisabled()) {
            labelClass = optionInfo.getDisabledClass();
        } else {
            labelClass = optionInfo.getEnabledClass();
        }
        if (labelClass != null) {
            writer.writeAttribute("class", labelClass, "labelClass");
        }

        if (curItem.isEscape()) {
            String label = curItem.getLabel();
            if (label == null) {
                label = valueString;
            }
            writer.writeText(label, component, "label");
        } else {
            writer.write(curItem.getLabel());
        }
        writer.endElement("option");
        writer.writeText("\n", component, null);
        return true;
    }
    
    
    /*
    @Override
    public void encodeChildren(FacesContext context, UIComponent uicomponent) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        DropdownComponent component = (DropdownComponent) uicomponent;
        
        //This is to retrieve all child components declared in the xhtml page
        //List<SelectItem> listItems = this.getSelectItems(context, component);
        //This is to retrieve the value that has been tagged to this component, usually a List object
        //Object values = this.getValues(component);
        //Object submittedValues = getSubmittedValues(component);
        //Render empty list message
        
        List<UIComponent> children = component.getChildren();
        super.encodeChildren(context, uicomponent);
        
        System.out.println("Test encode children");
        
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
    */

    @Override
    public boolean getRendersChildren() {
        return true;
    }
    
    
}
