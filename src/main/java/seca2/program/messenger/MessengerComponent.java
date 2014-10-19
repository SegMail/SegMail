/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.messenger;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIMessages;

/**
 *
 * @author LeeKiatHaw
 */
@FacesComponent(MessengerComponent.COMPONENT_TYPE)
public class MessengerComponent extends UIMessages {

    public static final String COMPONENT_FAMILY = "Messenger";
    public static final String COMPONENT_TYPE = "Messenger";
    
    //Properties
    private static final String CLOSABLE = "closable";
    private static final Boolean CLOSABLE_DEFAULT = false;

    public MessengerComponent() {
        
    }

    public boolean isClosable() {
        return (Boolean) this.getStateHelper().eval(CLOSABLE, CLOSABLE_DEFAULT);
    }

    public void setClosable(boolean closable) {
        this.getStateHelper().put(CLOSABLE, closable);
    }
    
    
    public String testComponentMethod(){
        return "Test new component";
    }
    
    @Override
    public String getFamily() {
        return MessengerComponent.COMPONENT_FAMILY;
    }
    
}
