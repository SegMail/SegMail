/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.jsf.custom.tooltip;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIOutput;

/**
 *
 * @author LeeKiatHaw
 */
@FacesComponent(TooltipComponent.COMPONENT_TYPE)
public class TooltipComponent extends UIOutput{
    
    public static final String COMPONENT_FAMILY = "Tooltip";
    public static final String COMPONENT_TYPE = "Tooltip";
    
    //Properties
    private static final String FOR = "for";
    private static final String FOR_DEFAULT = "";
}
