/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.autoresponder;

/**
 *
 * @author LeeKiatHaw
 */
public enum AUTO_EMAIL_TYPE {
    CONFIRMATION("CONFIRMATION"),
    WELCOME("WELCOME"),
    AUTORESPONDER("AUTORESPONDER");
    
    final String name;
    
    private AUTO_EMAIL_TYPE(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name; //To change body of generated methods, choose Tools | Templates.
    }
    
    public static AUTO_EMAIL_TYPE getType(String name){
        if(name == null ? CONFIRMATION.name() == null : name.equals(CONFIRMATION.name())) 
            return CONFIRMATION;
        if(name == null ? WELCOME.name() == null : name.equals(WELCOME.name())) 
            return WELCOME;
        if(name == null ? AUTORESPONDER.name() == null : name.equals(AUTORESPONDER.name())) 
            return AUTORESPONDER;
        return AUTORESPONDER; //default
    }
}
