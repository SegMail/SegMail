/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription.email;

/**
 *
 * @author LeeKiatHaw
 */
public class AutoEmailTypeFactory {
    
    public static enum TYPE{
        CONFIRMATION,
        WELCOME,
        AUTORESPONDER
    }
    
    public static AutoresponderEmail getAutoEmailTypeInstance(TYPE type){
        switch(type){
            case CONFIRMATION   :   return new AutoConfirmEmail();
            case WELCOME     :   return new AutoWelcomeEmail();
            default             :   return null;
        }
    }
    
    public static Class<? extends AutoresponderEmail> getAutoEmailTypeClass(TYPE type){
        switch(type){
            case CONFIRMATION   :   return AutoConfirmEmail.class;
            case WELCOME     :   return AutoWelcomeEmail.class;
            default             :   throw new RuntimeException("EmailTemplateFactory cannot identify the type "+type.toString());
        }
    }
}
