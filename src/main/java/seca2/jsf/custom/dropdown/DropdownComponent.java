/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.jsf.custom.dropdown;

import java.io.IOException;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UINamingContainer;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;

/**
 * It seems better to use a composite component than a custom one, since the 
 * components are already available - h:selectOneMenu, f:selectItem, etc.
 * @author LeeKiatHaw
 */
@FacesComponent(DropdownComponent.COMPONENT_TYPE)
public class DropdownComponent extends HtmlSelectOneMenu implements NamingContainer {

    public static final String COMPONENT_FAMILY = "Dropdown";
    public static final String COMPONENT_TYPE = "Dropdown";
    
    //Properties
    public static final String EMPTY_MESSAGE = "emptyMessage";
    public static final String EMPTY_MESSAGE_DEFAULT = "No Item exists yet";
    
    private String styleClass;

    @Override
    public String getFamily() {
        return UINamingContainer.COMPONENT_FAMILY; 
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        
        super.encodeBegin(context); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
